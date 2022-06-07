package com.ruoyi.sbk.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(hidden = true)
    private Integer id;
    private String name;
    @ApiModelProperty(hidden = true)
    private String code;
    @ApiModelProperty(hidden = true)
    private Date addTime;
    @ApiModelProperty(hidden = true)
    private Integer addId;
    @ApiModelProperty(hidden = true)
    private Integer orderId;
    @ApiModelProperty(hidden = true)
    private Integer flag;
    @ApiModelProperty(hidden = true)
    private Long status;
}
