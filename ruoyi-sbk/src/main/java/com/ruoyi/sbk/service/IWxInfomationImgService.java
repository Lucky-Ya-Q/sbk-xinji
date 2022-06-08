package com.ruoyi.sbk.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.sbk.domain.WxArchives;
import com.ruoyi.sbk.domain.WxInfomationImg;

import java.util.List;

/**
 * 图片信息Service接口
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
public interface IWxInfomationImgService extends IService<WxInfomationImg> {
    List<WxInfomationImg> selectListByLambdaQueryWrapper(LambdaQueryWrapper<WxInfomationImg> lambdaQueryWrapper);

    WxInfomationImg selectOneByLambdaQueryWrapper(LambdaQueryWrapper<WxInfomationImg> lambdaQueryWrapper);

    WxInfomationImg selectOneBySfzh(String cardNum);

    Integer selectPersonidByMax();
}
