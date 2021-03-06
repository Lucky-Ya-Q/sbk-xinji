package com.ruoyi.sbk.controller;

import cn.hutool.core.net.URLEncoder;
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
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.sbk.common.SbkBaseController;
import com.ruoyi.sbk.domain.*;
import com.ruoyi.sbk.dto.*;
import com.ruoyi.sbk.service.*;
import com.ruoyi.sbk.util.AESUtils;
import com.ruoyi.sbk.util.SbkParamUtils;
import com.tecsun.sm.utils.ParamUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Api(tags = "智慧城市")
@RestController
@RequestMapping("/smart/city")
public class SmartCityController extends SbkBaseController {
    @Autowired
    private SbkService sbkService;
    @Autowired
    private IWxArchivesService wxArchivesService;
    @Autowired
    private IWxBukaInfoService wxBukaInfoService;
    @Autowired
    private IWxBukaInfoImgService wxBukaInfoImgService;
    @Autowired
    private IWxInfomationImgService wxInfomationImgService;
    @Autowired
    private SmartCityService smartCityService;
    @Autowired
    private IWxDistrict2Service wxDistrict2Service;
    @Autowired
    private IUnitinfoShiService unitinfoShiService;
    @Autowired
    private IWxBukaBankService wxBukaBankService;
    @Autowired
    private IWxRelationService wxRelationService;
    @Autowired
    private IWxResidenceTypeService wxResidenceTypeService;
    @Autowired
    private IWxOccupationService wxOccupationService;
    @Autowired
    private IWxMingzuService wxMingzuService;
    @Autowired
    private WxMpService wxMpService;

    /**
     * 测试
     */
    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("测试")
    @PostMapping("/test")
    public AjaxResult test(@RequestBody @Validated EncryptParam encryptParam) throws WxErrorException {
        SbkUser sbkUser = JSON.parseObject(AESUtils.decrypt(encryptParam.getBody(), AESUtils.KEY), SbkUser.class);
        String jsonString = JSON.toJSONString(sbkUser);
        log.info("解密后的数据：{}", jsonString);
        String encrypt = AESUtils.encrypt(jsonString, AESUtils.KEY);
        return AjaxResult.success("操作成功", encrypt);
    }

    /**
     * 微信网页授权
     */
    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("微信网页授权")
    @PostMapping("/userInfo")
    public AjaxResult userInfo(@RequestBody @Validated UserInfoParam userInfoParam) throws WxErrorException {
        WxOAuth2AccessToken wxOAuth2AccessToken = wxMpService.getOAuth2Service().getAccessToken(userInfoParam.getWxCode());
        WxOAuth2UserInfo wxOAuth2UserInfo = wxMpService.getOAuth2Service().getUserInfo(wxOAuth2AccessToken, null);
        WxMpUser wxMpUser = wxMpService.getUserService().userInfo(wxOAuth2UserInfo.getOpenid());
        return AjaxResult.success(wxMpUser);
    }

    /**
     * 申领
     */
    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("申领")
    @PostMapping("/shenling")
    public AjaxResult shenling(@RequestBody @Validated WxArchives wxArchives) {
        Map<String, Object> result = new HashMap<>();
        wxArchives.setIsZhifu(0); // 未支付
        int time = (int) (System.currentTimeMillis() / 1000);
        wxArchives.setOrderno(time + RandomUtil.randomNumbers(4)); // 订单号
        result.put("orderno", wxArchives.getOrderno());
        wxArchives.setReturnFlag(0); // 未申请退费
        wxArchives.setExamineReturnFlag(0); // 申请退费未审核
        wxArchives.setAddTime(new Date());


        if (!IdcardUtil.isValidCard(wxArchives.getCardNum())) {
            return AjaxResult.error("身份证号码格式错误");
        }

        try {
            String[] communicationAddresss = wxArchives.getCommunicationAddress().split("/");
            log.info("校验通讯地址：{}", communicationAddresss[2]);
        } catch (Exception e) {
            throw new ServiceException("通讯地址格式错误");
        }

        wxArchives.setStepStatus("999");
//        wxArchives.setExamineStatus("999");
        wxArchives.setExamineStatus("0");
        if ("0".equals(wxArchives.getIsMail())) {
            wxArchives.setStepStatus("9");
//            wxArchives.setExamineStatus("0");
        } else {
            if (StrUtil.isBlank(wxArchives.getCountyCodeMail())) {
                return AjaxResult.error("收件人区县编码不能为空");
            }
            // 减免邮寄费
            Integer mailPrice = 20;
            WxDistrict2 wxDistrict2 = wxDistrict2Service.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxDistrict2>().eq(WxDistrict2::getCode, wxArchives.getCountyCodeMail()));
            if (wxDistrict2 != null) {
                mailPrice = wxDistrict2.getMailPrice();
            }
            wxArchives.setMoneyEms(mailPrice);
            WxBukaBank wxBukaBank = wxBukaBankService.getById(wxArchives.getBankid());
            wxArchives.setNopayflagEms(wxBukaBank.getNopayflag());
            if (wxBukaBank.getNopayflag() == 1) {
                wxArchives.setStepStatus("9");
//                wxArchives.setExamineStatus("0");
            } else {
//                wxArchives.setExamineStatus("0");
            }
        }

        String code = wxArchives.getCode();
        switch (code) {
            case "f54791a523474e12b7c183f17c3cbcc2":
                wxArchives.setSource("4");
                break;
            case "f1a04d853c2f8212210267ebbda5eadb":
                wxArchives.setSource("1");
                break;
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
        wxInfomationImg.setCardNum(wxArchives.getCardNum());
        wxInfomationImg.setPersonid(String.valueOf(wxInfomationImgService.selectPersonidByMax() + 1));

        smartCityService.saveArchivesAndImg(wxArchives);
        return AjaxResult.success(result);
    }

    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("修改申领")
    @PostMapping("/editShenling")
    public AjaxResult editShenling(@RequestBody @Validated WxArchives wxArchives) {
        wxArchives.setIsZhifu(null); // 未支付
        wxArchives.setOrderno(null); // 订单号
        wxArchives.setReturnFlag(null); // 未申请退费
        wxArchives.setExamineReturnFlag(null); // 申请退费未审核
        wxArchives.setAddTime(null);

        if (!IdcardUtil.isValidCard(wxArchives.getCardNum())) {
            return AjaxResult.error("身份证号码格式错误");
        }

        try {
            String[] communicationAddresss = wxArchives.getCommunicationAddress().split("/");
            log.info("校验通讯地址：{}", communicationAddresss[2]);
        } catch (Exception e) {
            throw new ServiceException("通讯地址格式错误");
        }

        wxArchives.setStepStatus("999");
//        wxArchives.setExamineStatus("999");
        wxArchives.setExamineStatus("0");
        if ("0".equals(wxArchives.getIsMail())) {
            wxArchives.setStepStatus("9");
//            wxArchives.setExamineStatus("0");
        } else {
            if (StrUtil.isBlank(wxArchives.getCountyCodeMail())) {
                return AjaxResult.error("收件人区县编码不能为空");
            }
            // 减免邮寄费
            Integer mailPrice = 20;
            WxDistrict2 wxDistrict2 = wxDistrict2Service.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxDistrict2>().eq(WxDistrict2::getCode, wxArchives.getCountyCodeMail()));
            if (wxDistrict2 != null) {
                mailPrice = wxDistrict2.getMailPrice();
            }
            wxArchives.setMoneyEms(mailPrice);
            WxBukaBank wxBukaBank = wxBukaBankService.getById(wxArchives.getBankid());
            wxArchives.setNopayflagEms(wxBukaBank.getNopayflag());
            if (wxBukaBank.getNopayflag() == 1) {
                wxArchives.setStepStatus("9");
//                wxArchives.setExamineStatus("0");
            } else {
//                wxArchives.setExamineStatus("0");
            }
        }

        wxArchives.setSource(null);
        wxArchives.setPersonid(null);

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
        wxInfomationImg.setCardNum(wxArchives.getCardNum());
        wxInfomationImg.setPersonid(null);

        smartCityService.updateArchivesAndImg(wxArchives);
        return AjaxResult.success();
    }

    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("补换卡")
    @PostMapping("/buhuanka")
    public AjaxResult buhuanka(@RequestBody @Validated WxBukaInfo wxBukaInfo) {
        Map<String, Object> result = new HashMap<>();
        wxBukaInfo.setIsZhifu(0); // 未支付
        int time = (int) (System.currentTimeMillis() / 1000);
        wxBukaInfo.setOrderno(time + RandomUtil.randomNumbers(4)); // 订单号
        result.put("orderno", wxBukaInfo.getOrderno());
        wxBukaInfo.setAddTime(new Date());

        if (!IdcardUtil.isValidCard(wxBukaInfo.getIdcardno())) {
            return AjaxResult.error("身份证号码格式错误");
        }

        wxBukaInfo.setStepStatus(999);
        wxBukaInfo.setExamineStatus(0);
        // 减免邮寄费
        Integer mailPrice = 20;
        WxDistrict2 wxDistrict2 = wxDistrict2Service.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxDistrict2>().eq(WxDistrict2::getCode, wxBukaInfo.getShouZoonCode()));
        if (wxDistrict2 != null) {
            mailPrice = wxDistrict2.getMailPrice();
        }
        wxBukaInfo.setMoneyEms(mailPrice);
        WxBukaBank wxBukaBank = wxBukaBankService.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxBukaBank>()
                .eq(WxBukaBank::getCode, wxBukaInfo.getNewBank()));
        wxBukaInfo.setNopayflagEms(wxBukaBank.getNopayflag());
        if (wxBukaBank.getNopayflag() == 1) {
            wxBukaInfo.setStepStatus(9);
        }

        String code = wxBukaInfo.getCode();
        switch (code) {
            case "f54791a523474e12b7c183f17c3cbcc2":
                wxBukaInfo.setSource(4);
                break;
            case "f1a04d853c2f8212210267ebbda5eadb":
                wxBukaInfo.setSource(1);
                break;
        }

        boolean isAdult = IdcardUtil.getAgeByIdCard(wxBukaInfo.getIdcardno()) > 16;
        wxBukaInfo.setFlag(isAdult ? 1 : 2);

        if (!isAdult) {
            if (StrUtil.isBlank(wxBukaInfo.getDaiName())) {
                return AjaxResult.error("监护人姓名不能为空");
            }
            if (StrUtil.isBlank(wxBukaInfo.getDaiIdcardno())) {
                return AjaxResult.error("监护人身份证号码不能为空");
            }
            if (!IdcardUtil.isValidCard(wxBukaInfo.getDaiIdcardno())) {
                return AjaxResult.error("监护人身份证号码格式错误");
            }
        }

        WxBukaInfoImg wxBukaInfoImg = wxBukaInfo.getWxBukaInfoImg();
        wxBukaInfoImg.setIdcardno(wxBukaInfo.getIdcardno());
        wxBukaInfoImg.setOrderno(wxBukaInfo.getOrderno());
        wxBukaInfoImg.setAddTime(new Date());
        smartCityService.saveBukaInfoAndImg(wxBukaInfo);
        return AjaxResult.success(result);
    }

    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("修改补换卡")
    @PostMapping("/editBuhuanka")
    public AjaxResult editBuhuanka(@RequestBody @Validated WxBukaInfo wxBukaInfo) {
        wxBukaInfo.setIsZhifu(null); // 未支付
        wxBukaInfo.setOrderno(null); // 订单号
        wxBukaInfo.setAddTime(null);

        if (!IdcardUtil.isValidCard(wxBukaInfo.getIdcardno())) {
            return AjaxResult.error("身份证号码格式错误");
        }

        wxBukaInfo.setStepStatus(999);
        wxBukaInfo.setExamineStatus(0);
        // 减免邮寄费
        Integer mailPrice = 20;
        WxDistrict2 wxDistrict2 = wxDistrict2Service.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxDistrict2>().eq(WxDistrict2::getCode, wxBukaInfo.getShouZoonCode()));
        if (wxDistrict2 != null) {
            mailPrice = wxDistrict2.getMailPrice();
        }
        wxBukaInfo.setMoneyEms(mailPrice);
        WxBukaBank wxBukaBank = wxBukaBankService.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxBukaBank>()
                .eq(WxBukaBank::getCode, wxBukaInfo.getNewBank()));
        wxBukaInfo.setNopayflagEms(wxBukaBank.getNopayflag());
        if (wxBukaBank.getNopayflag() == 1) {
            wxBukaInfo.setStepStatus(9);
        }

        wxBukaInfo.setSource(null);

        boolean isAdult = IdcardUtil.getAgeByIdCard(wxBukaInfo.getIdcardno()) > 16;
        wxBukaInfo.setFlag(isAdult ? 1 : 2);

        if (!isAdult) {
            if (StrUtil.isBlank(wxBukaInfo.getDaiName())) {
                return AjaxResult.error("监护人姓名不能为空");
            }
            if (StrUtil.isBlank(wxBukaInfo.getDaiIdcardno())) {
                return AjaxResult.error("监护人身份证号码不能为空");
            }
            if (!IdcardUtil.isValidCard(wxBukaInfo.getDaiIdcardno())) {
                return AjaxResult.error("监护人身份证号码格式错误");
            }
        }

        WxBukaInfoImg wxBukaInfoImg = wxBukaInfo.getWxBukaInfoImg();
        wxBukaInfoImg.setIdcardno(wxBukaInfo.getIdcardno());
        wxBukaInfoImg.setOrderno(null);
        wxBukaInfoImg.setAddTime(null);
        smartCityService.updateBukaInfoAndImg(wxBukaInfo);
        return AjaxResult.success();
    }

    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("取消补换卡申请")
    @PostMapping("/closeBuhuanka")
    public AjaxResult closeBuhuanka(@RequestBody @Validated CloseBuhuanka closeBuhuanka) {
        LambdaQueryWrapper<WxBukaInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WxBukaInfo::getOrderno, closeBuhuanka.getOrderno());
        WxBukaInfo wxBukaInfo = wxBukaInfoService.selectOneByLambdaQueryWrapper(queryWrapper);
        if (wxBukaInfo == null) {
            return AjaxResult.error("补换卡信息不存在");
        }
        if (wxBukaInfo.getExamineStatus() == 1) {
            return AjaxResult.error("补换卡信息审核通过，不允许撤销");
        }

        if (wxBukaInfo.getStepStatus() != -3 && wxBukaInfo.getStepStatus() != 9) {
            return AjaxResult.error("补换卡信息状态异常，不允许撤销");
        }
        wxBukaInfo.setStepStatus(-1);
        wxBukaInfo.setReturnReason(closeBuhuanka.getReason());
        wxBukaInfo.setReturnAddtime(new Date());
        wxBukaInfoService.updateById(wxBukaInfo);
        return AjaxResult.success();
    }

    /**
     * 获取邮寄费支付信息
     */
    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("获取邮寄费支付信息")
    @PostMapping("/orderInfo")
    public AjaxResult orderInfo(@RequestBody @Validated OrderInfoParam orderInfoParam) {
        Map<String, Object> result = new HashMap<>();
        Integer mailPrice = 20;
        JSONObject jsonObject;

        String type = orderInfoParam.getType();
        if ("shenling".equals(type)) {
            WxArchives wxArchives = wxArchivesService.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxArchives>().eq(WxArchives::getCardNum, orderInfoParam.getCardNum()).eq(WxArchives::getOrderno, orderInfoParam.getOrderno()));
            if (wxArchives == null) {
                return AjaxResult.error("未查询到社保卡信息");
            }
            result.put("isPay", wxArchives.getIsZhifu());
            result.put("moneyEms", wxArchives.getMoneyEms());
            result.put("nopayflagEms", wxArchives.getNopayflagEms());
            WxDistrict2 wxDistrict2 = wxDistrict2Service.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxDistrict2>().eq(WxDistrict2::getCode, wxArchives.getCountyCodeMail()));
            if (wxDistrict2 != null) {
                mailPrice = wxDistrict2.getMailPrice();
            }
            jsonObject = smartCityService.putOrderinfo(wxArchives, mailPrice);
        } else if ("buhuanka".equals(type)) {
            WxBukaInfo wxBukaInfo = wxBukaInfoService.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxBukaInfo>().eq(WxBukaInfo::getIdcardno, orderInfoParam.getCardNum()).eq(WxBukaInfo::getOrderno, orderInfoParam.getOrderno()));
            if (wxBukaInfo == null) {
                return AjaxResult.error("未查询到社保卡信息");
            }
            result.put("isPay", wxBukaInfo.getIsZhifu());
            result.put("moneyEms", wxBukaInfo.getMoneyEms());
            result.put("nopayflagEms", wxBukaInfo.getNopayflagEms());
            WxDistrict2 wxDistrict2 = wxDistrict2Service.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxDistrict2>().eq(WxDistrict2::getCode, wxBukaInfo.getShouZoonCode()));
            if (wxDistrict2 != null) {
                mailPrice = wxDistrict2.getMailPrice();
            }
            jsonObject = smartCityService.putOrderinfo(wxBukaInfo, mailPrice);
        } else {
            return AjaxResult.error("领卡类型错误");
        }


        Integer status = jsonObject.getInteger("status");
        if (status == 1) {
            result.put("mailPrice", mailPrice);

            AES aes = SecureUtil.aes("3MH0P00OPS3OOROE".getBytes());
            jsonObject.remove("status");
            jsonObject.remove("error_desc");
            String content = aes.encryptBase64(jsonObject.toJSONString());
            String payUrl = StrUtil.format("https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxc2bd458948e845a0&redirect_uri={}&response_type=code&scope=snsapi_base&state={}&connect_redirect=1", "http://dingzhou.sjzydrj.net/index.php/home/Pay/wxpay/", URLEncoder.ALL.encode(content, StandardCharsets.UTF_8));
            result.put("payUrl", payUrl);

            return AjaxResult.success(result);
        } else {
            String errorDesc = jsonObject.getString("error_desc");
            return AjaxResult.error(errorDesc);
        }
    }

    /**
     * 查询订单是否已支付
     */
    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("查询订单是否已支付")
    @PostMapping("/orderIsPay")
    public AjaxResult orderIsPay(@RequestBody @Validated OrderInfoParam orderInfoParam) {
        Map<String, Object> result = new HashMap<>();
        String type = orderInfoParam.getType();
        if ("shenling".equals(type)) {
            WxArchives wxArchives = wxArchivesService.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxArchives>().eq(WxArchives::getCardNum, orderInfoParam.getCardNum()).eq(WxArchives::getOrderno, orderInfoParam.getOrderno()));
            if (wxArchives == null) {
                return AjaxResult.error("未查询到社保卡信息");
            }
            result.put("isPay", wxArchives.getIsZhifu());
        } else if ("buhuanka".equals(type)) {
            WxBukaInfo wxBukaInfo = wxBukaInfoService.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxBukaInfo>().eq(WxBukaInfo::getIdcardno, orderInfoParam.getCardNum()).eq(WxBukaInfo::getOrderno, orderInfoParam.getOrderno()));
            if (wxBukaInfo == null) {
                return AjaxResult.error("未查询到社保卡信息");
            }
            result.put("isPay", wxBukaInfo.getIsZhifu());
        } else {
            return AjaxResult.error("领卡类型错误");
        }
        return AjaxResult.success(result);
    }


    /**
     * 可申请退邮寄费列表 - 微信
     */
    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("可申请邮寄退费列表 - 微信")
    @PostMapping("/wxReturnList")
    public AjaxResult wxReturnList(@RequestBody @Validated WxReturnListParam wxReturnListParam) {
        List<WxArchives> wxArchivesList = wxArchivesService.selectListByLambdaQueryWrapper(new LambdaQueryWrapper<WxArchives>()
                .eq(WxArchives::getIsZhifu, 1)
                .eq(WxArchives::getOpenid, wxReturnListParam.getOpenId())
                .notIn(WxArchives::getExamineStatus, 1));
        return AjaxResult.success(wxArchivesList);
    }

    /**
     * 申请退邮寄费 - 微信
     */
    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("申请退邮寄费 - 微信")
    @PostMapping("/wxReturnOrder")
    public AjaxResult wxReturnOrder(@RequestBody @Validated WxReturnOrderParam wxReturnOrderParam) {
        WxArchives wxArchives = wxArchivesService.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxArchives>().eq(WxArchives::getIsZhifu, 1).eq(WxArchives::getOrderno, wxReturnOrderParam.getOrderno()).notIn(WxArchives::getExamineStatus, 1));
        if (wxArchives == null) {
            return AjaxResult.error("未查到满足退费条件的订单");
        }
        wxArchives.setReturnFlag(1);
        wxArchives.setReturnReason("申请合并邮寄");
        wxArchives.setReturnTime(new Date());
        wxArchivesService.updateById(wxArchives);
        return AjaxResult.success();
    }

    /**
     * 申请退邮寄费
     */
    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("申请退邮寄费")
    @PostMapping("/returnOrder")
    public AjaxResult returnOrder(@RequestBody @Validated ReturnOrderParam returnOrderParam) {
        WxArchives wxArchives = wxArchivesService.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxArchives>().eq(WxArchives::getIsZhifu, 1).eq(WxArchives::getCardNum, returnOrderParam.getSfzh()).eq(WxArchives::getName, returnOrderParam.getXm()).eq(WxArchives::getPhone, returnOrderParam.getPhone()).notIn(WxArchives::getExamineStatus, 1));
        if (wxArchives == null) {
            return AjaxResult.error("未查到满足退费条件的订单");
        }
        wxArchives.setReturnFlag(1);
        wxArchives.setReturnReason("申请合并邮寄");
        wxArchives.setReturnTime(new Date());
        wxArchivesService.updateById(wxArchives);
        return AjaxResult.success();
    }

    /**
     * 取消补卡申请原因
     */
    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("取消补卡申请原因")
    @PostMapping("/reasonList")
    public AjaxResult reasonList(@RequestBody @Validated CodeParam codeParam) {
        List<String> list = new ArrayList<>();
        list.add("卡已找到");
        list.add("线下网点办理");
        list.add("其他");
        return AjaxResult.success(list);
    }

    /**
     * 人员信息查询
     */
    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("人员信息查询")
    @PostMapping("/ryxxcx")
    public AjaxResult ryxxcx(@RequestBody @Validated ZkjdcxParam zkjdcxParam) {
        // 人员信息查询
        String keyInfo = zkjdcxParam.getSfzh() + "|" + zkjdcxParam.getXm();
        Result result = sbkService.getResult("0831011", keyInfo);
        if (!"200".equals(result.getStatusCode())) {
            return AjaxResult.error(result.getMessage());
        }
        Map<String, String> data = (Map<String, String>) result.getData();
        return AjaxResult.success("操作成功", data.get("ReturnResult"));
    }

    /**
     * 新办卡资格校验
     */
    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("新办卡资格校验")
    @PostMapping("/xbkzgjy")
    public AjaxResult xbkzgjy(@RequestBody @Validated XbkzgjyParam xbkzgjyParam) throws IOException {
        List<String> whiteList = new ArrayList<>();
        whiteList.add("130125200002094513"); // 刘元博
        whiteList.add("130133200001022737"); // 张梦泽
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

        String code = xbkzgjyParam.getCode();
        String source = "0";
        switch (code) {
            case "f54791a523474e12b7c183f17c3cbcc2":
                source = "4";
                break;
            case "f1a04d853c2f8212210267ebbda5eadb":
                source = "1";
                break;
        }

        WxArchives wxArchives = wxArchivesService.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxArchives>().eq(WxArchives::getCardNum, xbkzgjyParam.getSfzh()).eq(WxArchives::getName, xbkzgjyParam.getXm()));
        if (wxArchives != null) {
            WxInfomationImg wxInfomationImg = wxInfomationImgService.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxInfomationImg>().eq(WxInfomationImg::getCardNum, xbkzgjyParam.getSfzh()));
            wxArchives.setWxInfomationImg(wxInfomationImg);
            // 审核状态：0代表未审核 1代表审核通过 2代表审核未通过
            String examineStatus = wxArchives.getExamineStatus();
            if ("0".equals(examineStatus)) {
                if ("9".equals(wxArchives.getStepStatus())) {
                    return AjaxResult.error("采集信息审核中");
                } else {
                    if (source.equals(wxArchives.getSource())) {
                        return new AjaxResult(201, "请继续填写采集信息", wxArchives);
                    } else {
                        return AjaxResult.error("请继续在首次申领渠道修改信息");
                    }
                }
            } else if ("1".equals(examineStatus)) {
                return AjaxResult.error("采集信息审核已通过");
            } else if ("2".equals(examineStatus)) {
                if (source.equals(wxArchives.getSource())) {
                    return new AjaxResult(201, "采集信息审核未通过", wxArchives);
                } else {
                    return AjaxResult.error("请继续在首次申领渠道修改信息");
                }
            }
        }
        return AjaxResult.success("您可以新采集信息");
    }

    /**
     * 补换卡数据回显
     */
    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("补换卡数据回显")
    @PostMapping("/bhksjhx")
    public AjaxResult bhksjhx(@RequestBody @Validated BhksjhxParam bhksjhxParam) {
        String code = bhksjhxParam.getCode();
        int source = 0;
        switch (code) {
            case "f54791a523474e12b7c183f17c3cbcc2":
                source = 4;
                break;
            case "f1a04d853c2f8212210267ebbda5eadb":
                source = 1;
                break;
        }

        WxBukaInfo wxBukaInfo = wxBukaInfoService.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxBukaInfo>().eq(WxBukaInfo::getIdcardno, bhksjhxParam.getSfzh())
                .in(WxBukaInfo::getExamineStatus, 0, 2)
                .orderByDesc(WxBukaInfo::getAddTime));
        if (wxBukaInfo != null) {
            System.out.println(wxBukaInfo);
            WxBukaInfoImg wxBukaInfoImg = wxBukaInfoImgService.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxBukaInfoImg>().eq(WxBukaInfoImg::getOrderno, wxBukaInfo.getOrderno()));
            wxBukaInfo.setWxBukaInfoImg(wxBukaInfoImg);
            // 审核状态：0代表未审核 1代表审核通过 2代表审核未通过
            String examineStatus = String.valueOf(wxBukaInfo.getExamineStatus());
            if ("0".equals(examineStatus)) {
                if (wxBukaInfo.getStepStatus() == 9) {
                    return AjaxResult.error("采集信息审核中");
                } else if (wxBukaInfo.getStepStatus() == 999) {
                    if (source == wxBukaInfo.getSource()) {
                        return new AjaxResult(201, "请继续填写采集信息", wxBukaInfo);
                    } else {
                        return AjaxResult.error("请继续在首次申领渠道修改信息");
                    }
                } else {
                    return AjaxResult.success("您可以新采集信息");
                }
            } else if ("2".equals(examineStatus)) {
                if (source == wxBukaInfo.getSource()) {
                    return new AjaxResult(201, "采集信息审核未通过", wxBukaInfo);
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
//    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
//    @ApiOperation("重新采集数据回显")
//    @PostMapping("/getCollectInfo")
//    public AjaxResult getCollectInfo(@RequestBody @Validated XbkzgjyParam xbkzgjyParam) {
//        WxArchives wxArchives = wxArchivesService.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxArchives>().eq(WxArchives::getCardNum, xbkzgjyParam.getSfzh()).eq(WxArchives::getName, xbkzgjyParam.getXm()));
//        if (wxArchives == null) {
//            return AjaxResult.error("未查到采集信息");
//        }
//        WxInfomationImg wxInfomationImg = wxInfomationImgService.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxInfomationImg>().eq(WxInfomationImg::getCardNum, xbkzgjyParam.getSfzh()));
//        wxArchives.setWxInfomationImg(wxInfomationImg);
//        return AjaxResult.success(wxArchives);
//    }

    /**
     * 邮寄物流信息
     */
    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("补换卡记录列表")
    @PostMapping("/buhuankaList")
    public AjaxResult buhuankaList(@RequestBody @Validated BhksjhxParam bhksjhxParam) {
        List<Map<String, Object>> mapList = new ArrayList<>();

        String code = bhksjhxParam.getCode();
        int source = 0;
        switch (code) {
            case "f54791a523474e12b7c183f17c3cbcc2":
                source = 4;
                break;
            case "f1a04d853c2f8212210267ebbda5eadb":
                source = 1;
                break;
        }
        LambdaQueryWrapper<WxBukaInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WxBukaInfo::getIdcardno, bhksjhxParam.getSfzh());
        queryWrapper.eq(WxBukaInfo::getSource, source);
        List<WxBukaInfo> wxBukaInfoList = wxBukaInfoService.selectListByLambdaQueryWrapper(queryWrapper);
        for (WxBukaInfo wxBukaInfo : wxBukaInfoList) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", wxBukaInfo.getId());
            map.put("newName", wxBukaInfo.getNewName());
            map.put("idcardno", wxBukaInfo.getIdcardno());
            map.put("addTime", wxBukaInfo.getAddTime());
            map.put("orderno", wxBukaInfo.getOrderno());
            if (wxBukaInfo.getStepStatus() == 999) {
                map.put("state", "未完成");
            }
            if (wxBukaInfo.getStepStatus() == 9) {
                if (wxBukaInfo.getExamineStatus() == 0) {
                    map.put("state", "审核中");
                } else if (wxBukaInfo.getExamineStatus() == 1) {
                    map.put("state", "审核通过");
                } else if (wxBukaInfo.getExamineStatus() == 2) {
                    map.put("state", "审核驳回");
                }
            }
            mapList.add(map);
        }

        return AjaxResult.success(mapList);
    }

    /**
     * 邮寄物流信息
     */
    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("邮寄物流信息")
    @PostMapping("/mailInfo")
    public AjaxResult mailInfo(@RequestBody @Validated MailInfoParam mailInfoParam) {
        return AjaxResult.success(smartCityService.selectMailInfoByWldh(mailInfoParam.getWldh()));
    }

    /**
     * 审核信息查询
     */
    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("审核信息查询")
    @PostMapping("/examineInfo")
    public AjaxResult examineInfo(@RequestBody @Validated ExamineInfoParam examineInfoParam) {
        Map<String, Object> result = new HashMap<>();

        String type = examineInfoParam.getType();
        if (type.equals("shenling")) {
            WxArchives wxArchives = wxArchivesService.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxArchives>().eq(WxArchives::getCardNum, examineInfoParam.getSfzh()).eq(WxArchives::getName, examineInfoParam.getXm()));
            if (wxArchives == null) {
                return AjaxResult.error("未查到申领数据");
            }
            int examineStatus = Integer.parseInt(wxArchives.getExamineStatus());
//            if (examineStatus == 999) {
//                return AjaxResult.error("请完善采集信息");
//            }
            result.put("examineStatus", examineStatus); // 审核时间
            result.put("examineTime", wxArchives.getExamineTime()); // 审核时间
            result.put("rejectReason", wxArchives.getReason()); // 驳回原因
        } else if (type.equals("buhuanka")) {
            WxBukaInfo wxBukaInfo = wxBukaInfoService.selectOneByLambdaQueryWrapper(new LambdaQueryWrapper<WxBukaInfo>().eq(WxBukaInfo::getIdcardno, examineInfoParam.getSfzh()).eq(WxBukaInfo::getKaName, examineInfoParam.getXm()));
            if (wxBukaInfo == null) {
                return AjaxResult.error("未查到补换卡数据");
            }
            Integer examineStatus = wxBukaInfo.getExamineStatus();
//            if (examineStatus == 999) {
//                return AjaxResult.error("请完善采集信息");
//            }
            result.put("examineStatus", examineStatus); // 审核时间
            result.put("examineTime", wxBukaInfo.getExamineTime()); // 审核时间
            result.put("rejectReason", wxBukaInfo.getRejectReason()); // 驳回原因
        } else {
            return AjaxResult.error("领卡类型错误");
        }

        return AjaxResult.success(result);
    }

    // ————————————————————————————  其他单独接口  ————————————————————————————

    /**
     * 身份证号密码校验
     */
    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("身份证号密码校验")
    @PostMapping("/account")
    public AjaxResult account(@RequestBody @Validated EncryptParam encryptParam) throws IOException {
        AccountParam accountParam = JSON.parseObject(AESUtils.decrypt(encryptParam.getBody(), AESUtils.KEY), AccountParam.class);

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

        String encrypt = AESUtils.encrypt(JSON.toJSONString(sbkUser), AESUtils.KEY);
        return AjaxResult.success("操作成功", encrypt);
    }

    /**
     * 人员基础信息变更
     */
    @Log(title = "电子社保卡", businessType = BusinessType.RYJCXXBG)
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
        // 社保卡基本信息查询
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
    @Log(title = "电子社保卡", businessType = BusinessType.JG)
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
     * 正式挂失
     */
    @Log(title = "电子社保卡", businessType = BusinessType.ZSGS)
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
    @Log(title = "电子社保卡", businessType = BusinessType.FWMMCZ)
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
    @Log(title = "电子社保卡", businessType = BusinessType.FWMMXG)
    @ApiOperation("服务密码修改")
    @PostMapping("/fwmmxg")
    public AjaxResult fwmmxg(@RequestBody @Validated EncryptParam encryptParam) throws IOException {
        FwmmxgParam fwmmxgParam = JSON.parseObject(AESUtils.decrypt(encryptParam.getBody(), AESUtils.KEY), FwmmxgParam.class);
        // 社保卡基本信息查询
        Result result = sbkService.getResult("0811014", fwmmxgParam.getAac002() + "||");
        if (!"200".equals(result.getStatusCode())) {
            return AjaxResult.error(result.getMessage());
        }
        Map<String, String> data = (Map<String, String>) result.getData();
        String jbxxcx = ParamUtils.decrypted(SbkParamUtils.PRIVATEKEY, data.get("ReturnResult"));
        String[] jbxxcxArr = jbxxcx.split("\\|");

        fwmmxgParam.setAaz500(jbxxcxArr[10]);
        fwmmxgParam.setAac002(jbxxcxArr[1]);
        fwmmxgParam.setAac003(jbxxcxArr[0]);
        // 服务密码修改
        String keyInfo = fwmmxgParam.getAac002() + "|" + fwmmxgParam.getAac003() + "|" + fwmmxgParam.getAaz500() + "|" + fwmmxgParam.getOldPassword() + "|" + fwmmxgParam.getNewPassword();
        Result fwmmxgResult = sbkService.getResult("0821020", keyInfo);
        return toAjax(fwmmxgResult);
    }

    // ————————————————————————————  字典相关接口  ————————————————————————————

    /**
     * 查询单位信息列表
     */
    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("查询单位信息列表")
    @PostMapping("/unitinfoShi")
    public AjaxResult unitinfoShi(@RequestBody @Validated UnitinfoShiParam unitinfoShiParam) {
        UnitinfoShi unitinfoShi = new UnitinfoShi();
        unitinfoShi.setUnitname(unitinfoShiParam.getUnitname());
        List<UnitinfoShi> list = unitinfoShiService.selectUnitinfoShiList(unitinfoShi);
        return AjaxResult.success(list);
    }

    /**
     * 查询银行信息列表
     */
    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("查询银行信息列表")
    @PostMapping("/wxBukaBank")
    public AjaxResult wxBukaBank(@RequestBody @Validated CodeParam codeParam) {
        return AjaxResult.success(wxBukaBankService.listAll());
    }

    /**
     * 查询代办人关系列表
     */
    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("查询代办人关系列表")
    @PostMapping("/wxRelation")
    public AjaxResult wxRelation(@RequestBody @Validated CodeParam codeParam) {
        return AjaxResult.success(wxRelationService.listAll());
    }

    /**
     * 查询户口性质列表
     */
    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("查询户口性质列表")
    @PostMapping("/wxResidenceType")
    public AjaxResult wxResidenceType(@RequestBody @Validated CodeParam codeParam) {
        return AjaxResult.success(wxResidenceTypeService.listAll());
    }

    /**
     * 查询职业信息列表
     */
    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("查询职业信息列表")
    @PostMapping("/wxOccupation")
    public AjaxResult wxOccupation(@RequestBody @Validated CodeParam codeParam) {
        return AjaxResult.success(wxOccupationService.listAll());
    }

    /**
     * 查询民族信息列表
     */
    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("查询民族信息列表")
    @PostMapping("/wxMingzu")
    public AjaxResult wxMingzu(@RequestBody @Validated CodeParam codeParam) {
        return AjaxResult.success(wxMingzuService.listAll());
    }

    @Log(title = "电子社保卡", businessType = BusinessType.OTHER)
    @ApiOperation("制卡进度查询")
    @PostMapping("/zkjdcx")
    public AjaxResult zkjdcx(@RequestBody @Validated ZkjdcxParam zkjdcxParam) throws IOException {
        Map<String, Object> resultMap = new HashMap<>();
        // 申领信息查询
        Result result = sbkService.getResult("0811012", zkjdcxParam.getSfzh() + "|" + zkjdcxParam.getXm());
        if (!"200".equals(result.getStatusCode())) {
            resultMap.put("shenling", smartCityService.getShenLingData(zkjdcxParam, "100"));
            resultMap.put("buhuanka", new ArrayList<>());
        } else {
            Map<String, String> data = (Map<String, String>) result.getData();
            String slxxcx = ParamUtils.decrypted(SbkParamUtils.PRIVATEKEY, data.get("ReturnResult"));
            String[] slxxcxArr = slxxcx.split("\\|");

            resultMap.put("shenling", smartCityService.getShenLingData(zkjdcxParam, slxxcxArr[12]));
            resultMap.put("buhuanka", smartCityService.getBuHuanKaData(zkjdcxParam, slxxcxArr[12]));
        }

        return AjaxResult.success(resultMap);
    }
}
