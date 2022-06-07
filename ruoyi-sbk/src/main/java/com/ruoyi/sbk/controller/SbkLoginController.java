package com.ruoyi.sbk.controller;

import cn.hutool.core.util.DesensitizedUtil;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SbkUser;
import com.ruoyi.common.core.domain.model.SbkLoginUser;
import com.ruoyi.framework.web.service.SbkTokenService;
import com.ruoyi.sbk.dto.AccountParam;
import com.ruoyi.sbk.dto.Result;
import com.ruoyi.sbk.service.CSBService;
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

@Api(tags = "社保卡登录")
@RestController
@RequestMapping("/sbklogin")
public class SbkLoginController {
    @Autowired
    private CSBService csbService;
    @Autowired
    private SbkTokenService sbkTokenService;
    @Autowired
    private SbkService sbkService;

    /**
     * 身份证号密码登录
     */
    @ApiOperation("身份证号密码登录")
    @PostMapping("/account")
    public AjaxResult account(@RequestBody @Validated AccountParam accountParam) throws IOException {
        // 社保卡基本信息查询
        Result result = sbkService.getResult("0811014", accountParam.getUsername() + "||");
        if (!"200".equals(result.getStatusCode())) {
            return AjaxResult.error(result.getMessage());
        }
        Map<String, String> data = (Map<String, String>) result.getData();
        String jbxxcx = ParamUtils.decrypted(SbkParamUtils.PRIVATEKEY, data.get("ReturnResult"));
        String[] jbxxcxArr = jbxxcx.split("\\|");

        // 服务密码校验
        String fwmmjyKeyInfo = jbxxcxArr[1] + "|" + jbxxcxArr[0] + "|" + jbxxcxArr[10] + "|" + accountParam.getPassword();
        Result fwmmjyResult = sbkService.getResult("0821021", fwmmjyKeyInfo);
        if (!"200".equals(fwmmjyResult.getStatusCode())) {
            return AjaxResult.error(fwmmjyResult.getMessage());
        }

        SbkUser sbkUser = new SbkUser();
        sbkUser.setAaz500(jbxxcxArr[10]);
        sbkUser.setAac002(jbxxcxArr[1]);
        sbkUser.setAac003(jbxxcxArr[0]);

        SbkLoginUser sbkLoginUser = new SbkLoginUser();
        sbkLoginUser.setUser(sbkUser);

        String token = sbkTokenService.createToken(sbkLoginUser);
        return AjaxResult.success().put(Constants.TOKEN, token);
    }

    /**
     * 扫码登录-获取二维码
     */
    @ApiOperation("扫码登录-获取二维码")
    @GetMapping("/getQrCode")
    public AjaxResult getQrCode() {
        return AjaxResult.success(csbService.qrcode_channel_encrypt());
    }

    /**
     * 扫码登录-校验二维码
     */
    @ApiOperation("扫码登录-校验二维码")
    @GetMapping("/verifyQrCode")
    public AjaxResult verifyQrCode(String qrCode) throws IOException {
        String esscNo = csbService.qrcode_channel_query_encrypt(qrCode).getString("esscNo");

        // 电子社保卡基本信息
        Result result = sbkService.getResult("0811015", esscNo);
        if (!"200".equals(result.getStatusCode())) {
            return AjaxResult.error(result.getMessage());
        }
        Map<String, String> data = (Map<String, String>) result.getData();
        String dzsbkjbxx = ParamUtils.decrypted(SbkParamUtils.PRIVATEKEY, data.get("ReturnResult"));

        // String dzsbkjbxx = "发卡地行政区划代码|AD0351899|卡识别码|130125200002094513|刘元博|社保卡状态";
        String[] dzsbkjbxxArr = dzsbkjbxx.split("\\|");

        SbkUser sbkUser = new SbkUser();
        sbkUser.setAab301(dzsbkjbxxArr[0]);
        sbkUser.setAaz500(dzsbkjbxxArr[1]);
        sbkUser.setAaz501(dzsbkjbxxArr[2]);
        sbkUser.setAac002(dzsbkjbxxArr[3]);
        sbkUser.setAac003(dzsbkjbxxArr[4]);
        sbkUser.setAaz502(dzsbkjbxxArr[5]);

        SbkLoginUser sbkLoginUser = new SbkLoginUser();
        sbkLoginUser.setUser(sbkUser);

        String token = sbkTokenService.createToken(sbkLoginUser);
        return AjaxResult.success().put(Constants.TOKEN, token);
    }

    /**
     * 获取信息-电子社保卡基本信息
     */
    @ApiOperation("获取信息-电子社保卡基本信息")
    @GetMapping("/getInfo")
    public AjaxResult getInfo() {
        SbkUser sbkUser = SbkSecurityUtils.getSbkUser();
        sbkUser.setAac003(DesensitizedUtil.chineseName(sbkUser.getAac003()));
        return AjaxResult.success(sbkUser);
    }
}
