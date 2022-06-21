package com.ruoyi.sbk.service;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.sbk.domain.WxArchives;
import com.ruoyi.sbk.domain.WxBukaInfo;
import com.ruoyi.sbk.dto.ZkjdcxParam;

import java.util.List;
import java.util.Map;

public interface SmartCityService {
    JSONObject selectMailInfoByWldh(String wldh);

    void saveArchivesAndImg(WxArchives wxArchives);

    void updateArchivesAndImg(WxArchives wxArchives);

    JSONObject putOrderinfo(WxArchives wxArchives, Integer mailPrice);

    JSONObject putOrderinfo(WxBukaInfo wxBukaInfo, Integer mailPrice);

    void saveBukaInfoAndImg(WxBukaInfo wxBukaInfo);

    void updateBukaInfoAndImg(WxBukaInfo wxBukaInfo);

    Map<String, Object> getShenLingData(ZkjdcxParam zkjdcxParam, String state);

    List<Map<String, Object>> getBuHuanKaData(ZkjdcxParam zkjdcxParam, String state);
}
