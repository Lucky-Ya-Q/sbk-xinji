package com.ruoyi.sbk.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.sbk.domain.WxBukaInfoImg;

import java.util.List;

public interface IWxBukaInfoImgService extends IService<WxBukaInfoImg> {
    List<WxBukaInfoImg> selectListByLambdaQueryWrapper(LambdaQueryWrapper<WxBukaInfoImg> lambdaQueryWrapper);

    WxBukaInfoImg selectOneByLambdaQueryWrapper(LambdaQueryWrapper<WxBukaInfoImg> lambdaQueryWrapper);
}
