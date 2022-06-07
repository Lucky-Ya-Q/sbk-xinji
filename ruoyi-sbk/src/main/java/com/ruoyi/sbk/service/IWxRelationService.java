package com.ruoyi.sbk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.sbk.domain.WxRelation;

import java.util.List;

/**
 * 代办人关系Service接口
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
public interface IWxRelationService extends IService<WxRelation> {
    /**
     * 查询代办人关系列表
     *
     * @param wxRelation 代办人关系
     * @return 代办人关系集合
     */
    List<WxRelation> selectWxRelationList(WxRelation wxRelation);
}
