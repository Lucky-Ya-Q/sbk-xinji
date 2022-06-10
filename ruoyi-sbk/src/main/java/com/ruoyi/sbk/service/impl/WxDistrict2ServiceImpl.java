package com.ruoyi.sbk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.sbk.domain.WxBukaInfo;
import com.ruoyi.sbk.domain.WxDistrict2;
import com.ruoyi.sbk.mapper.WxBukaInfoMapper;
import com.ruoyi.sbk.mapper.WxDistrict2Mapper;
import com.ruoyi.sbk.service.IWxDistrict2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WxDistrict2ServiceImpl extends ServiceImpl<WxDistrict2Mapper, WxDistrict2> implements IWxDistrict2Service {
    @Autowired
    private WxDistrict2Mapper wxDistrict2Mapper;

    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public List<WxDistrict2> selectListByLambdaQueryWrapper(LambdaQueryWrapper<WxDistrict2> lambdaQueryWrapper) {
        return wxDistrict2Mapper.selectList(lambdaQueryWrapper);
    }

    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public WxDistrict2 selectOneByLambdaQueryWrapper(LambdaQueryWrapper<WxDistrict2> lambdaQueryWrapper) {
        return wxDistrict2Mapper.selectOne(lambdaQueryWrapper.last("limit 1"));
    }
}
