package com.ruoyi.sbk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.sbk.domain.WxInfomationImg;
import com.ruoyi.sbk.mapper.WxInfomationImgMapper;
import com.ruoyi.sbk.service.IWxInfomationImgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 图片信息Service业务层处理
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
@Service
public class WxInfomationImgServiceImpl extends ServiceImpl<WxInfomationImgMapper, WxInfomationImg> implements IWxInfomationImgService {
    @Autowired
    private WxInfomationImgMapper wxInfomationImgMapper;

    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public List<WxInfomationImg> selectListByLambdaQueryWrapper(LambdaQueryWrapper<WxInfomationImg> lambdaQueryWrapper) {
        return wxInfomationImgMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public WxInfomationImg selectOneByLambdaQueryWrapper(LambdaQueryWrapper<WxInfomationImg> lambdaQueryWrapper) {
        return wxInfomationImgMapper.selectOne(lambdaQueryWrapper.last("limit 1"));
    }

    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public WxInfomationImg selectOneBySfzh(String cardNum) {
        return wxInfomationImgMapper.selectOne(new LambdaQueryWrapper<WxInfomationImg>()
                .eq(WxInfomationImg::getCardNum, cardNum));
    }

    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public Integer selectPersonidByMax() {
        WxInfomationImg wxInfomationImg = wxInfomationImgMapper.selectOne(new LambdaQueryWrapper<WxInfomationImg>()
                .orderByDesc(WxInfomationImg::getPersonid).last("limit 1"));
        return Integer.valueOf(wxInfomationImg.getPersonid());
    }
}
