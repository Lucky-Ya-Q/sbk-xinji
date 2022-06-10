package com.ruoyi.sbk.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CodeParam {
    @ApiModelProperty("授权码")
    private String code;
}
