package com.ruoyi.sbk.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 新办卡资格校验参数
 */
@Data
public class XbkzgjyParam {
    @NotBlank(message = "身份证号不能为空")
    @ApiModelProperty("身份证号")
    private String sfzh;
    @NotBlank(message = "姓名不能为空")
    @ApiModelProperty("姓名")
    private String xm;
}
