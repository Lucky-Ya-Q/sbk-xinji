package com.ruoyi.sbk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.sbk.domain.WxBukaInfoImg;
import com.ruoyi.sbk.mapper.WxBukaInfoImgMapper;
import com.ruoyi.sbk.service.IWxBukaInfoImgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WxBukaInfoImgServiceImpl extends ServiceImpl<WxBukaInfoImgMapper, WxBukaInfoImg> implements IWxBukaInfoImgService {
    @Autowired
    private WxBukaInfoImgMapper wxBukaInfoImgMapper;

    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public List<WxBukaInfoImg> selectListByLambdaQueryWrapper(LambdaQueryWrapper<WxBukaInfoImg> lambdaQueryWrapper) {
        return wxBukaInfoImgMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public WxBukaInfoImg selectOneByLambdaQueryWrapper(LambdaQueryWrapper<WxBukaInfoImg> lambdaQueryWrapper) {
        return wxBukaInfoImgMapper.selectOne(lambdaQueryWrapper.last("limit 1"));
    }
}
