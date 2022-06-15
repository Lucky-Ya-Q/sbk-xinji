package com.ruoyi.sbk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.sbk.domain.WxBukaInfo;
import com.ruoyi.sbk.mapper.WxBukaInfoMapper;
import com.ruoyi.sbk.service.IWxBukaInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WxBukaInfoServiceImpl extends ServiceImpl<WxBukaInfoMapper, WxBukaInfo> implements IWxBukaInfoService {
    @Autowired
    private WxBukaInfoMapper wxBukaInfoMapper;

    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public List<WxBukaInfo> selectListByLambdaQueryWrapper(LambdaQueryWrapper<WxBukaInfo> lambdaQueryWrapper) {
        return wxBukaInfoMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public WxBukaInfo selectOneByLambdaQueryWrapper(LambdaQueryWrapper<WxBukaInfo> lambdaQueryWrapper) {
        return wxBukaInfoMapper.selectOne(lambdaQueryWrapper.last("limit 1"));
    }

    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public boolean updateById(WxBukaInfo entity) {
        return SqlHelper.retBool(wxBukaInfoMapper.updateById(entity));
    }
}
