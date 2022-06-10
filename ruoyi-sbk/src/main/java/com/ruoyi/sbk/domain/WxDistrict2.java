package com.ruoyi.sbk.domain;

import lombok.Data;

@Data
public class WxDistrict2 {
    /**
     *
     */
    private Integer id;

    /**
     *
     */
    private String city;

    /**
     *
     */
    private String district;

    /**
     *
     */
    private String code;

    /**
     *
     */
    private Integer parentId;

    /**
     *
     */
    private String lingkaType;

    /**
     * 邮寄费
     */
    private Integer mailPrice;
}

