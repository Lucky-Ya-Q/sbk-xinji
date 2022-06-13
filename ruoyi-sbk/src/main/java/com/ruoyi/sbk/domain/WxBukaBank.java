package com.ruoyi.sbk.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 银行信息对象 wx_buka_bank
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WxBukaBank extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String code;
    private Date addTime;
    private Integer addId;
    private Integer orderId;
    private Integer flag;
    private Long status;
    private String bankcode;
    private Integer nopayflag;
    private String info;
    private Integer infoflag;
}
