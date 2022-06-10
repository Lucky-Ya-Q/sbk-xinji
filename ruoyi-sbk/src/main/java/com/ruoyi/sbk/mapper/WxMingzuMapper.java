package com.ruoyi.sbk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.sbk.domain.WxMingzu;

import java.util.List;

/**
 * 民族信息Mapper接口
 *
 * @author lucky-ya-q
 * @date 2022-03-18
 */
public interface WxMingzuMapper extends BaseMapper<WxMingzu> {
    /**
     * 查询民族信息列表
     *
     * @param wxMingzu 民族信息
     * @return 民族信息集合
     */
    List<WxMingzu> selectWxMingzuList(WxMingzu wxMingzu);

    List<WxMingzu> listAll();
}
