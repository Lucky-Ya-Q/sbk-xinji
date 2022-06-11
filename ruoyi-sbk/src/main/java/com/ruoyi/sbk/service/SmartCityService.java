package com.ruoyi.sbk.service;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.sbk.domain.WxArchives;
import com.ruoyi.sbk.domain.WxBukaInfo;
import org.springframework.transaction.annotation.Transactional;

public interface SmartCityService {
    JSONObject selectMailInfoByWldh(String wldh);

    void saveArchivesAndImg(WxArchives wxArchives);

    void updateArchivesAndImg(WxArchives wxArchives);

    JSONObject putOrderinfo(WxArchives wxArchives, Integer mailPrice);

    JSONObject putOrderinfo(WxBukaInfo wxBukaInfo, Integer mailPrice);
}
