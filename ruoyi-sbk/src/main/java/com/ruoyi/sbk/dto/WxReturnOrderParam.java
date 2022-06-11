package com.ruoyi.sbk.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class WxReturnOrderParam {
    @ApiModelProperty("授权码")
    private String code;
    @NotBlank(message = "订单号不能为空")
    @ApiModelProperty("订单号")
    private String orderno;
}
