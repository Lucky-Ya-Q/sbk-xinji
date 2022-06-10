package com.ruoyi.sbk.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.sbk.domain.UnitinfoShi;
import com.ruoyi.sbk.domain.WxDistrict2;

import java.util.List;

/**
 * 单位信息Service接口
 *
 * @author lucky-ya-q
 * @date 2022-03-17
 */
public interface IUnitinfoShiService extends IService<UnitinfoShi> {
    List<UnitinfoShi> selectListByLambdaQueryWrapper(LambdaQueryWrapper<UnitinfoShi> lambdaQueryWrapper);

    UnitinfoShi selectOneByLambdaQueryWrapper(LambdaQueryWrapper<UnitinfoShi> lambdaQueryWrapper);

    /**
     * 查询单位信息列表
     *
     * @param unitinfoShi 单位信息
     * @return 单位信息集合
     */
    List<UnitinfoShi> selectUnitinfoShiList(UnitinfoShi unitinfoShi);
}
