package com.ruoyi.sbk.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.sbk.domain.WxBukaBank;

import java.util.List;

/**
 * 银行信息Service接口
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
public interface IWxBukaBankService extends IService<WxBukaBank> {
    List<WxBukaBank> selectListByLambdaQueryWrapper(LambdaQueryWrapper<WxBukaBank> lambdaQueryWrapper);

    WxBukaBank selectOneByLambdaQueryWrapper(LambdaQueryWrapper<WxBukaBank> lambdaQueryWrapper);

    List<WxBukaBank> listAll();
}
