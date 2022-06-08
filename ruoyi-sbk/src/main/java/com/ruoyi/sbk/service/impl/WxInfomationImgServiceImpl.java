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
        LambdaQueryWrapper<WxInfomationImg> wxInfomationImgLambdaQueryWrapper = new LambdaQueryWrapper<>();
        wxInfomationImgLambdaQueryWrapper.eq(WxInfomationImg::getCardNum, cardNum);
        return wxInfomationImgMapper.selectOne(wxInfomationImgLambdaQueryWrapper);
    }

    @Override
    @DataSource(value = DataSourceType.SLAVE)
    public Integer selectPersonidByMax() {
        LambdaQueryWrapper<WxInfomationImg> wxInfomationImgLambdaQueryWrapper = new LambdaQueryWrapper<>();
        wxInfomationImgLambdaQueryWrapper.orderByDesc(WxInfomationImg::getPersonid).last("limit 1");
        WxInfomationImg wxInfomationImg = wxInfomationImgMapper.selectOne(wxInfomationImgLambdaQueryWrapper);
        return Integer.valueOf(wxInfomationImg.getPersonid());
    }
}
