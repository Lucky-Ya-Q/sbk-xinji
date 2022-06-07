package com.ruoyi.sbk.service.impl;

import com.alibaba.csb.sdk.ContentBody;
import com.alibaba.csb.sdk.HttpCaller;
import com.alibaba.csb.sdk.HttpCallerException;
import com.alibaba.csb.sdk.HttpParameters;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.sbk.service.CSBService;
import com.ruoyi.sbk.util.AESUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class CSBServiceImpl implements CSBService {
    // 渠道编号
    private final String channelNo = "9200000003";
    // 地方接入编号
    private final String accessNo = "0313000001";

    // 互联网-生产环境
    private final String csburl = "https://ssc.mohrss.gov.cn/CSB/";
    private final String accessKey = "5d818531580d4f39851bef258d9a1992";
    private final String securityKey = "1nbqJh19NFN6axl3V+nnnYD26OM=";
    private final String encryptKey = "bko8fnu2570b6qpy";

    // 内网-生产环境
    // private final String csburl = "http://10.1.189.189:8086/CSB/";
    // private final String accessKey = "4b148746a54c47dcaa6b7f5c12a787c8";
    // private final String securityKey = "ne/RsEpRJRvmcySH5CGiNXfVauM=";
    // private final String encryptKey = "bko8fnu2570b6qpy";

    private final String api_version = "1.0.0";
    private final String api_method = "post";

    /**
     * 扫码登录-渠道二维码生成接口
     *
     * @return
     */
    @Override
    public JSONObject qrcode_channel_encrypt() {
        HttpParameters.Builder builder = HttpParameters.newBuilder();
        builder.requestURL(csburl) // 设置请求的URL
                .api("qrcode_channel_encrypt") // 设置服务名
                .version(api_version) // 设置版本号
                .method(api_method) // 设置调用方式, get/post
                .accessKey(accessKey).secretKey(securityKey); // 设置accessKey 和 设置secretKey

        // 设置请求参数（json格式)
        Map<String, String> param = new HashMap<>();
        param.put("channelNo", channelNo);
        param.put("accessNo", accessNo);
        param.put("systemName", "辛集市乡村人社公共服务平台");
        AESUtils.encrypt(JSON.toJSONString(param), encryptKey);
        Map<String, String> encryptParam = new HashMap<>();
        encryptParam.put("security", AESUtils.encrypt(JSON.toJSONString(param), encryptKey));

        ContentBody cb = new ContentBody(JSON.toJSONString(encryptParam));
        builder.contentBody(cb);

        // 进行调用 返回结果（json格式)
        String result = null;
        try {
            result = HttpCaller.invoke(builder.build());
            log.info("================sign_info==================");
            log.info(result);
            /* 返回的result内容如下：
                {"msgCode":700,"msg":"失败","result":null}
                {"code":503,"csbCode":503,"message":"[503]service not registed, key is : _api_qrcode_channel_encrypt_null_null_1.0.0","requestId":"ac1e500c16287562905084900d00af","state":{}}
             */
            JSONObject retJson = JSON.parseObject(result); // 返回的json对象
            if ("000000".equals(retJson.getString("msgCode"))) {
                JSONObject resultJson = retJson.getJSONObject("result");
                String encryptValue = resultJson.getString("encrypt");
                String decrypt = AESUtils.decrypt(encryptValue, encryptKey);
                // 打印解密后出参
                log.info(decrypt);
                return JSON.parseObject(decrypt);
            } else if (retJson.getString("msgCode").startsWith("5")) {
                throw new ServiceException(retJson.getString("message"));
            } else {
                throw new ServiceException(retJson.getString("msg"), retJson.getInteger("msgCode"));
            }
        } catch (HttpCallerException e) {
            log.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 扫码登录-二维码验证授权结果查询接口
     *
     * @param qrCode
     * @return
     */
    @Override
    public JSONObject qrcode_channel_query_encrypt(String qrCode) {
        HttpParameters.Builder builder = HttpParameters.newBuilder();
        builder.requestURL(csburl) // 设置请求的URL
                .api("qrcode_channel_query_encrypt") // 设置服务名
                .version(api_version) // 设置版本号
                .method(api_method) // 设置调用方式, get/post
                .accessKey(accessKey).secretKey(securityKey); // 设置accessKey 和 设置secretKey

        // 设置请求参数（json格式)
        Map<String, String> param = new HashMap<>();
        param.put("channelNo", channelNo);
        param.put("accessNo", accessNo);
        param.put("qrCode", qrCode);
        AESUtils.encrypt(JSON.toJSONString(param), encryptKey);
        Map<String, String> encryptParam = new HashMap<>();
        encryptParam.put("security", AESUtils.encrypt(JSON.toJSONString(param), encryptKey));

        ContentBody cb = new ContentBody(JSON.toJSONString(encryptParam));
        builder.contentBody(cb);

        //进行调用 返回结果（json格式)
        String result = null;
        try {
            result = HttpCaller.invoke(builder.build());
            log.info("================sign_info==================");
            log.info(result);
            /* 返回的result内容如下：
                {"msgCode":700,"msg":"失败","result":null}
                {"code":503,"csbCode":503,"message":"[503]service not registed, key is : _api_qrcode_channel_encrypt_null_null_1.0.0","requestId":"ac1e500c16287562905084900d00af","state":{}}
             */
            JSONObject retJson = JSON.parseObject(result); // 返回的json对象
            if ("000000".equals(retJson.getString("msgCode"))) {
                JSONObject resultJson = retJson.getJSONObject("result");
                String encryptValue = resultJson.getString("encrypt");
                String decrypt = AESUtils.decrypt(encryptValue, encryptKey);
                // 打印解密后出参
                log.info(decrypt);
                return JSON.parseObject(decrypt);
            } else if (retJson.getString("msgCode").startsWith("5")) {
                throw new ServiceException(retJson.getString("message"));
            } else {
                throw new ServiceException(retJson.getString("msg"), retJson.getInteger("msgCode"));
            }
        } catch (HttpCallerException e) {
            log.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }
}
