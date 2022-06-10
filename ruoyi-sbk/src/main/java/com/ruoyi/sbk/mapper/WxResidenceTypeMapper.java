package com.ruoyi.sbk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.sbk.domain.WxResidenceType;

import java.util.List;

/**
 * 户口性质Mapper接口
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
public interface WxResidenceTypeMapper extends BaseMapper<WxResidenceType> {
    /**
     * 查询户口性质列表
     *
     * @param wxResidenceType 户口性质
     * @return 户口性质集合
     */
    List<WxResidenceType> selectWxResidenceTypeList(WxResidenceType wxResidenceType);

    List<WxResidenceType> listAll();
}
