package com.ruoyi.sbk.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 加密参数
 */
@Data
public class EncryptParam {
    @NotBlank(message = "授权码不能为空")
    @ApiModelProperty("授权码")
    private String code;
    @NotBlank(message = "请求体不能为空")
    @ApiModelProperty("请求体")
    private String body;
}
