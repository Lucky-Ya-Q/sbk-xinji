package com.ruoyi.sbk.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.security.core.parameters.P;

@Data
public class UnitinfoShiParam {
    @ApiModelProperty("授权码")
    private String code;
    @ApiModelProperty("单位名称")
    private String unitname;
}
