package com.ruoyi.sbk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.sbk.domain.SbkOperLog;

import java.util.List;
import java.util.Map;

/**
 * 社保卡操作日志记录Mapper接口
 *
 * @author lucky-ya-q
 * @date 2022-03-09
 */
public interface SbkOperLogMapper extends BaseMapper<SbkOperLog> {
    /**
     * 查询社保卡操作日志记录列表
     *
     * @param sbkOperLog 社保卡操作日志记录
     * @return 社保卡操作日志记录集合
     */
    List<SbkOperLog> selectSbkOperLogList(SbkOperLog sbkOperLog);

    List<Map<String, Integer>> groupByServiceType();

    List<Map<String, Integer>> groupByStreet();

    List<Map<String, Integer>> groupByChannelType();

    Map<String, Integer> bjqdfbbfxxc();
}
