package com.ruoyi.sbk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.sbk.domain.SbkFwwd;

import java.util.List;

/**
 * 社保卡服务网点Service接口
 *
 * @author lucky-ya-q
 * @date 2022-03-10
 */
public interface ISbkFwwdService extends IService<SbkFwwd> {
    /**
     * 查询社保卡服务网点列表
     *
     * @param sbkFwwd 社保卡服务网点
     * @return 社保卡服务网点集合
     */
    List<SbkFwwd> selectSbkFwwdList(SbkFwwd sbkFwwd);
}
