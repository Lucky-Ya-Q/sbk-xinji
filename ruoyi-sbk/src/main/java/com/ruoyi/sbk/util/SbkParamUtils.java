package com.ruoyi.sbk.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.ruoyi.common.exception.ServiceException;
import com.tecsun.sm.utils.ParamUtils;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SbkParamUtils {
    // 德生三代卡接口地址
    public static String URL = "http://10.32.40.120:8091/api/outInterface/publicServiceSendMsg";
    // // 社保卡国密算法接口私钥
    // public static String PRIVATEKEY = "00BA4A4BB6A80FD5167F27560C7E3E3AAA77F2E5E2F349C79EBC0C3005844E0B5B";
    // // 社保卡国密算法接口公钥
    // public static String PUBLICKEY = "04A42AC1ECD94238D572EEAC2A5AC463D0B31E1063A840FAEDB0104BA08A26143181BDE8EFB2C88A2CFF39008B0B2A8669F6BE02F5DD2465C586BA6B954F453CE9";
    // // 德生分配外部平台编号
    // public static String PLATFORMID = "shiJiaZhangOnline";

    // 社保卡国密算法接口私钥
    public static String PRIVATEKEY = "6A24AA40B830B53DF31B416A11B6ECA70B23DD870E46852C7BFCCE5D399372B9";
    // 社保卡国密算法接口公钥
    public static String PUBLICKEY = "049F7766F78B323D9CCB7CD8E63A17F9414E59587EEA5AD3AF7F6221F23243357958B7748FC412C950C6699F35DC7D7CA0E47A0F8CF0E11C0F66851EC99761B97A";
    // 德生分配外部平台编号
    public static String PLATFORMID = "xinJiWxAccount";

    /**
     * 封装请求参数
     */
    public static Map<String, Object> getHttpBodys(String transCode, String keyInfo) {
        Map<String, Object> httpBodys = new HashMap<>();
        httpBodys.put("platformId", PLATFORMID);
        Date date = new Date();
        httpBodys.put("platformSeqId", "05" + transCode + DateUtil.format(date, "yyyyMMddHHmmssSSS") + RandomUtil.randomNumbers(6));
        httpBodys.put("platformTransDate", DateUtil.format(date, "yyyyMMdd"));
        httpBodys.put("platformTransTime", DateUtil.format(date, "HHmmss"));
        httpBodys.put("transCode", transCode);
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("keyInfo", keyInfo);
            String dataString = ParamUtils.encrypted(PUBLICKEY, JSON.toJSONString(data));
            httpBodys.put("dataString", dataString);
            String sign = ParamUtils.sign(PLATFORMID, PRIVATEKEY, dataString);
            httpBodys.put("sign", sign);
        } catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }
        return httpBodys;
    }
}
