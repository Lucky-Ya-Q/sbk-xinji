package com.ruoyi.sbk.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 社保卡操作日志记录对象 sbk_oper_log
 *
 * @author lucky-ya-q
 * @date 2022-03-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SbkOperLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;

    /**
     * 持卡人姓名
     */
    @Excel(name = "持卡人姓名")
    private String cardXm;

    /**
     * 持卡人身份证号
     */
    @Excel(name = "持卡人身份证号")
    private String cardSfzh;

    /**
     * 持卡人卡号
     */
    @Excel(name = "持卡人卡号")
    private String cardKh;

    /**
     * 渠道类型（1个人、2区县、3乡镇、4村委会）
     */
    @Excel(name = "渠道类型")
    private Long channelType;

    /**
     * 区县
     */
    @Excel(name = "区县")
    private String area;

    /**
     * 乡镇
     */
    @Excel(name = "乡镇")
    private String street;

    /**
     * 村委会
     */
    @Excel(name = "村委会")
    private String village;

    /**
     * 服务类型（信息变更、挂失、解挂……）
     */
    @Excel(name = "服务类型")
    private String serviceType;

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

    /**
     * 用户昵称
     */
    @Excel(name = "用户名称")
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String nickName;

    /**
     * 社保卡基本信息
     */
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String[] sbkBaseInfo;

    /**
     * 申领基本信息
     */
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private WxArchives wxArchives;
}
