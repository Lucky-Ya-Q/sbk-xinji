package com.ruoyi.sbk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.sbk.domain.WxBukaBank;
import com.ruoyi.sbk.mapper.WxBukaBankMapper;
import com.ruoyi.sbk.service.IWxBukaBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * 银行信息Service业务层处理
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
@Service
public class WxBukaBankServiceImpl extends ServiceImpl<WxBukaBankMapper, WxBukaBank> implements IWxBukaBankService {
    @Autowired
    private WxBukaBankMapper wxBukaBankMapper;

    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public WxBukaBank getById(Serializable id) {
        return wxBukaBankMapper.selectById(id);
    }

    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public List<WxBukaBank> selectListByLambdaQueryWrapper(LambdaQueryWrapper<WxBukaBank> lambdaQueryWrapper) {
        return wxBukaBankMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public WxBukaBank selectOneByLambdaQueryWrapper(LambdaQueryWrapper<WxBukaBank> lambdaQueryWrapper) {
        return wxBukaBankMapper.selectOne(lambdaQueryWrapper.last("limit 1"));
    }

    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public List<WxBukaBank> listAll() {
        return wxBukaBankMapper.selectList(new LambdaQueryWrapper<WxBukaBank>()
                .eq(WxBukaBank::getFlag, 1)
                .eq(WxBukaBank::getStatus, 2)
                .orderByAsc(WxBukaBank::getOrderId));
    }
}
