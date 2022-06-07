package com.ruoyi.sbk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.sbk.domain.WxOccupation;
import com.ruoyi.sbk.mapper.WxOccupationMapper;
import com.ruoyi.sbk.service.IWxOccupationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 职业信息Service业务层处理
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
@Service
public class WxOccupationServiceImpl extends ServiceImpl<WxOccupationMapper, WxOccupation> implements IWxOccupationService {
    @Autowired
    private WxOccupationMapper wxOccupationMapper;

    /**
     * 查询职业信息列表
     *
     * @param wxOccupation 职业信息
     * @return 职业信息
     */
    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public List<WxOccupation> selectWxOccupationList(WxOccupation wxOccupation) {
        return wxOccupationMapper.selectWxOccupationList(wxOccupation);
    }
}
