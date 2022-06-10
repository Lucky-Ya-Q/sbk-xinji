package com.ruoyi.sbk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.sbk.domain.WxRelation;

import java.util.List;

/**
 * 代办人关系Mapper接口
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
public interface WxRelationMapper extends BaseMapper<WxRelation> {
    /**
     * 查询代办人关系列表
     *
     * @param wxRelation 代办人关系
     * @return 代办人关系集合
     */
    List<WxRelation> selectWxRelationList(WxRelation wxRelation);

    List<WxRelation> listAll();
}
