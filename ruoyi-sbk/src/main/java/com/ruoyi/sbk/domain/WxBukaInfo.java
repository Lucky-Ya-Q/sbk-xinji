package com.ruoyi.sbk.domain;

import java.util.Date;

import lombok.Data;

@Data
public class WxBukaInfo {
    /**
     *
     */
    private Integer id;

    /**
     * 身份证号
     */
    private String idcardno;

    /**
     * 1本人 2代办
     */
    private Integer flag;

    /**
     * 步骤状态9代表完成 -1 退款申请提交 -2退款成功 -3退款失败 10已撤销
     */
    private Integer stepStatus;

    /**
     *
     */
    private String openid;

    /**
     *
     */
    private String nickname;

    /**
     * 卡信息 姓名
     */
    private String kaName;

    /**
     * 卡信息 性别
     */
    private Integer kaSex;

    /**
     * 卡信息 民族
     */
    private Integer kaNation;

    /**
     * 卡信息 银行
     */
    private Integer kaBank;

    /**
     * 修改 姓名
     */
    private String newName;

    /**
     * 修改 性别
     */
    private Integer newSex;

    /**
     * 修改 民族
     */
    private Integer newNation;

    /**
     * 修改 银行
     */
    private Integer newBank;

    /**
     * 补换卡的原因
     */
    private String bukaReason;

    /**
     * 卡信息 电话
     */
    private String kaPhone;

    /**
     * 卡信息 出生日期
     */
    private String kaBirthday;

    /**
     * 卡信息 地址
     */
    private String kaAddress;

    /**
     * 归属地代码
     */
    private Integer zoningCode;

    /**
     * 收件人 姓名
     */
    private String shouName;

    /**
     * 收件人 省
     */
    private String shouAddressProv;

    /**
     * 收件人 市
     */
    private String shouAddressCity;

    /**
     * 收件人 区县
     */
    private String shouAddressCoun;

    /**
     * 收件人 详细地址
     */
    private String shouDetailedAddress;

    /**
     * 收件人区划代码
     */
    private Integer shouZoonCode;

    /**
     * 收件人 联系电话
     */
    private String shouPhone;

    /**
     * 收件人 身份证号吗
     */
    private String shouIdcardno;

    /**
     * 1身份证 2户口本
     */
    private Integer daiCardType;

    /**
     * 1身份证 2户口本
     */
    private Integer cardType;

    /**
     *
     */
    private Date addTime;

    /**
     * 审核人ID
     */
    private Integer examineCid;

    /**
     * 审核状态：0代表未审核 1代表审核通过 2代表审核未通过  3代表已提交退款申请  4退款申请通过 5退款申请驳回
     */
    private Integer examineStatus;

    /**
     * 审核时间
     */
    private Date examineTime;

    /**
     * 驳回原因
     */
    private String rejectReason;

    /**
     * 0代表未支付  1代表支付
     */
    private Integer isZhifu;

    /**
     * 支付时间
     */
    private Date zhifuTime;

    /**
     * 发起邮寄时间
     */
    private Integer mailTime;

    /**
     * 邮寄状态：1邮寄中 2邮寄成功 3确认收货成功
     */
    private Integer mailStatus;

    /**
     * 生成的订单号
     */
    private String orderno;

    /**
     * 微信订单号
     */
    private String wxOrderno;

    /**
     * 0未下载 1已下载
     */
    private Integer isDownInfo;

    /**
     * 0未下载 1已下载
     */
    private Integer isDownNewimg;

    /**
     * 0未生成 1已生成 2已下载
     */
    private Integer isJpg;

    /**
     * 生成——操作员
     */
    private Integer jpgCid;

    /**
     *
     */
    private Integer jpgAddTime;

    /**
     * 导出时间 申请表
     */
    private Date daochuTime;

    /**
     * 导出-操作员 申请表
     */
    private Integer daochuCid;

    /**
     * 补换卡渠道：0代表e证e卡公众号 1工商银行
     */
    private Integer source;

    /**
     * 工行网点
     */
    private Integer icbcNetwork;

    /**
     *
     */
    private String lng;

    /**
     *
     */
    private String lat;

    /**
     *
     */
    private String isTicket;

    /**
     *
     */
    private String mailnum;

    /**
     *
     */
    private Date postTime;

    /**
     *
     */
    private String returnReason;

    /**
     * 区县编码
     */
    private String countyCode;

    /**
     * 邮寄类型 0 到付 1线上付
     */
    private Integer mailtype;

    /**
     * 提交撤销补卡时间
     */
    private Date returnAddtime;

    /**
     * 职业编号
     */
    private String occupationId;

    /**
     * 证件开始时间
     */
    private Date beginTime;

    /**
     * 证件结束时间
     */
    private Date endTime;

    /**
     * 代办人关系编号
     */
    private String daiRelationId;

    /**
     * 代办人证件开始时间
     */
    private Date daiBeginTime;

    /**
     * 代办人证件结束时间
     */
    private Date daiEndTime;

    /**
     * 代办人姓名
     */
    private String daiName;

    /**
     * 代办人身份证号
     */
    private String daiIdcardno;
}

