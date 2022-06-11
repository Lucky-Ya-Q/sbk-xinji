package com.ruoyi.sbk.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 获取邮寄费支付信息-申领
 */
@Data
public class OrderInfoParam {
    @ApiModelProperty("授权码")
    private String code;
    @NotBlank(message = "审核类型不能为空")
    @ApiModelProperty("审核类型")
    private String type;
    @NotBlank(message = "身份证号不能为空")
    @ApiModelProperty("身份证号")
    private String cardNum;
    @NotBlank(message = "订单号不能为空")
    @ApiModelProperty("订单号")
    private String orderno;
}
