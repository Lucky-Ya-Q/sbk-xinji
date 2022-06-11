package com.ruoyi.sbk.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SbkUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.framework.config.ServerConfig;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.sbk.domain.*;
import com.ruoyi.sbk.dto.Result;
import com.ruoyi.sbk.dto.XbkzgjyParam;
import com.ruoyi.sbk.enums.ServiceType;
import com.ruoyi.sbk.factory.SbkAsyncFactory;
import com.ruoyi.sbk.service.*;
import com.ruoyi.sbk.util.SbkParamUtils;
import com.tecsun.sm.utils.ParamUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Api(tags = "社保卡公共平台")
@RestController
@RequestMapping("/sbk/common")
public class SbkCommonController {
    @Autowired
    private ServerConfig serverConfig;
    @Autowired
    private SbkService sbkService;
    @Autowired
    private ISbkFwwdService sbkFwwdService;
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
    private IWxArchivesService wxArchivesService;
    @Autowired
    private IWxInfomationImgService wxInfomationImgService;

    /**
     * 基本信息查询
     */
    @ApiOperation("基本信息查询")
    @GetMapping("/jbxxcx")
    public AjaxResult jbxxcx(SbkUser sbkUser) throws IOException {
        // 社保卡基本信息查询
        Result result = sbkService.getResult("0811014", sbkUser.getAac002() + "||");
        if (!"200".equals(result.getStatusCode())) {
            return AjaxResult.error(result.getMessage());
        }
        Map<String, String> data = (Map<String, String>) result.getData();
        String jbxxcx = ParamUtils.decrypted(SbkParamUtils.PRIVATEKEY, data.get("ReturnResult"));
        String[] jbxxcxArr = jbxxcx.split("\\|");
        return AjaxResult.success(jbxxcxArr);
    }

    /**
     * 查询所有社保卡服务网点列表
     */
    @ApiOperation("查询所有社保卡服务网点列表")
    @GetMapping("/listAll")
    public AjaxResult listAll() {
        LambdaQueryWrapper<SbkFwwd> sbkFwwdLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sbkFwwdLambdaQueryWrapper.eq(SbkFwwd::getState, 1);
        return AjaxResult.success(sbkFwwdService.list(sbkFwwdLambdaQueryWrapper));
    }

    /**
     * 上传证件照
     */
    @ApiOperation("上传证件照")
    @PostMapping("/upload")
    public AjaxResult upload(MultipartFile file, String sfzh, String type) {
        String[] types = {"head", "shen_zheng", "shen_fan", "shen_hu", "shen_hu_head", "dai_zheng", "dai_fan", "dai_hu", "qianming"};
        List<String> typeList = Arrays.asList(types);
        if (!typeList.contains(type)) {
            return AjaxResult.error("目录类型不合法", types);
        }
        if (!IdcardUtil.isValidCard(sfzh)) {
            return AjaxResult.error("身份证号码格式错误");
        }
        try {
            // 上传文件路径
            String filePath = RuoYiConfig.getCaijiPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file, type, sfzh);
            String url = serverConfig.getUrl() + fileName;
            AjaxResult ajax = AjaxResult.success();
            ajax.put("fileName", fileName.substring("/profile".length()));
            ajax.put("url", url);
            return ajax;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 新办卡资格校验
     */
    @ApiOperation("新办卡资格校验")
    @GetMapping("/xbkzgjy")
    public AjaxResult xbkzgjy(@Validated XbkzgjyParam xbkzgjyParam) throws IOException {
        List<String> whiteList = new ArrayList<>();
        whiteList.add("130133200001022737");
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

        xbkzgjyParam.setXm(null);
        WxArchives wxArchives = wxArchivesService.selectOneBySfzhAndXm(xbkzgjyParam);
        if (wxArchives != null) {
            // 审核状态：0代表未审核 1代表审核通过 2代表审核未通过
            String examineStatus = wxArchives.getExamineStatus();
            if ("0".equals(examineStatus)) {
                return AjaxResult.error("社保卡采集信息审核中……");
            } else if ("1".equals(examineStatus)) {
                return AjaxResult.error("社保卡采集信息审核已通过");
            } else if ("2".equals(examineStatus)) {
                // 0代表未支付  1代表支付
                Integer isZhifu = wxArchives.getIsZhifu();
                if (isZhifu == 1) {
                    return AjaxResult.error("请继续在公众号修改信息");
                } else if (isZhifu == 0) {
                    String cardNum = wxArchives.getCardNum();
                    WxInfomationImg wxInfomationImg = wxInfomationImgService.selectOneBySfzh(cardNum);
                    wxArchives.setWxInfomationImg(wxInfomationImg);
                    return new AjaxResult(201, "审核未通过", wxArchives);
                }
            }
        }
        return AjaxResult.success("新采集信息");
    }

    /**
     * 申领
     */
    @ApiOperation("申领")
    @PutMapping("/shenling")
    public AjaxResult shenling(@RequestBody WxArchives wxArchives) {
        WxInfomationImg wxInfomationImg = wxArchives.getWxInfomationImg();
        // 身份证号码
        String cardNum = wxArchives.getCardNum();
        if (!IdcardUtil.isValidCard(cardNum)) {
            return AjaxResult.error("身份证号码格式错误");
        }
        // 性别
        int sex = IdcardUtil.getGenderByIdCard(cardNum);
        wxArchives.setSex(sex == 1 ? "男" : "女");
        // 出生日期
        String birthday = DateUtil.format(DateUtil.parse(IdcardUtil.getBirthByIdCard(cardNum)), "yyyy-MM-dd");
        wxArchives.setBirthday(birthday);
        int age = IdcardUtil.getAgeByIdCard(cardNum);
        wxArchives.setIsAdult(age > 16 ? "0" : "1");

        if ("0".equals(wxArchives.getIsAdult())) {
            // 监护人身份证号码
            String guardianCardNum = wxArchives.getGuardianCardNum();
            if (!IdcardUtil.isValidCard(guardianCardNum)) {
                return AjaxResult.error("监护人身份证号码格式错误");
            }
            // 代办人性别
            wxArchives.setSex(IdcardUtil.getGenderByIdCard(guardianCardNum) == 1 ? "男" : "女");

            // 小于6周 没传头像，设置默认头像
            if (age <= 6) {
                if (StrUtil.isBlank(wxInfomationImg.getHeadImgUrl())) {
                    if (sex == 1) {
                        wxInfomationImg.setHeadImgUrl("/weixinweb/Infomation_upload/boy.jpg");
                    } else {
                        wxInfomationImg.setHeadImgUrl("/weixinweb/Infomation_upload/girl.jpg");
                    }
                }
            }
        }

        // 监护人身份证号码
        String guardianCardNum = wxArchives.getGuardianCardNum();
        // 判断是否有代办人
        if (StrUtil.isNotEmpty(guardianCardNum)) {
            if (!IdcardUtil.isValidCard(guardianCardNum)) {
                return AjaxResult.error("监护人身份证号码格式错误");
            }
            // 代办人性别
            wxArchives.setSex(IdcardUtil.getGenderByIdCard(guardianCardNum) == 1 ? "男" : "女");
        }
        // 申领步骤 未完成签名前为999 完成申领为9
        wxArchives.setStepStatus("9");
        // 检索码 最大值+1
        wxArchives.setPersonid(String.valueOf(wxArchivesService.selectPersonidByMax() + 1));
        // 审核状态：0代表未审核 1代表审核通过 2代表审核未通过
        wxArchives.setExamineStatus("0");
        wxArchives.setAddTime(new Date());

        wxInfomationImg.setCardNum(cardNum);
        // 检索码 最大值+1
        wxInfomationImg.setPersonid(String.valueOf(wxInfomationImgService.selectPersonidByMax() + 1));
        wxArchivesService.saveArchivesAndImg(wxArchives);

        // 记录日志
        LoginUser loginUser = null;
        try {
            loginUser = SecurityUtils.getLoginUser();
        } catch (ServiceException e) {
            log.info(e.getMessage());
        }
        SbkUser sbkUser = new SbkUser();
        sbkUser.setAac002(cardNum);
        sbkUser.setAac003(wxArchives.getName());
        if (loginUser == null) {
            // 个人办理
            AsyncManager.me().execute(SbkAsyncFactory.recordOper(sbkUser, ServiceType.SL));
        } else {
            // 大厅办理
            AsyncManager.me().execute(SbkAsyncFactory.recordOper(loginUser, sbkUser, ServiceType.SL));
        }
        return AjaxResult.success();
    }

    /**
     * 修改申领信息
     */
    @ApiOperation("修改申领信息")
    @PutMapping("/updateShenling")
    public AjaxResult updateShenling(@RequestBody WxArchives wxArchives) {
        // 身份证号码
        String cardNum = wxArchives.getCardNum();
        if (!IdcardUtil.isValidCard(cardNum)) {
            return AjaxResult.error("身份证号码格式错误");
        }
        // 性别
        int sex = IdcardUtil.getGenderByIdCard(cardNum);
        wxArchives.setSex(sex == 1 ? "男" : "女");
        // 出生日期
        String birthday = DateUtil.format(DateUtil.parse(IdcardUtil.getBirthByIdCard(cardNum)), "yyyy-MM-dd");
        wxArchives.setBirthday(birthday);
        int age = IdcardUtil.getAgeByIdCard(cardNum);
        wxArchives.setIsAdult(age > 16 ? "1" : "0");

        if ("0".equals(wxArchives.getIsAdult())) {
            // 监护人身份证号码
            String guardianCardNum = wxArchives.getGuardianCardNum();
            if (!IdcardUtil.isValidCard(guardianCardNum)) {
                return AjaxResult.error("监护人身份证号码格式错误");
            }
            // 代办人性别
            wxArchives.setSex(IdcardUtil.getGenderByIdCard(guardianCardNum) == 1 ? "男" : "女");

            // 小于6周 没传头像，设置默认头像
            if (age <= 6) {
                WxInfomationImg wxInfomationImg = wxArchives.getWxInfomationImg();
                if (StrUtil.isBlank(wxInfomationImg.getHeadImgUrl())) {
                    if (sex == 1) {
                        wxInfomationImg.setHeadImgUrl("/weixinweb/Infomation_upload/boy.jpg");
                    } else {
                        wxInfomationImg.setHeadImgUrl("/weixinweb/Infomation_upload/girl.jpg");
                    }
                }
            }
        }

        // 监护人身份证号码
        String guardianCardNum = wxArchives.getGuardianCardNum();
        // 判断是否有代办人
        if (StrUtil.isNotEmpty(guardianCardNum)) {
            if (!IdcardUtil.isValidCard(guardianCardNum)) {
                return AjaxResult.error("监护人身份证号码格式错误");
            }
            // 代办人性别
            wxArchives.setSex(IdcardUtil.getGenderByIdCard(guardianCardNum) == 1 ? "男" : "女");
        }

        // 审核状态：0代表未审核 1代表审核通过 2代表审核未通过
        wxArchives.setExamineStatus("0");
        wxArchivesService.updateArchivesAndImg(wxArchives);

        // 记录日志
        LoginUser loginUser = null;
        try {
            loginUser = SecurityUtils.getLoginUser();
        } catch (ServiceException e) {
            log.info(e.getMessage());
        }
        SbkUser sbkUser = new SbkUser();
        sbkUser.setAac002(cardNum);
        sbkUser.setAac003(wxArchives.getName());
        if (loginUser == null) {
            // 个人办理
            AsyncManager.me().execute(SbkAsyncFactory.recordOper(sbkUser, ServiceType.SL));
        } else {
            // 大厅办理
            AsyncManager.me().execute(SbkAsyncFactory.recordOper(loginUser, sbkUser, ServiceType.SL));
        }
        return AjaxResult.success();
    }

    /**
     * 查询单位信息列表
     */
    @ApiOperation("查询单位信息列表")
    @GetMapping("/unitinfoShi")
    public AjaxResult unitinfoShi(UnitinfoShi unitinfoShi) {
        List<UnitinfoShi> list = unitinfoShiService.selectUnitinfoShiList(unitinfoShi);
        return AjaxResult.success(list);
    }

    /**
     * 查询银行信息列表
     */
    @ApiOperation("查询银行信息列表")
    @GetMapping("/wxBukaBank")
    public AjaxResult wxBukaBank(WxBukaBank wxBukaBank) {
        List<WxBukaBank> list = wxBukaBankService.selectWxBukaBankList(wxBukaBank);
        return AjaxResult.success(list);
    }

    /**
     * 查询代办人关系列表
     */
    @ApiOperation("查询代办人关系列表")
    @GetMapping("/wxRelation")
    public AjaxResult wxRelation(WxRelation wxRelation) {
        List<WxRelation> list = wxRelationService.selectWxRelationList(wxRelation);
        return AjaxResult.success(list);
    }

    /**
     * 查询户口性质列表
     */
    @ApiOperation("查询户口性质列表")
    @GetMapping("/wxResidenceType")
    public AjaxResult wxResidenceType(WxResidenceType wxResidenceType) {
        List<WxResidenceType> list = wxResidenceTypeService.selectWxResidenceTypeList(wxResidenceType);
        return AjaxResult.success(list);
    }

    /**
     * 查询职业信息列表
     */
    @ApiOperation("查询职业信息列表")
    @GetMapping("/wxOccupation")
    public AjaxResult wxOccupation(WxOccupation wxOccupation) {
        List<WxOccupation> list = wxOccupationService.selectWxOccupationList(wxOccupation);
        return AjaxResult.success(list);
    }

    /**
     * 查询民族信息列表
     */
    @ApiOperation("查询民族信息列表")
    @GetMapping("/wxMingzu")
    public AjaxResult wxMingzu(WxMingzu wxMingzu) {
        List<WxMingzu> list = wxMingzuService.selectWxMingzuList(wxMingzu);
        return AjaxResult.success(list);
    }
}
