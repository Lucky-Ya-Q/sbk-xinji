package com.ruoyi.sbk.dto;

import com.ruoyi.common.core.domain.entity.SbkUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 服务密码修改参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FwmmxgParam extends SbkUser {
    @NotBlank(message = "旧密码不能为空")
    @ApiModelProperty("旧密码")
    private String oldPassword;
    @NotBlank(message = "新密码不能为空")
    @ApiModelProperty("新密码")
    private String newPassword;
}
