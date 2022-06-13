package com.ruoyi;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.config.WxMpHostConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class RuoYiApplicationRunner implements ApplicationRunner {
    @Autowired
    private WxMpService wxMpService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        WxMpHostConfig wxMpHostConfig = new WxMpHostConfig();
        wxMpHostConfig.setApiHost("http://10.39.248.217:9904");
        wxMpHostConfig.setOpenHost("http://10.39.248.217:9904");
        wxMpHostConfig.setMpHost("http://10.39.248.217:9904");
        wxMpService.getWxMpConfigStorage().setHostConfig(wxMpHostConfig);
    }
}
