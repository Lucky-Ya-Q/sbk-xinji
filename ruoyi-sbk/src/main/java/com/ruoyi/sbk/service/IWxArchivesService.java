package com.ruoyi.sbk.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.sbk.domain.WxArchives;
import com.ruoyi.sbk.dto.XbkzgjyParam;

import java.util.List;

/**
 * 基本信息Service接口
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
public interface IWxArchivesService extends IService<WxArchives> {
    List<WxArchives> selectListByLambdaQueryWrapper(LambdaQueryWrapper<WxArchives> lambdaQueryWrapper);

    WxArchives selectOneByLambdaQueryWrapper(LambdaQueryWrapper<WxArchives> lambdaQueryWrapper);

    WxArchives selectOneBySfzhAndXm(XbkzgjyParam xbkzgjyParam);

    Integer selectPersonidByMax();

    void saveArchivesAndImg(WxArchives wxArchives);

    void updateArchivesAndImg(WxArchives wxArchives);
}
