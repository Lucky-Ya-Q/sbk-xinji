package com.ruoyi.sbk.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.DesensitizedUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.sbk.domain.SbkFwwd;
import com.ruoyi.sbk.domain.SbkOperLog;
import com.ruoyi.sbk.enums.ChannelType;
import com.ruoyi.sbk.enums.ServiceType;
import com.ruoyi.sbk.service.ISbkFwwdService;
import com.ruoyi.sbk.service.ISbkOperLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Api(tags = "大屏")
@RestController
@RequestMapping("/big/screen")
public class BigScreenController {
    @Autowired
    private ISbkOperLogService sbkOperLogService;
    @Autowired
    private ISbkFwwdService sbkFwwdService;

    /**
     * 总办件量
     */
    @ApiOperation("总办件量")
    @GetMapping("/zbjl")
    public AjaxResult zbjl() {
        return AjaxResult.success(sbkOperLogService.count());
    }

    /**
     * 今日办件总量
     */
    @ApiOperation("今日办件总量")
    @GetMapping("/jrbjzl")
    public AjaxResult jrbjzl() {
        Date date = DateUtil.date();
        DateTime beginOfDay = DateUtil.beginOfDay(date);
        DateTime endOfDay = DateUtil.endOfDay(date);
        LambdaQueryWrapper<SbkOperLog> sbkOperLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sbkOperLogLambdaQueryWrapper.between(SbkOperLog::getCreateTime, beginOfDay, endOfDay);
        return AjaxResult.success(sbkOperLogService.count(sbkOperLogLambdaQueryWrapper));
    }

    /**
     * 办件实时动态
     */
    @ApiOperation("办件实时动态")
    @GetMapping("/bjssdt")
    public AjaxResult bjssdt() {
        LambdaQueryWrapper<SbkOperLog> sbkOperLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sbkOperLogLambdaQueryWrapper.ne(SbkOperLog::getChannelType, ChannelType.GEREN.getValue()).orderByDesc(SbkOperLog::getCreateTime).last("limit 5");
        List<SbkOperLog> sbkOperLogList = sbkOperLogService.list(sbkOperLogLambdaQueryWrapper);
        for (SbkOperLog sbkOperLog : sbkOperLogList) {
            sbkOperLog.setCardXm(DesensitizedUtil.chineseName(sbkOperLog.getCardXm()));
            sbkOperLog.setCardSfzh(DesensitizedUtil.idCardNum(sbkOperLog.getCardSfzh(), 0, 4));
        }
        return AjaxResult.success(sbkOperLogList);
    }

    /**
     * 今日热门服务事项
     */
    @ApiOperation("今日热门服务事项")
    @GetMapping("/jrrmfwsx")
    public AjaxResult jrrmfwsx() {
        return AjaxResult.success(sbkOperLogService.groupByServiceType());
    }

    /**
     * 办件大厅总数
     */
    @ApiOperation("办件大厅总数")
    @GetMapping("/bjdtzs")
    public AjaxResult bjdtzs() {
        LambdaQueryWrapper<SbkFwwd> sbkFwwdLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sbkFwwdLambdaQueryWrapper.eq(SbkFwwd::getState, 1);
        return AjaxResult.success(sbkFwwdService.count(sbkFwwdLambdaQueryWrapper));
    }

    /**
     * 服务事项总数
     */
    @ApiOperation("服务事项总数")
    @GetMapping("/fwsxzs")
    public AjaxResult fwsxzs() {
        return AjaxResult.success(ServiceType.values().length);
    }

    /**
     * 今日各时段办件量情况
     */
    @ApiOperation("今日各时段办件量情况")
    @GetMapping("/jrgsdbjlqk")
    public AjaxResult jrgsdbjlqk() {
        List<Map<String, Integer>> mapList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 11; i++) {
            calendar.set(Calendar.HOUR_OF_DAY, 8 + i);
            Date beginOfHour = DateUtil.beginOfHour(calendar).getTime();
            Date endOfHour = DateUtil.endOfHour(calendar).getTime();
            LambdaQueryWrapper<SbkOperLog> sbkOperLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
            sbkOperLogLambdaQueryWrapper.between(SbkOperLog::getCreateTime, beginOfHour, endOfHour);
            long count = sbkOperLogService.count(sbkOperLogLambdaQueryWrapper);
            Map<String, Integer> map = new HashMap<>();
            map.put("hour", 8 + i);
            map.put("count", Math.toIntExact(count));
            mapList.add(map);
        }
        return AjaxResult.success(mapList);
    }

    /**
     * 今日个人办件总量
     */
    @ApiOperation("今日个人办件总量")
    @GetMapping("/jrgrbjzl")
    public AjaxResult jrgrbjzl() {
        Date date = DateUtil.date();
        DateTime beginOfDay = DateUtil.beginOfDay(date);
        DateTime endOfDay = DateUtil.endOfDay(date);
        LambdaQueryWrapper<SbkOperLog> sbkOperLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sbkOperLogLambdaQueryWrapper.between(SbkOperLog::getCreateTime, beginOfDay, endOfDay).eq(SbkOperLog::getChannelType, ChannelType.GEREN.getValue());
        return AjaxResult.success(sbkOperLogService.count(sbkOperLogLambdaQueryWrapper));
    }

    /**
     * 今日业务员办件总量
     */
    @ApiOperation("今日业务员办件总量")
    @GetMapping("/jrywybjzl")
    public AjaxResult jrywybjzl() {
        Date date = DateUtil.date();
        DateTime beginOfDay = DateUtil.beginOfDay(date);
        DateTime endOfDay = DateUtil.endOfDay(date);
        LambdaQueryWrapper<SbkOperLog> sbkOperLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sbkOperLogLambdaQueryWrapper.between(SbkOperLog::getCreateTime, beginOfDay, endOfDay).ne(SbkOperLog::getChannelType, ChannelType.GEREN.getValue());
        return AjaxResult.success(sbkOperLogService.count(sbkOperLogLambdaQueryWrapper));
    }

    /**
     * 各地区今日办件情况
     */
    @ApiOperation("各地区今日办件情况")
    @GetMapping("/gdqjrbjqk")
    public AjaxResult gdqjrbjqk() {
        return AjaxResult.success(sbkOperLogService.groupByStreet());
    }

    /**
     * 办件渠道分布-分县乡村
     */
    @ApiOperation("办件渠道分布-分县乡村")
    @GetMapping("/bjqdfb")
    public AjaxResult bjqdfb() {
        return AjaxResult.success(sbkOperLogService.groupByChannelType());
    }

    /**
     * 办件渠道分布-不分县乡村
     */
    @ApiOperation("办件渠道分布-不分县乡村")
    @GetMapping("/bjqdfbbfxxc")
    public AjaxResult bjqdfbbfxxc() {
        return AjaxResult.success(sbkOperLogService.bjqdfbbfxxc());
    }
}
