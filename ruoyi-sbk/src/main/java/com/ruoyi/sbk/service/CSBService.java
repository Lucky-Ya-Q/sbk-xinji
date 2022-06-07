package com.ruoyi.sbk.service;

import com.alibaba.fastjson.JSONObject;

public interface CSBService {
    JSONObject qrcode_channel_encrypt();

    JSONObject qrcode_channel_query_encrypt(String qrCode);
}
