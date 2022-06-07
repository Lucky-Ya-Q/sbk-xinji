package com.ruoyi.sbk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.sbk.domain.WxInfomationImg;

/**
 * 图片信息Service接口
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
public interface IWxInfomationImgService extends IService<WxInfomationImg> {
    WxInfomationImg selectOneBySfzh(String cardNum);

    Integer selectPersonidByMax();
}
