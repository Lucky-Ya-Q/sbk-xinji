package com.ruoyi.sbk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.sbk.domain.SbkOperLog;
import com.ruoyi.sbk.mapper.SbkOperLogMapper;
import com.ruoyi.sbk.service.ISbkOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 社保卡操作日志记录Service业务层处理
 *
 * @author lucky-ya-q
 * @date 2022-03-09
 */
@Service
public class SbkOperLogServiceImpl extends ServiceImpl<SbkOperLogMapper, SbkOperLog> implements ISbkOperLogService {
    @Autowired
    private SbkOperLogMapper sbkOperLogMapper;

    /**
     * 查询社保卡操作日志记录列表
     *
     * @param sbkOperLog 社保卡操作日志记录
     * @return 社保卡操作日志记录
     */
    @Override
    @DataScope(userAlias = "u", deptAlias = "d")
    public List<SbkOperLog> selectSbkOperLogList(SbkOperLog sbkOperLog) {
        return sbkOperLogMapper.selectSbkOperLogList(sbkOperLog);
    }

    @Override
    public List<Map<String, Integer>> groupByServiceType() {
        return sbkOperLogMapper.groupByServiceType();
    }

    @Override
    public List<Map<String, Integer>> groupByStreet() {
        return sbkOperLogMapper.groupByStreet();
    }

    @Override
    public List<Map<String, Integer>> groupByChannelType() {
        return sbkOperLogMapper.groupByChannelType();
    }

    @Override
    public Map<String, Integer> bjqdfbbfxxc() {
        return sbkOperLogMapper.bjqdfbbfxxc();
    }
}
