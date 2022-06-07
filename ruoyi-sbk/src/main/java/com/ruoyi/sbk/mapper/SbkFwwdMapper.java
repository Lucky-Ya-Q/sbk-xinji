package com.ruoyi.sbk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.sbk.domain.SbkFwwd;

import java.util.List;

/**
 * 社保卡服务网点Mapper接口
 *
 * @author lucky-ya-q
 * @date 2022-03-10
 */
public interface SbkFwwdMapper extends BaseMapper<SbkFwwd> {
    /**
     * 查询社保卡服务网点列表
     *
     * @param sbkFwwd 社保卡服务网点
     * @return 社保卡服务网点集合
     */
    List<SbkFwwd> selectSbkFwwdList(SbkFwwd sbkFwwd);
}
