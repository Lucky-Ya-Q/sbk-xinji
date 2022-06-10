package com.ruoyi.sbk.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 基本信息对象 wx_archives
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
@Data
public class WxArchives {
    @TableField(exist = false)
    private String code;
    private static final long serialVersionUID = 1L;
    private Long id;

    /**
     * 步骤状态9代表完成
     */
    @ApiModelProperty(hidden = true)
    private String stepStatus;

    /**
     * 数据来源：1微信公众号  2：12333网站
     */
    @ApiModelProperty("数据来源：1微信公众号  2服务平台  3管理后台  4辛集审批局")
    private String source;

    /**
     * 检索码
     */
    @ApiModelProperty(hidden = true)
    private String personid;

    /**
     * 申请人姓名
     */
    @NotBlank(message = "申请人姓名不能为空")
    @ApiModelProperty("申请人姓名")
    private String name;
    @ApiModelProperty(hidden = true)
    private String openid;
    @ApiModelProperty(hidden = true)
    private String nickname;

    /**
     * 身份证号码
     */
    @NotBlank(message = "身份证号码不能为空")
    @ApiModelProperty("身份证号码")
    private String cardNum;

    /**
     * 通讯地址
     */
    @ApiModelProperty("通讯地址")
    private String communicationAddress;

    /**
     * 详细地址
     */
    @ApiModelProperty("详细地址")
    private String detailedAddress;

    /**
     * 电话
     */
    @ApiModelProperty("电话")
    private String phone;

    /**
     * 证件类型 1代表身份证 2代表户口本
     */
    @ApiModelProperty("证件类型 1代表身份证 2代表户口本")
    private String cardType;

    /**
     * 身份证有效期结束时间
     */
    @ApiModelProperty("身份证有效期结束时间 长期2099-12-31")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    /**
     * 性别
     */
    @ApiModelProperty(hidden = true)
    private String sex;

    /**
     * 出生日期
     */
    @ApiModelProperty(hidden = true)
    private String birthday;

    /**
     * 民族
     */
    @ApiModelProperty("民族")
    private String nation;

    /**
     * 是否成年0代表是成年人    1代表是未成年人
     */
    @ApiModelProperty(hidden = true)
    private String isAdult;

    /**
     * 监护人姓名
     */
    @ApiModelProperty("监护人姓名")
    private String guardianName;

    /**
     * 监护人身份证号码
     */
    @ApiModelProperty("监护人身份证号码")
    private String guardianCardNum;

    /**
     * 监护人电话
     */
    @ApiModelProperty("监护人电话")
    private String guardianPhone;

    /**
     * 监护人证件类型
     */
    @ApiModelProperty("监护人证件类型")
    private String guardianCardType;

    /**
     * 审核状态：0代表未审核 1代表审核通过 2代表审核未通过
     */
    @ApiModelProperty(hidden = true)
    private String examineStatus;

    /**
     * 审核时间
     */
    @ApiModelProperty(hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date examineTime;

    /**
     * 审核工作人员ID
     */
    @ApiModelProperty(hidden = true)
    private Integer examineCid;

    /**
     * 审核不通过的原因
     */
    @ApiModelProperty(hidden = true)
    private String reason;
    @ApiModelProperty("centercode")
    private String centercode;
    @ApiModelProperty("unitcode")
    private String unitcode;
    @ApiModelProperty("unitcodeDs")
    private String unitcodeDs;

    /**
     * 单位
     */
    @ApiModelProperty("单位")
    private String company;

    /**
     * 是否邮寄 0 代表不邮寄  1代表邮寄
     */
    @ApiModelProperty("是否邮寄 0 代表不邮寄  1代表邮寄")
    private String isMail;
    @ApiModelProperty(hidden = true)
    private Date addTime;

    /**
     * 是否导出信息 0 代表否 ，1代表是
     */
    @ApiModelProperty(hidden = true)
    private String isDownInfo;

    /**
     * 是否导出照片 0 代表否 ，1代表是
     */
    @ApiModelProperty(hidden = true)
    private String isDownImg;
    @ApiModelProperty(hidden = true)
    private String isJpg;
    @ApiModelProperty(hidden = true)
    private Date jpgAddTime;

    /**
     * zhifu:打赏; haopin:好评
     */
    @ApiModelProperty(hidden = true)
    private String finish;

    /**
     * 完成时间
     */
    @ApiModelProperty(hidden = true)
    private Long timeEnd;
    @ApiModelProperty(hidden = true)
    private String lng;
    @ApiModelProperty(hidden = true)
    private String lat;
    @ApiModelProperty("银行名称")
    private String bank;
    @ApiModelProperty(hidden = true)
    private String formid;
    @ApiModelProperty(hidden = true)
    private String lingka;

    /**
     * 订单号
     */
    @ApiModelProperty(hidden = true)
    private String orderno;

    /**
     * 邮寄类型 0 到付 1线上付
     */
    @ApiModelProperty("邮寄类型 0 到付 1线上付")
    private Integer mailtype;

    /**
     * 0代表未支付  1代表支付
     */
    @ApiModelProperty(hidden = true)
    private Integer isZhifu;

    /**
     * 支付时间
     */
    @ApiModelProperty(hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date zhifuTime;

    /**
     * 区县编码
     */
    @ApiModelProperty("区县编码")
    private String countyCode;

    /**
     * 合并订单号
     */
    @ApiModelProperty(hidden = true)
    private String combinedOrderno;

    /**
     * 0未合并  1已合并
     */
    @ApiModelProperty(hidden = true)
    private Integer combinedFlag;

    /**
     * 申请退费原因
     */
    @ApiModelProperty(hidden = true)
    private String returnReason;

    /**
     * 申请退费时间
     */
    @ApiModelProperty(hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date returnTime;

    /**
     * 0未申请  1已申请
     */
    @ApiModelProperty(hidden = true)
    private Integer returnFlag;

    /**
     * 0未审核  1已通过 2已驳回
     */
    @ApiModelProperty(hidden = true)
    private Integer examineReturnFlag;

    /**
     * 审核退费时间
     */
    @ApiModelProperty(hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date examineReturnTime;

    /**
     * 驳回申请退费原因
     */
    @ApiModelProperty(hidden = true)
    private String examineReturnReason;

    /**
     * 退费申请操作员
     */
    @ApiModelProperty(hidden = true)
    private Integer examineReturnCid;

    /**
     * 户口性质编号
     */
    @ApiModelProperty("户口性质编号")
    private String residenceTypeId;

    /**
     * 户口所在地
     */
    @ApiModelProperty("户口所在地")
    private String residenceAddress;

    /**
     * 职业编号
     */
    @ApiModelProperty("职业编号")
    private String occupationId;

    /**
     * 证件开始时间
     */
    @ApiModelProperty("证件开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date beginTime;

    /**
     * 代办人关系编号
     */
    @ApiModelProperty("代办人关系编号")
    private String daiRelationId;

    /**
     * 代办人性别
     */
    @ApiModelProperty(hidden = true)
    private String daiSex;

    /**
     * 代办人证件开始时间
     */
    @ApiModelProperty("代办人证件开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date daiBeginTime;

    /**
     * 代办人证件结束时间
     */
    @ApiModelProperty("代办人证件结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date daiEndTime;

    /**
     * 代办人民族编号
     */
    @ApiModelProperty("代办人民族编号")
    private String daiNationId;

    /**
     * 代办人户口所在地区县编码
     */
    @ApiModelProperty("代办人户口所在地区县编码")
    private Integer daiCountyCode;

    /**
     * 收件人地址
     */
    @ApiModelProperty("收件人地址")
    private String communicationAddressMail;

    /**
     * 收件人详细地址
     */
    @ApiModelProperty("收件人详细地址")
    private String detailedAddressMail;

    /**
     * 收件人电话
     */
    @ApiModelProperty("收件人电话")
    private String phoneMail;

    /**
     * 收件人区县编码
     */
    @ApiModelProperty("收件人区县编码")
    private String countyCodeMail;

    /**
     * 银行ID
     */
    @ApiModelProperty("银行ID")
    private Integer bankid;

    @TableField(exist = false)
    private WxInfomationImg wxInfomationImg;
}
