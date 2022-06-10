package com.ruoyi.sbk.service.impl;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.sbk.domain.WxArchives;
import com.ruoyi.sbk.mapper.WxArchivesMapper;
import com.ruoyi.sbk.mapper.WxInfomationImgMapper;
import com.ruoyi.sbk.service.SmartCityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class SmartCityServiceImpl implements SmartCityService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private WxArchivesMapper wxArchivesMapper;
    @Autowired
    private WxInfomationImgMapper wxInfomationImgMapper;

    @Override
    public JSONObject selectMailInfoByWldh(String wldh) {
        AES aes = SecureUtil.aes("94DA411B9C39B410".getBytes());
        String url = "http://ipps.hbwkd.cn/ipps/orderPay/api/EmsTrail/EmsTrailAction.do?actionType=getMailInfo";
//        String url = "http://10.36.2.8:9007/emsipps/ipps/orderPay/api/EmsTrail/EmsTrailAction.do?actionType=getMailInfo";
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("AUTH_CODE", "94D5C8EAA808A9A5E050007F01005B3C");
        hashMap.put("CUST_APPID", "wxde85bc4bf1f7629a");
        hashMap.put("MAIL_NUM", wldh);
        String content = aes.encryptBase64(JSON.toJSONString(hashMap));
        String result = restTemplate.postForObject(url, content, String.class);
        if (result == null) {
            throw new ServiceException("没有查到物流信息");
        }
        result = aes.decryptStr(result.replaceAll("\\r\\n", ""));
        return JSON.parseObject(result);
    }

    @Override
    @Transactional
    @DataSource(value = DataSourceType.SLAVE)
    public void saveArchivesAndImg(WxArchives wxArchives) {
        Long count = wxArchivesMapper.selectCount(new LambdaQueryWrapper<WxArchives>()
                .eq(WxArchives::getCardNum, wxArchives.getCardNum())
                .eq(WxArchives::getName, wxArchives.getName()));
        if (count > 0) {
            throw new ServiceException("采集信息已存在");
        }
        wxArchivesMapper.insert(wxArchives);
        wxInfomationImgMapper.insert(wxArchives.getWxInfomationImg());
    }
}
