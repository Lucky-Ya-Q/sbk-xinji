package com.ruoyi.sbk.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 渠道类型（1个人、2区县、3乡镇、4村委会）
 */
@Getter
@AllArgsConstructor
public enum ChannelType {
    GEREN("个人", 1L),
    AREA("区县", 2L),
    STREET("乡镇", 3L),
    VILLAGE("村委会", 4L);
    private final String label;
    private final Long value;
}
