package com.ruoyi.sbk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.sbk.domain.WxRelation;
import com.ruoyi.sbk.mapper.WxRelationMapper;
import com.ruoyi.sbk.service.IWxRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 代办人关系Service业务层处理
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
@Service
public class WxRelationServiceImpl extends ServiceImpl<WxRelationMapper, WxRelation> implements IWxRelationService {
    @Autowired
    private WxRelationMapper wxRelationMapper;

    /**
     * 查询代办人关系列表
     *
     * @param wxRelation 代办人关系
     * @return 代办人关系
     */
    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public List<WxRelation> selectWxRelationList(WxRelation wxRelation) {
        return wxRelationMapper.selectWxRelationList(wxRelation);
    }
}
