package com.ruoyi.sbk.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 制卡进度查询参数
 */
@Data
public class ZkjdcxParam {
    @ApiModelProperty("授权码")
    private String code;
    @NotBlank(message = "身份证号不能为空")
    @ApiModelProperty("身份证号")
    private String sfzh;
    @NotBlank(message = "姓名不能为空")
    @ApiModelProperty("姓名")
    private String xm;
}
