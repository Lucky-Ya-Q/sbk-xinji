package com.ruoyi.sbk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.sbk.domain.WxArchives;
import com.ruoyi.sbk.dto.XbkzgjyParam;

/**
 * 基本信息Service接口
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
public interface IWxArchivesService extends IService<WxArchives> {
    WxArchives selectOneBySfzhAndXm(XbkzgjyParam xbkzgjyParam);

    Integer selectPersonidByMax();

    void saveArchivesAndImg(WxArchives wxArchives);

    void updateArchivesAndImg(WxArchives wxArchives);
}
