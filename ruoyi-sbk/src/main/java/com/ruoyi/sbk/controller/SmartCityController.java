package com.ruoyi.sbk.controller;

import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SbkUser;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.sbk.common.SbkBaseController;
import com.ruoyi.sbk.domain.WxArchives;
import com.ruoyi.sbk.domain.WxBukaInfo;
import com.ruoyi.sbk.domain.WxDistrict2;
import com.ruoyi.sbk.domain.WxInfomationImg;
import com.ruoyi.sbk.dto.*;
import com.ruoyi.sbk.service.*;
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

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@Slf4j
@Api(tags = "智慧城市")
@RestController
@RequestMapping("/smart/city")
public class SmartCityController extends SbkBaseController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private SbkService sbkService;
    @Autowired
    private IWxArchivesService wxArchivesService;
    @Autowired
    private IWxBukaInfoService wxBukaInfoService;
    @Autowired
    private IWxInfomationImgService wxInfomationImgService;
    @Autowired
    private SmartCityService smartCityService;
    @Autowired
    private IWxDistrict2Service wxDistrict2Service;

    /**
     * 测试
     */
    @Log(title = "智慧城市", businessType = BusinessType.OTHER)
    @ApiOperation("测试")
    @PostMapping("/test")
    public AjaxResult test(@RequestBody @Validated EncryptParam encryptParam) {
        SbkUser sbkUser = JSON.parseObject(AESUtils.decrypt(encryptParam.getBody(), AESUtils.KEY), SbkUser.class);
        String jsonString = JSON.toJSONString(sbkUser);
        log.info("解密后的数据：{}", jsonString);
        String encrypt = AESUtils.encrypt(jsonString, AESUtils.KEY);
        return AjaxResult.success("操作成功", encrypt);
    }

    /**
     * 申领
     */
    @Log(title = "智慧城市", businessType = BusinessType.OTHER)
    @ApiOperation("申领")
    @PostMapping("/shenling")
    public AjaxResult shenling(@RequestBody @Validated WxArchives wxArchives) {
        wxArchives.setExamineStatus("0"); // 未审核
        wxArchives.setIsZhifu(0); // 未支付
        int time = (int) (System.currentTimeMillis() / 1000);
        wxArchives.setOrderno(time + RandomUtil.randomNumbers(4)); // 订单号
        wxArchives.setReturnFlag(0); // 未申请退费
        wxArchives.setExamineReturnFlag(0); // 申请退费未审核
        wxArchives.setAddTime(new Date());


        if (!IdcardUtil.isValidCard(wxArchives.getCardNum())) {
            return AjaxResult.error("身份证号码格式错误");
        }

        wxArchives.setStepStatus("999");
        if ("0".equals(wxArchives.getIsMail())) {
            wxArchives.setStepStatus("9");
        }

        String code = request.getParameter("code");
        if ("f54791a523474e12b7c183f17c3cbcc2".equals(code)) {
            wxArchives.setSource("4");
        }

        wxArchives.setPersonid(String.valueOf(wxArchivesService.selectPersonidByMax() + 1));


        boolean isAdult = IdcardUtil.getAgeByIdCard(wxArchives.getCardNum()) > 16;
        wxArchives.setIsAdult(isAdult ? "0" : "1");

        if (!isAdult) {
            if (StrUtil.isBlank(wxArchives.getGuardianName())) {
                return AjaxResult.error("监护人姓名不能为空");
            }
            if (StrUtil.isBlank(wxArchives.getGuardianCardNum())) {
                return AjaxResult.error("监护人身份证号码不能为空");
            }
            if (!IdcardUtil.isValidCard(wxArchives.getGuardianCardNum())) {
                return AjaxResult.error("监护人身份证号码格式错误");
            }

            int gender = IdcardUtil.getGenderByIdCard(wxArchives.getGuardianCardNum());
            wxArchives.setDaiSex(gender == 1 ? "男" : "女");

        }

        WxInfomationImg wxInfomationImg = wxArchives.getWxInfomationImg();
        wxInfomationImg.setPersonid(String.valueOf(wxInfomationImgService.selectPersonidByMax() + 1));

        smartCityService.saveArchivesAndImg(wxArchives);
        return AjaxResult.success("操作成功");
    }

    /**
     * 获取邮寄费支付信息-申领
     */
    @Log(title = "智慧城市", businessType = BusinessType.OTHER)
    @ApiOperation("获取邮寄费支付信息-申领")
    @GetMapping("/slOrderInfo")
    public AjaxResult slOrderInfo(@RequestBody @Validated SlOrderInfoParam slOrderInfoParam) {
        Map<String, Object> result = new HashMap<>();

        WxArchives wxArchives = wxArchivesService.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxArchives>()
                .eq(WxArchives::getCardNum, slOrderInfoParam.getCardNum())
                .eq(WxArchives::getOrderno, slOrderInfoParam.getOrderno()));
        if (wxArchives == null) {
            return AjaxResult.error("未查询到社保卡信息");
        }
        if (wxArchives.getIsZhifu() == 1) {
            return AjaxResult.error("已支付");
        }

        Integer mailPrice = 20;
        WxDistrict2 wxDistrict2 = wxDistrict2Service.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxDistrict2>()
                .eq(WxDistrict2::getCode, wxArchives.getCountyCodeMail()));
        if (wxDistrict2 != null) {
            mailPrice = wxDistrict2.getMailPrice();
        }

        JSONObject jsonObject = smartCityService.putOrderinfo(wxArchives, mailPrice);
        Integer status = jsonObject.getInteger("status");
        if (status == 1) {
            result.put("mailPrice", mailPrice);

            AES aes = SecureUtil.aes("3MH0P00OPS3OOROE".getBytes());
            jsonObject.remove("status");
            jsonObject.remove("error_desc");
            String content = aes.encryptBase64(jsonObject.toJSONString());

            String payUrl = StrUtil.format(
                    "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxc2bd458948e845a0&redirect_uri={}&response_type=code&scope=snsapi_base&state={}&connect_redirect=1",
                    "http://dingzhou.sjzydrj.net/index.php/home/Pay/wxpay/",
                    content);

            result.put("payUrl", payUrl);

            return AjaxResult.success(result);
        } else {
            String errorDesc = jsonObject.getString("error_desc");
            return AjaxResult.error(errorDesc);
        }
    }

    /**
     * 新办卡资格校验
     */
    @Log(title = "智慧城市", businessType = BusinessType.OTHER)
    @ApiOperation("新办卡资格校验")
    @GetMapping("/xbkzgjy")
    public AjaxResult xbkzgjy(@Validated XbkzgjyParam xbkzgjyParam) throws IOException {
        List<String> whiteList = new ArrayList<>();
        whiteList.add("130125200002094513"); // 刘元博
        if (!whiteList.contains(xbkzgjyParam.getSfzh())) {
            // 办卡资格校验
            String keyInfo = xbkzgjyParam.getSfzh() + "|" + xbkzgjyParam.getXm() + "|1";
            Result result = sbkService.getResult("0811011", keyInfo);
            if (!"200".equals(result.getStatusCode())) {
                return AjaxResult.error(result.getMessage());
            }
            Map<String, String> data = (Map<String, String>) result.getData();
            String bkzgjy = ParamUtils.decrypted(SbkParamUtils.PRIVATEKEY, data.get("ReturnResult"));
            String[] bkzgjyArr = bkzgjy.split("\\|");
            if ("0".equals(bkzgjyArr[0])) {
                return AjaxResult.error("您已有社保卡采集信息");
            }
        }

        String code = request.getParameter("code");
        String source = "0";
        if ("f54791a523474e12b7c183f17c3cbcc2".equals(code)) {
            source = "4";
        }

        WxArchives wxArchives = wxArchivesService.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxArchives>().eq(WxArchives::getCardNum, xbkzgjyParam.getSfzh()).eq(WxArchives::getName, xbkzgjyParam.getXm()));
        if (wxArchives != null) {
            // 审核状态：0代表未审核 1代表审核通过 2代表审核未通过
            String examineStatus = wxArchives.getExamineStatus();
            if ("0".equals(examineStatus)) {
                return AjaxResult.error("采集信息审核中");
            } else if ("1".equals(examineStatus)) {
                return AjaxResult.error("采集信息审核已通过");
            } else if ("2".equals(examineStatus)) {
                if (source.equals(wxArchives.getSource())) {
                    return new AjaxResult(201, "采集信息审核未通过");
                } else {
                    return AjaxResult.error("请继续在首次申领渠道修改信息");
                }
            }
        }
        return AjaxResult.success("您可以新采集信息");
    }

    /**
     * 重新采集数据回显
     */
    @Log(title = "智慧城市", businessType = BusinessType.OTHER)
    @ApiOperation("重新采集数据回显")
    @GetMapping("/getCollectInfo")
    public AjaxResult getCollectInfo(@Validated XbkzgjyParam xbkzgjyParam) {
        WxArchives wxArchives = wxArchivesService.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxArchives>().eq(WxArchives::getCardNum, xbkzgjyParam.getSfzh()).eq(WxArchives::getName, xbkzgjyParam.getXm()));
        if (wxArchives == null) {
            return AjaxResult.error("未查到采集信息");
        }
        WxInfomationImg wxInfomationImg = wxInfomationImgService.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxInfomationImg>().eq(WxInfomationImg::getCardNum, xbkzgjyParam.getSfzh()));
        wxArchives.setWxInfomationImg(wxInfomationImg);
        return AjaxResult.success(wxArchives);
    }

    /**
     * 邮寄物流信息
     */
    @Log(title = "智慧城市", businessType = BusinessType.OTHER)
    @ApiOperation("邮寄物流信息")
    @GetMapping("/mailInfo")
    public AjaxResult mailInfo(String wldh) {
        return AjaxResult.success(smartCityService.selectMailInfoByWldh(wldh));
    }

    /**
     * 审核信息查询
     */
    @Log(title = "智慧城市", businessType = BusinessType.OTHER)
    @ApiOperation("审核信息查询")
    @GetMapping("/examineInfo")
    public AjaxResult examineInfo(@Validated ExamineInfoParam examineInfoParam) {
        Map<String, Object> result = new HashMap<>();

        String type = examineInfoParam.getType();
        if (type.equals("shenling")) {
            WxArchives wxArchives = wxArchivesService.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxArchives>().eq(WxArchives::getCardNum, examineInfoParam.getSfzh()).eq(WxArchives::getName, examineInfoParam.getXm()));
            if (wxArchives == null) {
                return AjaxResult.error("未查到申领数据");
            }
            Integer examineStatus = Integer.valueOf(wxArchives.getExamineStatus());
            result.put("examineStatus", examineStatus); // 审核时间
            result.put("examineTime", wxArchives.getExamineTime()); // 审核时间
            result.put("rejectReason", wxArchives.getReason()); // 驳回原因
        } else if (type.equals("buhuanka")) {
            WxBukaInfo wxBukaInfo = wxBukaInfoService.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxBukaInfo>().eq(WxBukaInfo::getIdcardno, examineInfoParam.getSfzh()).eq(WxBukaInfo::getKaName, examineInfoParam.getXm()));
            if (wxBukaInfo == null) {
                return AjaxResult.error("未查到补换卡数据");
            }
            Integer examineStatus = wxBukaInfo.getExamineStatus();
            result.put("examineStatus", examineStatus); // 审核时间
            result.put("examineTime", wxBukaInfo.getExamineTime()); // 审核时间
            result.put("rejectReason", wxBukaInfo.getRejectReason()); // 驳回原因
        } else {
            return AjaxResult.error("审核类型错误");
        }

        return AjaxResult.success(result);
    }

    /**
     * 人员基础信息变更
     */
    @Log(title = "智慧城市", businessType = BusinessType.RYJCXXBG)
    @ApiOperation("人员基础信息变更")
    @PostMapping("/ryjcxxbg")
    public AjaxResult ryjcxxbg(@RequestBody @Validated EncryptParam encryptParam) {
        RyjcxxbgParam ryjcxxbgParam = JSON.parseObject(AESUtils.decrypt(encryptParam.getBody(), AESUtils.KEY), RyjcxxbgParam.class);
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
    public AjaxResult jbxxcx(@RequestBody @Validated EncryptParam encryptParam) throws IOException {
        SbkUser sbkUser = JSON.parseObject(AESUtils.decrypt(encryptParam.getBody(), AESUtils.KEY), SbkUser.class);
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
    public AjaxResult jg(@RequestBody @Validated EncryptParam encryptParam) {
        SbkUser sbkUser = JSON.parseObject(AESUtils.decrypt(encryptParam.getBody(), AESUtils.KEY), SbkUser.class);
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
    public AjaxResult zx(@RequestBody @Validated EncryptParam encryptParam) {
        SbkUser sbkUser = JSON.parseObject(AESUtils.decrypt(encryptParam.getBody(), AESUtils.KEY), SbkUser.class);
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
    public AjaxResult zsgs(@RequestBody @Validated EncryptParam encryptParam) {
        SbkUser sbkUser = JSON.parseObject(AESUtils.decrypt(encryptParam.getBody(), AESUtils.KEY), SbkUser.class);
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
    public AjaxResult fwmmcz(@RequestBody @Validated EncryptParam encryptParam) {
        SbkUser sbkUser = JSON.parseObject(AESUtils.decrypt(encryptParam.getBody(), AESUtils.KEY), SbkUser.class);
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
    public AjaxResult fwmmxg(@RequestBody @Validated EncryptParam encryptParam) {
        FwmmxgParam fwmmxgParam = JSON.parseObject(AESUtils.decrypt(encryptParam.getBody(), AESUtils.KEY), FwmmxgParam.class);
        // 服务密码修改
        String keyInfo = fwmmxgParam.getAac002() + "|" + fwmmxgParam.getAac003() + "|" + fwmmxgParam.getAaz500() + "|" + fwmmxgParam.getOldPassword() + "|" + fwmmxgParam.getNewPassword();
        Result result = sbkService.getResult("0821020", keyInfo);
        return toAjax(result);
    }
}
