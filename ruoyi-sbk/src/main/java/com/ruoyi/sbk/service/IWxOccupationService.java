package com.ruoyi.sbk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.sbk.domain.WxOccupation;

import java.util.List;

/**
 * 职业信息Service接口
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
public interface IWxOccupationService extends IService<WxOccupation> {
    /**
     * 查询职业信息列表
     *
     * @param wxOccupation 职业信息
     * @return 职业信息集合
     */
    List<WxOccupation> selectWxOccupationList(WxOccupation wxOccupation);

    List<WxOccupation> listAll();
}
