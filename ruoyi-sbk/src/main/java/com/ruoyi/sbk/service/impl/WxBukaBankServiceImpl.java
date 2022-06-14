package com.ruoyi.sbk.service.impl;

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

    /**
     * 查询银行信息列表
     *
     * @param wxBukaBank 银行信息
     * @return 银行信息
     */
    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public List<WxBukaBank> selectWxBukaBankList(WxBukaBank wxBukaBank) {
        return wxBukaBankMapper.selectWxBukaBankList(wxBukaBank);
    }

    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public List<WxBukaBank> listAll() {
        return wxBukaBankMapper.listAll();
    }
}
