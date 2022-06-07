package com.ruoyi.sbk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.sbk.domain.WxBukaBank;

import java.util.List;

/**
 * 银行信息Mapper接口
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
public interface WxBukaBankMapper extends BaseMapper<WxBukaBank> {
    /**
     * 查询银行信息列表
     *
     * @param wxBukaBank 银行信息
     * @return 银行信息集合
     */
    List<WxBukaBank> selectWxBukaBankList(WxBukaBank wxBukaBank);
}
