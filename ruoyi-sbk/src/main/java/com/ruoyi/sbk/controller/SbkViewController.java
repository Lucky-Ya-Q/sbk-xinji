package com.ruoyi.sbk.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SbkUser;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.sbk.common.SbkBaseController;
import com.ruoyi.sbk.dto.FwmmxgParam;
import com.ruoyi.sbk.dto.Result;
import com.ruoyi.sbk.dto.RyjcxxbgParam;
import com.ruoyi.sbk.enums.ServiceType;
import com.ruoyi.sbk.factory.SbkAsyncFactory;
import com.ruoyi.sbk.service.SbkService;
import com.ruoyi.sbk.util.SbkParamUtils;
import com.ruoyi.sbk.util.SbkSecurityUtils;
import com.tecsun.sm.utils.ParamUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Api(tags = "社保卡服务平台")
@RestController
@RequestMapping("/sbk/view")
public class SbkViewController extends SbkBaseController {
    @Autowired
    private SbkService sbkService;

    /**
     * 人员基础信息变更
     */
    @ApiOperation("人员基础信息变更")
    @PutMapping("/ryjcxxbg")
    public AjaxResult ryjcxxbg(@RequestBody @Validated RyjcxxbgParam ryjcxxbgParam) {
        SbkUser sbkUser = SbkSecurityUtils.getSbkUser();
        // 人员基础信息变更
        String keyInfo = sbkUser.getAac002() + "|" + sbkUser.getAac003() + "|" + ryjcxxbgParam.getJzdz() + "|" + ryjcxxbgParam.getYddh() + "|" + ryjcxxbgParam.getQsrq() + "|" + ryjcxxbgParam.getZzrq() + "|" + ryjcxxbgParam.getZy();
        Result result = sbkService.getResult("0821014", keyInfo);
        AsyncManager.me().execute(SbkAsyncFactory.recordOper(sbkUser, ServiceType.XXBG));
        return toAjax(result);
    }

    /**
     * 基本信息查询
     */
    @ApiOperation("基本信息查询")
    @GetMapping("/jbxxcx")
    public AjaxResult jbxxcx() throws IOException {
        SbkUser sbkUser = SbkSecurityUtils.getSbkUser();
        // 社保卡基本信息查询
        String keyInfo = sbkUser.getAac002() + "|" + sbkUser.getAac003() + "|" + sbkUser.getAaz500();
        Result result = sbkService.getResult("0811014", keyInfo);
        if (!"200".equals(result.getStatusCode())) {
            return AjaxResult.error(result.getMessage());
        }
        Map<String, String> data = (Map<String, String>) result.getData();
        String jbxxcx = ParamUtils.decrypted(SbkParamUtils.PRIVATEKEY, data.get("ReturnResult"));
        String[] jbxxcxArr = jbxxcx.split("\\|");
        return AjaxResult.success(jbxxcxArr);
    }

    /**
     * 解挂
     */
    @ApiOperation("解挂")
    @PutMapping("/jg")
    public AjaxResult jg() {
        SbkUser sbkUser = SbkSecurityUtils.getSbkUser();
        // 解挂
        String keyInfo = sbkUser.getAac002() + "|" + sbkUser.getAac003() + "|" + sbkUser.getAaz500();
        Result result = sbkService.getResult("0821015", keyInfo);
        AsyncManager.me().execute(SbkAsyncFactory.recordOper(sbkUser, ServiceType.JG));
        return toAjax(result);
    }

    /**
     * 正式挂失
     */
    @ApiOperation("正式挂失")
    @PutMapping("/zsgs")
    public AjaxResult zsgs() {
        SbkUser sbkUser = SbkSecurityUtils.getSbkUser();
        // 正式挂失
        String keyInfo = sbkUser.getAac002() + "|" + sbkUser.getAac003() + "|" + sbkUser.getAaz500();
        Result result = sbkService.getResult("0821017", keyInfo);
        AsyncManager.me().execute(SbkAsyncFactory.recordOper(sbkUser, ServiceType.GS));
        return toAjax(result);
    }

    /**
     * 服务密码重置
     */
    @ApiOperation("服务密码重置")
    @PutMapping("/fwmmcz")
    public AjaxResult fwmmcz() {
        SbkUser sbkUser = SbkSecurityUtils.getSbkUser();
        // 服务密码重置
        String keyInfo = sbkUser.getAac002() + "|" + sbkUser.getAac003() + "|" + sbkUser.getAaz500() + "|123456|";
        Result result = sbkService.getResult("0821019", keyInfo);
        AsyncManager.me().execute(SbkAsyncFactory.recordOper(sbkUser, ServiceType.MMCZ));
        return toAjax(result, "密码重置为123456");
    }

    /**
     * 服务密码修改
     */
    @ApiOperation("服务密码修改")
    @PutMapping("/fwmmxg")
    public AjaxResult fwmmxg(@RequestBody @Validated FwmmxgParam fwmmxgParam) {
        SbkUser sbkUser = SbkSecurityUtils.getSbkUser();
        // 服务密码修改
        String keyInfo = sbkUser.getAac002() + "|" + sbkUser.getAac003() + "|" + sbkUser.getAaz500() + "|" + fwmmxgParam.getOldPassword() + "|" + fwmmxgParam.getNewPassword();
        Result result = sbkService.getResult("0821020", keyInfo);
        AsyncManager.me().execute(SbkAsyncFactory.recordOper(sbkUser, ServiceType.MMXG));
        return toAjax(result);
    }

    /**
     * 制卡进度查询
     */
    @ApiOperation("制卡进度查询")
    @PutMapping("/zkjdcx")
    public AjaxResult zkjdcx() throws IOException {
        SbkUser sbkUser = SbkSecurityUtils.getSbkUser();
        // 制卡进度查询
        String keyInfo = sbkUser.getAac002() + "|" + sbkUser.getAac003();
        Result result = sbkService.getResult("0811016", keyInfo);
        if (!"200".equals(result.getStatusCode())) {
            return AjaxResult.error(result.getMessage());
        }
        Map<String, String> data = (Map<String, String>) result.getData();
        String zkjdcx = ParamUtils.decrypted(SbkParamUtils.PRIVATEKEY, data.get("ReturnResult"));
        String[] zkjdcxArr = zkjdcx.split("\\|");
        return AjaxResult.success(zkjdcxArr);
    }
}
