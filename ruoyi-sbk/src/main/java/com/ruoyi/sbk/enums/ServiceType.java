package com.ruoyi.sbk.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 服务类型（信息变更、挂失、解挂……）
 */
@Getter
@AllArgsConstructor
public enum ServiceType {
    XXBG("信息变更"),
    JG("解挂"),
    GS("挂失"),
    MMCZ("密码重置"),
    MMXG("密码修改"),
    SL("申领");
    private final String type;
}
