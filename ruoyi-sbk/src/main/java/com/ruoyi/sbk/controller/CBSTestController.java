package com.ruoyi.sbk.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.sbk.service.CSBService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "cbs测试")
@RestController
@RequestMapping("/cbstest")
public class CBSTestController {
    @Autowired
    private CSBService csbService;

    /**
     * 扫码登录-渠道二维码生成接口
     */
    @ApiOperation("扫码登录-渠道二维码生成接口")
    @GetMapping("/qrcode_channel_encrypt")
    public AjaxResult qrcode_channel_encrypt() {
        return AjaxResult.success(csbService.qrcode_channel_encrypt());
    }

    /**
     * 扫码登录-二维码验证授权结果查询接口
     */
    @ApiOperation("扫码登录-二维码验证授权结果查询接口")
    @GetMapping("/qrcode_channel_query_encrypt")
    public AjaxResult qrcode_channel_query_encrypt(String qrCode) {
        return AjaxResult.success(csbService.qrcode_channel_query_encrypt(qrCode));
    }
}
