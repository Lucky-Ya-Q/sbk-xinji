package com.ruoyi.sbk.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserInfoParam {
    @ApiModelProperty("授权码")
    private String code;
    @NotBlank(message = "微信授权码不能为空")
    @ApiModelProperty("微信授权码")
    private String wxCode;
}
