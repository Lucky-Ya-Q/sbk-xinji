package com.ruoyi.sbk.service;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.sbk.domain.WxArchives;

public interface SmartCityService {
    JSONObject selectMailInfoByWldh(String wldh);

    void saveArchivesAndImg(WxArchives wxArchives);
}
