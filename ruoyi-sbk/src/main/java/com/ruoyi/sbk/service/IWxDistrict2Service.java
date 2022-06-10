package com.ruoyi.sbk.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.sbk.domain.WxBukaInfo;
import com.ruoyi.sbk.domain.WxDistrict2;

import java.util.List;

public interface IWxDistrict2Service extends IService<WxDistrict2> {
    List<WxDistrict2> selectListByLambdaQueryWrapper(LambdaQueryWrapper<WxDistrict2> lambdaQueryWrapper);

    WxDistrict2 selectOneByLambdaQueryWrapper(LambdaQueryWrapper<WxDistrict2> lambdaQueryWrapper);
}
