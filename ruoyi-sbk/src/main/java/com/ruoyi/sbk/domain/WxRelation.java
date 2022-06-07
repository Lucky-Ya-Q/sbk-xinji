package com.ruoyi.sbk.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 代办人关系对象 wx_relation
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WxRelation extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(hidden = true)
    private String id;
    private String relation;
}
