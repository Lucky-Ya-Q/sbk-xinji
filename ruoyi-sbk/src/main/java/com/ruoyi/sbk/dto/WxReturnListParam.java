package com.ruoyi.sbk.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class WxReturnListParam {
    @ApiModelProperty("授权码")
    private String code;
    @NotBlank(message = "openId不能为空")
    @ApiModelProperty("openId")
    private String openId;
}
