package com.ruoyi.sbk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.sbk.domain.WxResidenceType;

import java.util.List;

/**
 * 户口性质Service接口
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
public interface IWxResidenceTypeService extends IService<WxResidenceType> {
    /**
     * 查询户口性质列表
     *
     * @param wxResidenceType 户口性质
     * @return 户口性质集合
     */
    List<WxResidenceType> selectWxResidenceTypeList(WxResidenceType wxResidenceType);
}
