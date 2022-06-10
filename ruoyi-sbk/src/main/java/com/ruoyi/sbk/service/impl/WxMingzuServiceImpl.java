package com.ruoyi.sbk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.sbk.domain.WxMingzu;
import com.ruoyi.sbk.mapper.WxMingzuMapper;
import com.ruoyi.sbk.service.IWxMingzuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 民族信息Service业务层处理
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
@Service
public class WxMingzuServiceImpl extends ServiceImpl<WxMingzuMapper, WxMingzu> implements IWxMingzuService {
    @Autowired
    private WxMingzuMapper wxMingzuMapper;

    /**
     * 查询民族信息列表
     *
     * @param wxMingzu 民族信息
     * @return 民族信息
     */
    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public List<WxMingzu> selectWxMingzuList(WxMingzu wxMingzu) {
        return wxMingzuMapper.selectWxMingzuList(wxMingzu);
    }

    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public List<WxMingzu> listAll() {
        return wxMingzuMapper.listAll();
    }
}
