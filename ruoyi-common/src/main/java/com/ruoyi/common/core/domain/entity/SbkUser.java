package com.ruoyi.common.core.domain.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户对象 sys_user
 *
 * @author ruoyi
 */
@Data
public class SbkUser {
    private static final long serialVersionUID = 1L;
    /**
     * 发卡地行政区划代码
     */
    @ApiModelProperty("发卡地行政区划代码")
    private String aab301;
    /**
     * 社会保障卡卡号
     */
    @ApiModelProperty("社会保障卡卡号")
    private String aaz500;
    /**
     * 卡识别码
     */
    @ApiModelProperty("卡识别码")
    private String aaz501;
    /**
     * 社会保障号码
     */
    @ApiModelProperty("社会保障号码")
    private String aac002;
    /**
     * 姓名
     */
    @ApiModelProperty("姓名")
    private String aac003;
    /**
     * 社保卡状态（二级代码:0封装，1正常，2挂失，3应用锁定，4临时挂失，9注销）
     */
    @ApiModelProperty("社保卡状态")
    private String aaz502;
}
