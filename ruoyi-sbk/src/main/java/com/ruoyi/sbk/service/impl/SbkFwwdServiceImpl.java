package com.ruoyi.sbk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.sbk.domain.SbkFwwd;
import com.ruoyi.sbk.mapper.SbkFwwdMapper;
import com.ruoyi.sbk.service.ISbkFwwdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 社保卡服务网点Service业务层处理
 *
 * @author lucky-ya-q
 * @date 2022-03-10
 */
@Service
public class SbkFwwdServiceImpl extends ServiceImpl<SbkFwwdMapper, SbkFwwd> implements ISbkFwwdService {
    @Autowired
    private SbkFwwdMapper sbkFwwdMapper;

    /**
     * 查询社保卡服务网点列表
     *
     * @param sbkFwwd 社保卡服务网点
     * @return 社保卡服务网点
     */
    @Override
    public List<SbkFwwd> selectSbkFwwdList(SbkFwwd sbkFwwd) {
        return sbkFwwdMapper.selectSbkFwwdList(sbkFwwd);
    }
}
