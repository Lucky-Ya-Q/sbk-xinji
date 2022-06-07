package com.ruoyi.sbk.dto;

import com.ruoyi.common.core.domain.entity.SbkUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 服务密码修改参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RyjcxxbgParam extends SbkUser {
    @NotBlank(message = "居住地址不能为空")
    @ApiModelProperty("居住地址")
    private String jzdz;
    @NotBlank(message = "移动电话不能为空")
    @ApiModelProperty("移动电话")
    private String yddh;
    @NotNull(message = "证件有效期起始日期不能为空")
    @ApiModelProperty("证件有效期起始日期")
    private String qsrq;
    @NotNull(message = "证件有效期终止日期不能为空")
    @ApiModelProperty("证件有效期终止日期")
    private String zzrq;
    @NotNull(message = "职业不能为空")
    @ApiModelProperty("职业")
    private String zy;
}
