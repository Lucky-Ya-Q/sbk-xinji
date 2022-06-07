package com.ruoyi.sbk.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 户口性质对象 wx_residence_type
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WxResidenceType extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(hidden = true)
    private String id;
    private String residenceType;
}
