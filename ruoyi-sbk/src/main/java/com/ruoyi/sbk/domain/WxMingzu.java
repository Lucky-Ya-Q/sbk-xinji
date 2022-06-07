package com.ruoyi.sbk.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 民族信息对象 wx_mingzu
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WxMingzu extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(hidden = true)
    private String nationId;
    private String nationName;
}
