package com.ruoyi.sbk.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.sbk.domain.WxArchives;
import com.ruoyi.sbk.domain.WxBukaInfo;
import com.ruoyi.sbk.mapper.WxArchivesMapper;
import com.ruoyi.sbk.mapper.WxBukaInfoImgMapper;
import com.ruoyi.sbk.mapper.WxBukaInfoMapper;
import com.ruoyi.sbk.mapper.WxInfomationImgMapper;
import com.ruoyi.sbk.service.SmartCityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SmartCityServiceImpl implements SmartCityService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private WxArchivesMapper wxArchivesMapper;
    @Autowired
    private WxBukaInfoMapper wxBukaInfoMapper;
    @Autowired
    private WxBukaInfoImgMapper wxBukaInfoImgMapper;
    @Autowired
    private WxInfomationImgMapper wxInfomationImgMapper;
    //    private final String url = "http://dingzhou.sjzydrj.net/index.php/Home/Orderpayapi/put_orderinfo";
    private final String url = "http://10.39.248.217:9904/orderpayapi/put_orderinfo";

    @Override
    public JSONObject selectMailInfoByWldh(String wldh) {
        AES aes = SecureUtil.aes("94DA411B9C39B410".getBytes());
//        String url = "http://ipps.hbwkd.cn/ipps/orderPay/api/EmsTrail/EmsTrailAction.do?actionType=getMailInfo";
        String url = "http://10.36.2.8:9007/emsipps/ipps/orderPay/api/EmsTrail/EmsTrailAction.do?actionType=getMailInfo";
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("AUTH_CODE", "94D5C8EAA808A9A5E050007F01005B3C");
        hashMap.put("CUST_APPID", "wxde85bc4bf1f7629a");
        hashMap.put("MAIL_NUM", wldh);
        String content = aes.encryptBase64(JSON.toJSONString(hashMap));
        String result = restTemplate.postForObject(url, content, String.class);
        if (StrUtil.isEmpty(result)) {
            throw new ServiceException("错误");
        }
        result = aes.decryptStr(result.replaceAll("\\r\\n", ""));
        return JSON.parseObject(result);
    }

    @Override
    @Transactional
    @DataSource(value = DataSourceType.SLAVE)
    public void saveArchivesAndImg(WxArchives wxArchives) {
        LambdaQueryWrapper<WxArchives> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WxArchives::getCardNum, wxArchives.getCardNum());
//        queryWrapper.eq(WxArchives::getExamineStatus, "0");
        Long count = wxArchivesMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new ServiceException("采集信息已存在");
//            LambdaUpdateWrapper<WxArchives> wxArchivesLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
//            wxArchivesLambdaUpdateWrapper.eq(WxArchives::getCardNum, wxArchives.getCardNum());
//            wxArchivesMapper.update(wxArchives, wxArchivesLambdaUpdateWrapper);
//            LambdaUpdateWrapper<WxInfomationImg> wxInfomationImgLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
//            wxInfomationImgLambdaUpdateWrapper.eq(WxInfomationImg::getCardNum, wxArchives.getCardNum());
//            wxInfomationImgMapper.update(wxArchives.getWxInfomationImg(), wxInfomationImgLambdaUpdateWrapper);
        }
        wxArchivesMapper.insert(wxArchives);
        wxInfomationImgMapper.insert(wxArchives.getWxInfomationImg());
    }

    @Override
    @Transactional
    @DataSource(value = DataSourceType.SLAVE)
    public void updateArchivesAndImg(WxArchives wxArchives) {
        wxArchivesMapper.updateById(wxArchives);
        wxInfomationImgMapper.updateById(wxArchives.getWxInfomationImg());
    }

    @Override
    public JSONObject putOrderinfo(WxArchives wxArchives, Integer mailPrice) {
        AES aes = SecureUtil.aes("3MH0P00OPS3OOROE".getBytes());
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("auth_code", "TWXUCQSOMT48MXWWURVTWGBI5PSSNN76");
        hashMap.put("cust_appid", "wxde85bc4bf1f7629a");
        hashMap.put("cust_order_code", wxArchives.getOrderno());
        hashMap.put("shou_date", DateUtil.format(new Date(), "yyyyMMdd"));
        String[] communicationAddresss = wxArchives.getCommunicationAddress().split("/");
        hashMap.put("shou_address_prov", communicationAddresss[0]);
        hashMap.put("shou_address_city", communicationAddresss[1]);
        hashMap.put("shou_address_coun", communicationAddresss[2]);
        hashMap.put("shou_detailed_address", wxArchives.getDetailedAddress());
        hashMap.put("shou_zoon_code", wxArchives.getCountyCode());
        hashMap.put("shou_phone", wxArchives.getPhone());
        hashMap.put("shou_idcardno", wxArchives.getCardNum());
        hashMap.put("cost_fee", mailPrice * 100);
        hashMap.put("ordercode", 1); // 1:社保卡申领 2:补换卡
        String content = aes.encryptBase64(JSON.toJSONString(hashMap));
        String result = restTemplate.postForObject(url, content, String.class);
        if (StrUtil.isEmpty(result)) {
            throw new ServiceException("错误");
        }
        result = aes.decryptStr(result.replaceAll("\"", ""));
        return JSON.parseObject(result);
    }

    @Override
    public JSONObject putOrderinfo(WxBukaInfo wxBukaInfo, Integer mailPrice) {
        AES aes = SecureUtil.aes("3MH0P00OPS3OOROE".getBytes());
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("auth_code", "0KRMYPWR3JF1BTY88TGCIEVRCSHSM7VF");
        hashMap.put("cust_appid", "wxde85bc4bf1f7629a");
        hashMap.put("cust_order_code", wxBukaInfo.getOrderno());
        hashMap.put("shou_date", DateUtil.format(new Date(), "yyyyMMdd"));
        hashMap.put("shou_address_prov", wxBukaInfo.getShouAddressProv());
        hashMap.put("shou_address_city", wxBukaInfo.getShouAddressCity());
        hashMap.put("shou_address_coun", wxBukaInfo.getShouAddressCoun());
        hashMap.put("shou_detailed_address", wxBukaInfo.getShouDetailedAddress());
        hashMap.put("shou_zoon_code", wxBukaInfo.getShouZoonCode());
        hashMap.put("shou_phone", wxBukaInfo.getShouPhone());
        hashMap.put("shou_idcardno", wxBukaInfo.getIdcardno());
        hashMap.put("cost_fee", mailPrice * 100);
        hashMap.put("ordercode", 2); // 1:社保卡申领 2:补换卡
        String content = aes.encryptBase64(JSON.toJSONString(hashMap));
        String result = restTemplate.postForObject(url, content, String.class);
        if (StrUtil.isEmpty(result)) {
            throw new ServiceException("错误");
        }
        result = aes.decryptStr(result.replaceAll("\"", ""));
        return JSON.parseObject(result);
    }

    @Override
    @Transactional
    @DataSource(value = DataSourceType.SLAVE)
    public void saveBukaInfoAndImg(WxBukaInfo wxBukaInfo) {
        LambdaQueryWrapper<WxBukaInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WxBukaInfo::getIdcardno, wxBukaInfo.getIdcardno());
        queryWrapper.eq(WxBukaInfo::getStepStatus, 9);
        queryWrapper.eq(WxBukaInfo::getExamineStatus, 0);
        Long count = wxBukaInfoMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new ServiceException("采集信息已存在");
//            WxBukaInfo wxBukaInfo1 = wxBukaInfoMapper.selectOne(queryWrapper);
//            LambdaUpdateWrapper<WxBukaInfo> wxBukaInfoLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
//            wxBukaInfoLambdaUpdateWrapper.eq(WxBukaInfo::getOrderno, wxBukaInfo1.getOrderno());
//            wxBukaInfoMapper.update(wxBukaInfo, wxBukaInfoLambdaUpdateWrapper);
//            LambdaUpdateWrapper<WxBukaInfoImg> wxBukaInfoImgLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
//            wxBukaInfoImgLambdaUpdateWrapper.eq(WxBukaInfoImg::getOrderno, wxBukaInfo1.getOrderno());
//            wxBukaInfoImgMapper.update(wxBukaInfo.getWxBukaInfoImg(), wxBukaInfoImgLambdaUpdateWrapper);
        }
        wxBukaInfoMapper.insert(wxBukaInfo);
        wxBukaInfoImgMapper.insert(wxBukaInfo.getWxBukaInfoImg());
    }

    @Override
    @Transactional
    @DataSource(value = DataSourceType.SLAVE)
    public void updateBukaInfoAndImg(WxBukaInfo wxBukaInfo) {
        wxBukaInfoMapper.updateById(wxBukaInfo);
        wxBukaInfoImgMapper.updateById(wxBukaInfo.getWxBukaInfoImg());
    }
}
