package com.ruoyi.sbk.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.sbk.domain.WxArchives;
import com.ruoyi.sbk.dto.XbkzgjyParam;
import com.ruoyi.sbk.mapper.WxArchivesMapper;
import com.ruoyi.sbk.mapper.WxInfomationImgMapper;
import com.ruoyi.sbk.service.IWxArchivesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 基本信息Service业务层处理
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
@Service
public class WxArchivesServiceImpl extends ServiceImpl<WxArchivesMapper, WxArchives> implements IWxArchivesService {
    @Autowired
    private WxArchivesMapper wxArchivesMapper;
    @Autowired
    private WxInfomationImgMapper wxInfomationImgMapper;

    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public List<WxArchives> selectListByLambdaQueryWrapper(LambdaQueryWrapper<WxArchives> lambdaQueryWrapper) {
        return wxArchivesMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public WxArchives selectOneByLambdaQueryWrapper(LambdaQueryWrapper<WxArchives> lambdaQueryWrapper) {
        return wxArchivesMapper.selectOne(lambdaQueryWrapper.last("limit 1"));
    }

    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public WxArchives selectOneBySfzhAndXm(XbkzgjyParam xbkzgjyParam) {
        LambdaQueryWrapper<WxArchives> wxArchivesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        wxArchivesLambdaQueryWrapper.eq(WxArchives::getCardNum, xbkzgjyParam.getSfzh())
                .eq(StrUtil.isNotEmpty(xbkzgjyParam.getXm()), WxArchives::getName, xbkzgjyParam.getXm());
        return wxArchivesMapper.selectOne(wxArchivesLambdaQueryWrapper);
    }

    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public Integer selectPersonidByMax() {
        LambdaQueryWrapper<WxArchives> wxArchivesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        wxArchivesLambdaQueryWrapper.orderByDesc(WxArchives::getPersonid).last("limit 1");
        WxArchives wxArchives = wxArchivesMapper.selectOne(wxArchivesLambdaQueryWrapper);
        return Integer.valueOf(wxArchives.getPersonid());
    }

    @Override
    @Transactional
    @DataSource(value = DataSourceType.SLAVE)
    public void saveArchivesAndImg(WxArchives wxArchives) {
        LambdaQueryWrapper<WxArchives> wxArchivesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        wxArchivesLambdaQueryWrapper.eq(WxArchives::getCardNum, wxArchives.getCardNum());
        Long count = wxArchivesMapper.selectCount(wxArchivesLambdaQueryWrapper);
        if (count > 0) {
            throw new ServiceException("采集信息已存在");
        }
        wxArchivesMapper.insert(wxArchives);
        wxInfomationImgMapper.insert(wxArchives.getWxInfomationImg());
    }

    @Override
    @Transactional
    @DataSource(value = DataSourceType.SLAVE)
    public void updateArchivesAndImg(WxArchives wxArchives) {
        wxArchivesMapper.updateById(wxArchives);
        wxInfomationImgMapper.updateById(wxArchives.getWxInfomationImg());
    }
}
