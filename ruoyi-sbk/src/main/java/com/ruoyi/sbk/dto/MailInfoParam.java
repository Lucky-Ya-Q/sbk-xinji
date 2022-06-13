package com.ruoyi.sbk.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
@Data
public class MailInfoParam {
    @ApiModelProperty("授权码")
    private String code;
    @NotBlank(message = "物流单号不能为空")
    @ApiModelProperty("物流单号")
    private String wldh;
}
