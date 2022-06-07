package com.ruoyi.sbk.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SbkUser;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.sbk.common.SbkBaseController;
import com.ruoyi.sbk.dto.FwmmxgParam;
import com.ruoyi.sbk.dto.Result;
import com.ruoyi.sbk.dto.RyjcxxbgParam;
import com.ruoyi.sbk.enums.ServiceType;
import com.ruoyi.sbk.factory.SbkAsyncFactory;
import com.ruoyi.sbk.service.SbkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "社保卡管理后台")
@RestController
@RequestMapping("/sbk/admin")
public class SbkAdminController extends SbkBaseController {
    @Autowired
    private SbkService sbkService;

    /**
     * 人员基础信息变更
     */
    @Log(title = "社保卡", businessType = BusinessType.RYJCXXBG)
    @ApiOperation("人员基础信息变更")
    @PutMapping("/ryjcxxbg")
    public AjaxResult ryjcxxbg(@RequestBody @Validated RyjcxxbgParam ryjcxxbgParam) {
        // 人员基础信息变更
        String keyInfo = ryjcxxbgParam.getAac002() + "|" + ryjcxxbgParam.getAac003() + "|" + ryjcxxbgParam.getJzdz() + "|" + ryjcxxbgParam.getYddh() + "|" + ryjcxxbgParam.getQsrq() + "|" + ryjcxxbgParam.getZzrq() + "|" + ryjcxxbgParam.getZy();
        Result result = sbkService.getResult("0821014", keyInfo);
        SbkUser sbkUser = new SbkUser();
        BeanUtils.copyProperties(ryjcxxbgParam, sbkUser);
        AsyncManager.me().execute(SbkAsyncFactory.recordOper(SecurityUtils.getLoginUser(),
                sbkUser, ServiceType.XXBG));
        return toAjax(result);
    }

    /**
     * 解挂
     */
    @Log(title = "社保卡", businessType = BusinessType.JG)
    @ApiOperation("解挂")
    @PutMapping("/jg")
    public AjaxResult jg(@RequestBody SbkUser sbkUser) {
        // 解挂
        String keyInfo = sbkUser.getAac002() + "|" + sbkUser.getAac003() + "|" + sbkUser.getAaz500();
        Result result = sbkService.getResult("0821015", keyInfo);
        AsyncManager.me().execute(SbkAsyncFactory.recordOper(SecurityUtils.getLoginUser(),
                sbkUser, ServiceType.JG));
        return toAjax(result);
    }

    /**
     * 正式挂失
     */
    @Log(title = "社保卡", businessType = BusinessType.ZSGS)
    @ApiOperation("正式挂失")
    @PutMapping("/zsgs")
    public AjaxResult zsgs(@RequestBody SbkUser sbkUser) {
        // 正式挂失
        String keyInfo = sbkUser.getAac002() + "|" + sbkUser.getAac003() + "|" + sbkUser.getAaz500();
        Result result = sbkService.getResult("0821017", keyInfo);
        AsyncManager.me().execute(SbkAsyncFactory.recordOper(SecurityUtils.getLoginUser(),
                sbkUser, ServiceType.GS));
        return toAjax(result);
    }

    /**
     * 服务密码重置
     */
    @Log(title = "社保卡", businessType = BusinessType.FWMMCZ)
    @ApiOperation("服务密码重置")
    @PutMapping("/fwmmcz")
    public AjaxResult fwmmcz(@RequestBody SbkUser sbkUser) {
        // 服务密码重置
        String keyInfo = sbkUser.getAac002() + "|" + sbkUser.getAac003() + "|" + sbkUser.getAaz500() + "|123456|";
        Result result = sbkService.getResult("0821019", keyInfo);
        AsyncManager.me().execute(SbkAsyncFactory.recordOper(SecurityUtils.getLoginUser(),
                sbkUser, ServiceType.MMCZ));
        return toAjax(result, "重置为123456");
    }

    /**
     * 服务密码修改
     */
    @Log(title = "社保卡", businessType = BusinessType.FWMMXG)
    @ApiOperation("服务密码修改")
    @PutMapping("/fwmmxg")
    public AjaxResult fwmmxg(@RequestBody @Validated FwmmxgParam fwmmxgParam) {
        // 服务密码修改
        String keyInfo = fwmmxgParam.getAac002() + "|" + fwmmxgParam.getAac003() + "|" + fwmmxgParam.getAaz500() + "|" + fwmmxgParam.getOldPassword() + "|" + fwmmxgParam.getNewPassword();
        Result result = sbkService.getResult("0821020", keyInfo);
        SbkUser sbkUser = new SbkUser();
        BeanUtils.copyProperties(fwmmxgParam, sbkUser);
        AsyncManager.me().execute(SbkAsyncFactory.recordOper(SecurityUtils.getLoginUser(),
                sbkUser, ServiceType.MMXG));
        return toAjax(result);
    }
}
