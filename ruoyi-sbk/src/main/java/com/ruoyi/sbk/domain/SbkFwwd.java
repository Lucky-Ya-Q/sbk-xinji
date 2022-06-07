package com.ruoyi.sbk.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 社保卡服务网点对象 sbk_fwwd
 *
 * @author lucky-ya-q
 * @date 2022-03-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SbkFwwd extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;

    /**
     * 行政区划
     */
    @Excel(name = "行政区划")
    private String xzqh;

    /**
     * 网点编号
     */
    @Excel(name = "网点编号")
    private String wdbh;

    /**
     * 网点名称
     */
    @Excel(name = "网点名称")
    private String wdmc;

    /**
     * 联系人
     */
    @Excel(name = "联系人")
    private String lxr;

    /**
     * 联系电话
     */
    @Excel(name = "联系电话")
    private String lxdh;

    /**
     * 网点地址
     */
    @Excel(name = "网点地址")
    private String address;

    /**
     * 经度
     */
    @Excel(name = "经度")
    private String longitude;

    /**
     * 纬度
     */
    @Excel(name = "纬度")
    private String latitude;

    /**
     * 状态（0禁用、1启用）
     */
    @Excel(name = "状态")
    private Long state;

    /**
     * 用户ID
     */
    @Excel(name = "用户ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(hidden = true)
    private Long userId;

    /**
     * 部门ID
     */
    @Excel(name = "部门ID")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(hidden = true)
    private Long deptId;
}
