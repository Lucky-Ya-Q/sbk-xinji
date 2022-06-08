package com.ruoyi.sbk.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 审核信息查询参数
 */
@Data
public class ExamineInfoParam {
    @NotBlank(message = "审核类型不能为空")
    @ApiModelProperty("审核类型")
    private String type;
    @NotBlank(message = "身份证号不能为空")
    @ApiModelProperty("身份证号")
    private String sfzh;
    @NotBlank(message = "姓名不能为空")
    @ApiModelProperty("姓名")
    private String xm;
}
