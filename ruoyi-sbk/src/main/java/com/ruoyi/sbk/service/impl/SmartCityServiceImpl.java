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
import com.ruoyi.sbk.domain.WxSmspersonEms;
import com.ruoyi.sbk.dto.ZkjdcxParam;
import com.ruoyi.sbk.mapper.WxArchivesMapper;
import com.ruoyi.sbk.mapper.WxBukaInfoImgMapper;
import com.ruoyi.sbk.mapper.WxBukaInfoMapper;
import com.ruoyi.sbk.mapper.WxInfomationImgMapper;
import com.ruoyi.sbk.service.IWxArchivesService;
import com.ruoyi.sbk.service.IWxBukaInfoService;
import com.ruoyi.sbk.service.SmartCityService;
import com.ruoyi.sbk.service.WxSmspersonEmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class SmartCityServiceImpl implements SmartCityService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private WxArchivesMapper wxArchivesMapper;
    @Autowired
    private WxBukaInfoMapper wxBukaInfoMapper;
    @Autowired
    private IWxBukaInfoService wxBukaInfoService;
    @Autowired
    private WxBukaInfoImgMapper wxBukaInfoImgMapper;
    @Autowired
    private WxInfomationImgMapper wxInfomationImgMapper;
    @Autowired
    private WxSmspersonEmsService wxSmspersonEmsService;
    @Autowired
    private IWxArchivesService wxArchivesService;
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

    @Override
    public Map<String, Object> getShenLingData(ZkjdcxParam zkjdcxParam, String state) {
        // shenling
        Map<String, Object> shenlingMap = new HashMap<>();
        // shenling.data 所有数据
        List<Map<String, Object>> resultList = new ArrayList<>();
        // shenling.data 德生数据
        List<Map<String, Object>> mapList = new ArrayList<>();

        String newCardCode1 = "1、制卡信息采集已审核通过（审核通过后五个工作日完成制卡，请耐心等待）。";
        String newCardCode2 = "2、正在写入社保信息，请耐心等待";
        String newCardCode3 = "3、制卡成功，待邮寄";
        String newCardCode4 = "4、已邮寄";
        switch (state) {
            case "32": {
                Map<String, Object> map1 = new HashMap<>();
                map1.put("flag", 1);
                map1.put("info", newCardCode1);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("flag", 1);
                map2.put("info", newCardCode2);
                Map<String, Object> map3 = new HashMap<>();
                map3.put("flag", 1);
                map3.put("info", newCardCode3);
                Map<String, Object> map4 = new HashMap<>();
                map4.put("flag", 0);
                map4.put("info", newCardCode4);
//                Map<String, Object> map5 = new HashMap<>();
//                map5.put("flag", 1);
//                map5.put("info", "5、个人已领取社保卡");

                map1.put("time_flag", 0);
                map2.put("time_flag", 0);
                map3.put("time_flag", 0);
                map4.put("time_flag", 0);
//                map5.put("time_flag", 0);
                mapList.add(map1);
                mapList.add(map2);
                mapList.add(map3);
                mapList.add(map4);
//                mapList.add(map5);
                break;
            }
            case "33": {
                Map<String, Object> map1 = new HashMap<>();
                map1.put("flag", 1);
                map1.put("info", newCardCode1);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("flag", 1);
                map2.put("info", newCardCode2);
                Map<String, Object> map3 = new HashMap<>();
                map3.put("flag", 1);
                map3.put("info", newCardCode3);
                Map<String, Object> map4 = new HashMap<>();
                map4.put("flag", 0);
                map4.put("info", newCardCode4);
//                Map<String, Object> map5 = new HashMap<>();
//                map5.put("flag", 1);
//                map5.put("info", "5、单位已领取社保卡");

                map1.put("time_flag", 0);
                map2.put("time_flag", 0);
                map3.put("time_flag", 0);
                map4.put("time_flag", 0);
//                map5.put("time_flag", 0);
                mapList.add(map1);
                mapList.add(map2);
                mapList.add(map3);
                mapList.add(map4);
//                mapList.add(map5);
                break;
            }
            case "11": {
                Map<String, Object> map1 = new HashMap<>();
                map1.put("flag", 1);
                map1.put("info", newCardCode1);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("flag", 0);
                map2.put("info", newCardCode2);
                Map<String, Object> map3 = new HashMap<>();
                map3.put("flag", 0);
                map3.put("info", newCardCode3);
                Map<String, Object> map4 = new HashMap<>();
                map4.put("flag", 0);
                map4.put("info", newCardCode4);
//                Map<String, Object> map5 = new HashMap<>();
//                map5.put("flag", 0);
//                map5.put("info", "5、单位或者个人已领取社保卡");

                map1.put("time_flag", 0);
                map2.put("time_flag", 0);
                map3.put("time_flag", 0);
                map4.put("time_flag", 0);
//                map5.put("time_flag", 0);
                mapList.add(map1);
                mapList.add(map2);
                mapList.add(map3);
                mapList.add(map4);
//                mapList.add(map5);
                break;
            }
            default: {
                Map<String, Object> map1 = new HashMap<>();
                map1.put("flag", 0);
                map1.put("info", newCardCode1);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("flag", 0);
                map2.put("info", newCardCode2);
                Map<String, Object> map3 = new HashMap<>();
                map3.put("flag", 0);
                map3.put("info", newCardCode3);
                Map<String, Object> map4 = new HashMap<>();
                map4.put("flag", 0);
                map4.put("info", newCardCode4);
//                Map<String, Object> map5 = new HashMap<>();
//                map5.put("flag", 0);
//                map5.put("info", "5、单位或者个人已领取社保卡");

                map1.put("time_flag", 0);
                map2.put("time_flag", 0);
                map3.put("time_flag", 0);
                map4.put("time_flag", 0);
//                map5.put("time_flag", 0);
                mapList.add(map1);
                mapList.add(map2);
                mapList.add(map3);
                mapList.add(map4);
//                mapList.add(map5);
                break;
            }
        }


        WxArchives wxArchives = wxArchivesService.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxArchives>().eq(WxArchives::getCardNum, zkjdcxParam.getSfzh()).eq(WxArchives::getStepStatus, 9).in(WxArchives::getExamineStatus, "0", "1", "2"));
        if (wxArchives != null) {

            shenlingMap.put("shijian", wxArchives.getAddTime());
            if (wxArchives.getSource().equals("1")) {
                shenlingMap.put("qudao", "微信公众号");
            } else if (wxArchives.getSource().equals("4")) {
                shenlingMap.put("qudao", "辛集审批局");
            }

            switch (wxArchives.getIsMail()) {
                case "1":
                    wxArchives.setIsMail("邮寄到家");
                    break;
                case "0":
                    wxArchives.setIsMail("网点领取");
                    mapList.remove(3);
                    mapList.get(0).put("info", "1、制卡信息采集已审核通过（请到指定网点领取社保卡）。");
                    mapList.get(2).put("info", "3、制卡成功，已领取社保卡");
                    break;
                case "2":
                    wxArchives.setIsMail("银行网点领取");
                    mapList.remove(3);
                    mapList.get(0).put("info", "1、制卡信息采集已审核通过（请到指定网点领取社保卡）。");
                    mapList.get(2).put("info", "3、制卡成功，已领取社保卡");
                    break;
            }
            if (wxArchives.getIsMail().equals("邮寄到家")) {
                LambdaQueryWrapper<WxSmspersonEms> queryWrapper = new LambdaQueryWrapper<WxSmspersonEms>()
                        .like(WxSmspersonEms::getIdcard, wxArchives.getCardNum())
                        .ge(WxSmspersonEms::getImporttime, wxArchives.getExamineTime());
                WxSmspersonEms wxSmspersonEms = wxSmspersonEmsService.selectOneByLambdaQueryWrapper(queryWrapper);
                if (wxSmspersonEms == null) {
                    mapList.get(3).put("flag", 0);
                } else {
                    mapList.get(3).put("flag", 1);
                    mapList.get(3).put("mailnum", wxSmspersonEms.getMailnum());
                }
            }


            Map<String, Object> mapa = new HashMap<>();
            mapa.put("flag", 1);
            mapa.put("add_time", wxArchives.getAddTime());
            mapa.put("nickname", wxArchives.getName());
            mapa.put("is_mail", wxArchives.getIsMail());

            Map<String, Object> mapb = new HashMap<>();
            switch (wxArchives.getExamineStatus()) {
                case "0":
                    mapb.put("flag", 0);
                    mapb.put("status", 0);
                    mapb.put("msg", "未初审");
                    break;
                case "1":
                    mapb.put("flag", 1);
                    mapb.put("status", 1);
                    mapb.put("examine_time", wxArchives.getExamineTime());
                    mapb.put("msg", "初审通过");
                    break;
                case "2":
                    mapb.put("flag", 2);
                    mapb.put("status", 1);
                    mapb.put("examine_time", wxArchives.getExamineTime());
                    mapb.put("msg", "初审驳回。驳回原因：" + wxArchives.getReason() + "。");
                    break;
            }

            Map<String, Object> mapc = new HashMap<>();
            if ("2".equals(wxArchives.getIsJpg())) {
                mapc.put("flag", 1);
                mapc.put("msg", "已导出");
                mapc.put("daochu_time", wxArchives.getJpgAddTime());
            } else {
                if (wxArchives.getIsMail().equals("网点领取") && wxArchives.getExamineStatus().equals("1")) {
                    mapc.put("flag", 1);
                    mapc.put("msg", "已导出");
                    mapc.put("daochu_time", wxArchives.getJpgAddTime());
                } else {
                    mapc.put("flag", 0);
                    mapc.put("msg", "未导出");
                }
            }

            String source = "【石家庄】";
            switch (wxArchives.getSource()) {
                case "6":
                    source = "【赞皇】";
                    break;
                case "7":
                    source = "【鹿泉】";
                    break;
                case "8":
                    source = "【灵寿】";
                    break;
            }
            mapa.put("info", "A、" + source + "首次制卡信息提交成功");
            mapb.put("info", "B、" + source + "制卡信息网上初审");
            mapc.put("info", "C、" + source + "制卡信息导出到省级制卡平台");

            resultList.add(mapa);
            resultList.add(mapb);
            resultList.add(mapc);


        } else {
            mapList.remove(3);
        }
        resultList.addAll(mapList);

        shenlingMap.put("data", resultList);
        return shenlingMap;
    }

    @Override
    public List<Map<String, Object>> getBuHuanKaData(ZkjdcxParam zkjdcxParam, String state) {
        List<Map<String, Object>> buhuankaList = new ArrayList<>();

        List<WxBukaInfo> wxBukaInfoList = wxBukaInfoService.selectListByLambdaQueryWrapper(new LambdaQueryWrapper<WxBukaInfo>().eq(WxBukaInfo::getIdcardno, zkjdcxParam.getSfzh()).eq(WxBukaInfo::getStepStatus, 9).in(WxBukaInfo::getExamineStatus, "0", "1", "2").orderByDesc(WxBukaInfo::getId));
        for (WxBukaInfo wxBukaInfo : wxBukaInfoList) {
            // buhuanka
            Map<String, Object> buhuankaMap = new HashMap<>();
            buhuankaMap.put("shijian", wxBukaInfo.getAddTime());
            if (wxBukaInfo.getSource() == 1) {
                buhuankaMap.put("qudao", "微信公众号");
            } else if (wxBukaInfo.getSource() == 4) {
                buhuankaMap.put("qudao", "辛集审批局");
            }
            // shenling.data 所有数据
            List<Map<String, Object>> resultList = new ArrayList<>();
            // shenling.data 德生数据
            List<Map<String, Object>> mapList = new ArrayList<>();

            Map<String, Object> mapa = new HashMap<>();
            mapa.put("flag", 1);
            mapa.put("add_time", wxBukaInfo.getAddTime());
            mapa.put("nickname", wxBukaInfo.getKaName());
            mapa.put("is_mail", "邮寄到家");

            Map<String, Object> mapb = new HashMap<>();
            switch (wxBukaInfo.getExamineStatus()) {
                case 0:
                    mapb.put("flag", 0);
                    mapb.put("status", 0);
                    mapb.put("msg", "未初审");
                    break;
                case 1:
                    mapb.put("flag", 1);
                    mapb.put("status", 1);
                    mapb.put("examine_time", wxBukaInfo.getExamineTime());
                    mapb.put("msg", "初审通过");
                    break;
                case 2:
                    mapb.put("flag", 2);
                    mapb.put("status", 1);
                    mapb.put("examine_time", wxBukaInfo.getExamineTime());
                    mapb.put("msg", "初审驳回。驳回原因：" + wxBukaInfo.getRejectReason() + "。");
                    break;
            }

            Map<String, Object> mapc = new HashMap<>();
            if (wxBukaInfo.getIsJpg() == 2) {
                mapc.put("flag", 1);
                mapc.put("msg", "已导出");
                mapc.put("daochu_time", wxBukaInfo.getJpgAddTime());
            } else {
                if (wxBukaInfo.getExamineStatus() == 1) {
                    mapc.put("flag", 1);
                    mapc.put("msg", "已导出");
                    mapc.put("daochu_time", wxBukaInfo.getJpgAddTime());
                } else {
                    mapc.put("flag", 0);
                    mapc.put("msg", "未导出");
                }
            }

            String source = "【石家庄】";
            switch (wxBukaInfo.getSource()) {
                case 6:
                    source = "【赞皇】";
                    break;
                case 7:
                    source = "【鹿泉】";
                    break;
                case 8:
                    source = "【灵寿】";
                    break;
            }
            mapa.put("info", "A、" + source + "补换卡信息提交成功");
            mapb.put("info", "B、" + source + "制卡信息网上初审");
            mapc.put("info", "C、" + source + "制卡信息导出到省级制卡平台");

            resultList.add(mapa);
            resultList.add(mapb);
            resultList.add(mapc);

            if (mapc.get("flag").toString().equals("1")) {
                String oldCardCode1 = "1、制卡信息采集已审核通过（工作日当天12点前审核通过下午寄出，12点后审核通过第二个工作日寄出）。";
                String oldCardCode2 = "2、正在写入社保信息，请耐心等待";
                String oldCardCode3 = "3、制卡成功，待邮寄";
                switch (state) {
                    case "32":
                    case "33": {
                        Map<String, Object> map1 = new HashMap<>();
                        map1.put("flag", 1);
                        map1.put("info", oldCardCode1);
                        Map<String, Object> map2 = new HashMap<>();
                        map2.put("flag", 1);
                        map2.put("info", oldCardCode2);
                        Map<String, Object> map3 = new HashMap<>();
                        map3.put("flag", 1);
                        map3.put("info", oldCardCode3);

                        map1.put("time_flag", 0);
                        map2.put("time_flag", 0);
                        map3.put("time_flag", 0);
                        mapList.add(map1);
                        mapList.add(map2);
                        mapList.add(map3);
                        break;
                    }
                    case "11": {
                        Map<String, Object> map1 = new HashMap<>();
                        map1.put("flag", 1);
                        map1.put("info", oldCardCode1);
                        Map<String, Object> map2 = new HashMap<>();
                        map2.put("flag", 0);
                        map2.put("info", oldCardCode2);
                        Map<String, Object> map3 = new HashMap<>();
                        map3.put("flag", 0);
                        map3.put("info", oldCardCode3);

                        map1.put("time_flag", 0);
                        map2.put("time_flag", 0);
                        map3.put("time_flag", 0);
                        mapList.add(map1);
                        mapList.add(map2);
                        mapList.add(map3);
                        break;
                    }
                    default: {
                        Map<String, Object> map1 = new HashMap<>();
                        map1.put("flag", 0);
                        map1.put("info", oldCardCode1);
                        Map<String, Object> map2 = new HashMap<>();
                        map2.put("flag", 0);
                        map2.put("info", oldCardCode2);
                        Map<String, Object> map3 = new HashMap<>();
                        map3.put("flag", 0);
                        map3.put("info", oldCardCode3);

                        map1.put("time_flag", 0);
                        map2.put("time_flag", 0);
                        map3.put("time_flag", 0);
                        mapList.add(map1);
                        mapList.add(map2);
                        mapList.add(map3);
                        break;
                    }
                }
                Map<String, Object> map4 = new HashMap<>();
                map4.put("info", "4、已邮寄");
                LambdaQueryWrapper<WxSmspersonEms> queryWrapper = new LambdaQueryWrapper<WxSmspersonEms>()
                        .like(WxSmspersonEms::getIdcard, wxBukaInfo.getIdcardno())
                        .ge(WxSmspersonEms::getImporttime, wxBukaInfo.getExamineTime());
                WxSmspersonEms wxSmspersonEms = wxSmspersonEmsService.selectOneByLambdaQueryWrapper(queryWrapper);
                if (wxSmspersonEms == null) {
                    map4.put("flag", 0);
                } else {
                    map4.put("flag", 1);
                    map4.put("mailnum", wxSmspersonEms.getMailnum());
                }


//                Map<String, Object> map5 = new HashMap<>();
//                map5.put("info", "个人已领取社保卡");
//                if (wxBukaInfo.getMailStatus() == 2 || wxBukaInfo.getMailStatus() == 3) {
//                    map5.put("flag", 1);
//                } else {
//                    map5.put("flag", 0);
//                }

                map4.put("time_flag", 0);
//                map5.put("time_flag", 0);

                mapList.add(map4);
//                mapList.add(map5);

                resultList.addAll(mapList);
            }

            buhuankaMap.put("data", resultList);

            buhuankaList.add(buhuankaMap);
        }
        return buhuankaList;
    }
}
