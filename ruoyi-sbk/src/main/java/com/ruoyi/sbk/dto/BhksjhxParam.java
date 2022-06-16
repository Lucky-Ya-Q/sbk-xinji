package com.ruoyi.sbk.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
@Data
public class BhksjhxParam {
    @ApiModelProperty("授权码")
    private String code;
    @NotBlank(message = "身份证号不能为空")
    @ApiModelProperty("身份证号")
    private String sfzh;
}
