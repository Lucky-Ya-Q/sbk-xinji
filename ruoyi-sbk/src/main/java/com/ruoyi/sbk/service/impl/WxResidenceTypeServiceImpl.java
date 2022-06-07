package com.ruoyi.sbk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.sbk.domain.WxResidenceType;
import com.ruoyi.sbk.mapper.WxResidenceTypeMapper;
import com.ruoyi.sbk.service.IWxResidenceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 户口性质Service业务层处理
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
@Service
public class WxResidenceTypeServiceImpl extends ServiceImpl<WxResidenceTypeMapper, WxResidenceType> implements IWxResidenceTypeService {
    @Autowired
    private WxResidenceTypeMapper wxResidenceTypeMapper;

    /**
     * 查询户口性质列表
     *
     * @param wxResidenceType 户口性质
     * @return 户口性质
     */
    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public List<WxResidenceType> selectWxResidenceTypeList(WxResidenceType wxResidenceType) {
        return wxResidenceTypeMapper.selectWxResidenceTypeList(wxResidenceType);
    }
}
