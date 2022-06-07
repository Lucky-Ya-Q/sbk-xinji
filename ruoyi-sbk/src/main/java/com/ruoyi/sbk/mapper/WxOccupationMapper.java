package com.ruoyi.sbk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.sbk.domain.WxOccupation;

import java.util.List;

/**
 * 职业信息Mapper接口
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
public interface WxOccupationMapper extends BaseMapper<WxOccupation> {
    /**
     * 查询职业信息列表
     *
     * @param wxOccupation 职业信息
     * @return 职业信息集合
     */
    List<WxOccupation> selectWxOccupationList(WxOccupation wxOccupation);
}
