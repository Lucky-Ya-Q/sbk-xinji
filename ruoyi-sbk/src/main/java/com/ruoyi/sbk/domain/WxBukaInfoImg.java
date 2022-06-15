package com.ruoyi.sbk.domain;

import lombok.Data;

import java.util.Date;

@Data
public class WxBukaInfoImg {
    /**
     *
     */
    private Integer id;

    /**
     * 身份证号
     */
    private String idcardno;

    /**
     *
     */
    private String orderno;

    /**
     *
     */
    private Date addTime;

    /**
     *
     */
    private String zhengCardUrl;

    /**
     *
     */
    private String fanCardUrl;

    /**
     * 户口簿首页地址
     */
    private String hukouUrlHead;

    /**
     *
     */
    private String hukouUrl;

    /**
     * 申请单路径
     */
    private String jpgUrl;

    /**
     *
     */
    private String isJpg;

    /**
     *
     */
    private String daiZhengUrl;

    /**
     *
     */
    private String daiFanUrl;

    /**
     *
     */
    private String daiHukouUrl;

    /**
     *
     */
    private String qianmingUrl;

    /**
     * 电子照片路径
     */
    private String headUrl;

    /**
     * 卡头像照片
     */
    private String kaHeadUrl;

    /**
     * 更换头像照片
     */
    private String newHeadUrl;

    /**
     * 业务单路径
     */
    private String yewudanUrl;
}

