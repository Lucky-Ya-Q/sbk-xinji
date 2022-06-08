package com.ruoyi.sbk.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SbkUser;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.sbk.common.SbkBaseController;
import com.ruoyi.sbk.dto.FwmmxgParam;
import com.ruoyi.sbk.dto.Result;
import com.ruoyi.sbk.dto.RyjcxxbgParam;
import com.ruoyi.sbk.service.SbkService;
import com.ruoyi.sbk.util.AESUtils;
import com.ruoyi.sbk.util.SbkParamUtils;
import com.tecsun.sm.utils.ParamUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Api(tags = "智慧城市")
@RestController
@RequestMapping("/smart/city")
public class SmartCityController extends SbkBaseController {
    @Autowired
    private SbkService sbkService;

    /**
     * 测试
     */
    @ApiOperation("测试")
    @PostMapping("/test")
    public AjaxResult test(@RequestBody String body) {
        SbkUser sbkUser = JSON.parseObject(AESUtils.decrypt(body, AESUtils.KEY), SbkUser.class);
        System.out.println(sbkUser);
        String encrypt = AESUtils.encrypt(JSON.toJSONString(sbkUser), AESUtils.KEY);
        return AjaxResult.success("操作成功", encrypt);
    }

    /**
     * 人员基础信息变更
     */
    @Log(title = "智慧城市", businessType = BusinessType.RYJCXXBG)
    @ApiOperation("人员基础信息变更")
    @PostMapping("/ryjcxxbg")
    public AjaxResult ryjcxxbg(@RequestBody String body) {
        RyjcxxbgParam ryjcxxbgParam = JSON.parseObject(AESUtils.decrypt(body, AESUtils.KEY), RyjcxxbgParam.class);
        // 人员基础信息变更
        String keyInfo = ryjcxxbgParam.getAac002() + "|" + ryjcxxbgParam.getAac003() + "|" + ryjcxxbgParam.getJzdz() + "|" + ryjcxxbgParam.getYddh() + "|" + ryjcxxbgParam.getQsrq() + "|" + ryjcxxbgParam.getZzrq() + "|" + ryjcxxbgParam.getZy();
        Result result = sbkService.getResult("0821014", keyInfo);
        return toAjax(result);
    }

    /**
     * 基本信息查询
     */
    @ApiOperation("基本信息查询")
    @PostMapping("/jbxxcx")
    public AjaxResult jbxxcx(@RequestBody String body) throws IOException {
        SbkUser sbkUser = JSON.parseObject(AESUtils.decrypt(body, AESUtils.KEY), SbkUser.class);
        // 智慧城市基本信息查询
        Result result = sbkService.getResult("0811014", sbkUser.getAac002() + "||");
        if (!"200".equals(result.getStatusCode())) {
            return AjaxResult.error(result.getMessage());
        }
        Map<String, String> data = (Map<String, String>) result.getData();
        String jbxxcx = ParamUtils.decrypted(SbkParamUtils.PRIVATEKEY, data.get("ReturnResult"));
        String encrypt = AESUtils.encrypt(jbxxcx, AESUtils.KEY);
        return AjaxResult.success("操作成功", encrypt);
    }

    /**
     * 解挂
     */
    @Log(title = "智慧城市", businessType = BusinessType.JG)
    @ApiOperation("解挂")
    @PostMapping("/jg")
    public AjaxResult jg(@RequestBody String body) {
        SbkUser sbkUser = JSON.parseObject(AESUtils.decrypt(body, AESUtils.KEY), SbkUser.class);
        // 解挂
        String keyInfo = sbkUser.getAac002() + "|" + sbkUser.getAac003() + "|" + sbkUser.getAaz500();
        Result result = sbkService.getResult("0821015", keyInfo);
        return toAjax(result);
    }

    /**
     * 注销
     */
    @Log(title = "智慧城市", businessType = BusinessType.OTHER)
    @ApiOperation("注销")
    @PostMapping("/zx")
    public AjaxResult zx(@RequestBody String body) {
        SbkUser sbkUser = JSON.parseObject(AESUtils.decrypt(body, AESUtils.KEY), SbkUser.class);
        // 注销
        String keyInfo = sbkUser.getAac002() + "|" + sbkUser.getAac003() + "|" + sbkUser.getAaz500();
        Result result = sbkService.getResult("0821018", keyInfo);
        return toAjax(result);
    }

    /**
     * 正式挂失
     */
    @Log(title = "智慧城市", businessType = BusinessType.ZSGS)
    @ApiOperation("正式挂失")
    @PostMapping("/zsgs")
    public AjaxResult zsgs(@RequestBody String body) {
        SbkUser sbkUser = JSON.parseObject(AESUtils.decrypt(body, AESUtils.KEY), SbkUser.class);
        // 正式挂失
        String keyInfo = sbkUser.getAac002() + "|" + sbkUser.getAac003() + "|" + sbkUser.getAaz500();
        Result result = sbkService.getResult("0821017", keyInfo);
        return toAjax(result);
    }

    /**
     * 服务密码重置
     */
    @Log(title = "智慧城市", businessType = BusinessType.FWMMCZ)
    @ApiOperation("服务密码重置")
    @PostMapping("/fwmmcz")
    public AjaxResult fwmmcz(@RequestBody String body) {
        SbkUser sbkUser = JSON.parseObject(AESUtils.decrypt(body, AESUtils.KEY), SbkUser.class);
        // 服务密码重置
        String keyInfo = sbkUser.getAac002() + "|" + sbkUser.getAac003() + "|" + sbkUser.getAaz500() + "|123456|";
        Result result = sbkService.getResult("0821019", keyInfo);
        return toAjax(result, "重置为123456");
    }

    /**
     * 服务密码修改
     */
    @Log(title = "智慧城市", businessType = BusinessType.FWMMXG)
    @ApiOperation("服务密码修改")
    @PostMapping("/fwmmxg")
    public AjaxResult fwmmxg(@RequestBody String body) {
        FwmmxgParam fwmmxgParam = JSON.parseObject(AESUtils.decrypt(body, AESUtils.KEY), FwmmxgParam.class);
        // 服务密码修改
        String keyInfo = fwmmxgParam.getAac002() + "|" + fwmmxgParam.getAac003() + "|" + fwmmxgParam.getAaz500() + "|" + fwmmxgParam.getOldPassword() + "|" + fwmmxgParam.getNewPassword();
        Result result = sbkService.getResult("0821020", keyInfo);
        return toAjax(result);
    }
}
