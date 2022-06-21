package com.ruoyi.sbk.domain;

import lombok.Data;

@Data
public class WxSmspersonEms {
    /**
     * 顺序号
     */
    private Integer id;

    /**
     * 邮件号
     */
    private String mailnum;

    /**
     * 收件人姓名
     */
    private String personname;

    /**
     * 收件人联系方式
     */
    private String personphone;

    /**
     * 身份证号
     */
    private String idcard;

    /**
     * 收件人地址
     */
    private String address;

    /**
     * 导入时间
     */
    private Integer importtime;

    /**
     * 是否发微信消息 0：未发 1：已发
     */
    private Integer sendflag;
}

