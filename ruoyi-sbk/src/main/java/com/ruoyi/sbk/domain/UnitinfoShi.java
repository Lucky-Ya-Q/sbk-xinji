package com.ruoyi.sbk.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 单位信息对象 unitinfo_shi
 *
 * @author lucky-ya-q
 * @date 2022-03-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UnitinfoShi extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(hidden = true)
    private String unitcode;
    @ApiModelProperty(hidden = true)
    private String centercode;
    private String unitname;
    @ApiModelProperty(hidden = true)
    private String unitcodeDs;
    @ApiModelProperty(hidden = true)
    private String flag;
    @ApiModelProperty(hidden = true)
    private String unittype;
}
