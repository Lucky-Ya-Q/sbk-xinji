package com.ruoyi.sbk.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.sbk.domain.WxBukaInfo;

import java.util.List;

public interface IWxBukaInfoService extends IService<WxBukaInfo> {
    List<WxBukaInfo> selectListByLambdaQueryWrapper(LambdaQueryWrapper<WxBukaInfo> lambdaQueryWrapper);

    WxBukaInfo selectOneByLambdaQueryWrapper(LambdaQueryWrapper<WxBukaInfo> lambdaQueryWrapper);
}
