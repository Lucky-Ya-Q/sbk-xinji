package com.ruoyi.sbk.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 图片信息对象 wx_infomation_img
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
@Data
public class WxInfomationImg {
    private static final long serialVersionUID = 1L;
    private Long id;

    /**
     * 0代表没有上传身份证图片，1代表已经上传
     */
    @ApiModelProperty(hidden = true)
    private String isCard;
    @ApiModelProperty(hidden = true)
    private String cardNum;

    /**
     * 检索码
     */
    @ApiModelProperty(hidden = true)
    private String personid;

    /**
     * 头像照片路径
     */
    @ApiModelProperty("头像照片路径")
    private String headImgUrl;

    /**
     * 头像照片添加时间
     */
    @ApiModelProperty(hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date headAddTime;

    /**
     * 身份证正面照-地址
     */
    @ApiModelProperty("身份证正面照-地址")
    private String zhengCardUrl;

    /**
     * 身份证反面照-地址
     */
    @ApiModelProperty("身份证反面照-地址")
    private String fanCardUrl;

    /**
     * 是否上传户口页1 代表上传  0代表未上传
     */
    @ApiModelProperty(hidden = true)
    private String isHukou;

    /**
     * 户口页照-地址
     */
    @ApiModelProperty("户口页照-地址")
    private String hukouUrl;

    /**
     * 未成年人户口本+监护人身份证是否上传：1代表上传
     */
    @ApiModelProperty(hidden = true)
    private String isGuardianCard;

    /**
     * 监护人身份证照-地址
     */
    @ApiModelProperty("监护人身份证照-地址")
    private String guardianCardUrl;

    /**
     * 未成年人户口本+监护人户口页是否上传：1代表上传
     */
    @ApiModelProperty(hidden = true)
    private String isGuardianHukou;

    /**
     * 监护人户口照-地址
     */
    @ApiModelProperty("监护人户口照-地址")
    private String guardianHukouUrl;

    /**
     * 未成年人身份证+监护人身份证是否上传：1代表上传
     */
    @ApiModelProperty(hidden = true)
    private String isChildCard;

    /**
     * 未成年人身份证国徽面
     */
    @ApiModelProperty(hidden = true)
    private String childCardFanUrl;

    /**
     * 未成年人身份证头像面
     */
    @ApiModelProperty(hidden = true)
    private String childCardZhengUrl;

    /**
     * 未成年人身份证+监护人户口本是否上传：1代表上传
     */
    @ApiModelProperty(hidden = true)
    private String isChildHukou;

    /**
     * 签名URL
     */
    @ApiModelProperty("签名URL")
    private String qianmingUrl;

    /**
     * 签名时间
     */
    @ApiModelProperty(hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date qianmingAddTime;

    /**
     * 是否下载 0：未下载   1：已经下载
     */
    @ApiModelProperty(hidden = true)
    private String isDown;
    @ApiModelProperty(hidden = true)
    private String jpgUrl;
    @ApiModelProperty(hidden = true)
    private String isJpg;
    @ApiModelProperty(hidden = true)
    private String isHead;

    /**
     * 监护人身份证反面
     */
    @ApiModelProperty("监护人身份证反面")
    private String guardianCardFanUrl;
}
