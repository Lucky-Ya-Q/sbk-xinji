package com.ruoyi.sbk.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.sbk.domain.SbkFwwd;
import com.ruoyi.sbk.service.ISbkFwwdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * 社保卡服务网点Controller
 *
 * @author lucky-ya-q
 * @date 2022-03-10
 */
@Api(tags = "社保卡服务网点")
@RestController
@RequestMapping("/sbk/fwwd")
public class SbkFwwdController extends BaseController {
    @Autowired
    private ISbkFwwdService sbkFwwdService;

    /**
     * 查询社保卡服务网点列表
     */
    @ApiOperation("查询社保卡服务网点列表")
    @PreAuthorize("@ss.hasPermi('sbk:fwwd:list')")
    @GetMapping("/list")
    public TableDataInfo list(SbkFwwd sbkFwwd) {
        startPage();
        List<SbkFwwd> list = sbkFwwdService.selectSbkFwwdList(sbkFwwd);
        return getDataTable(list);
    }

    /**
     * 导出社保卡服务网点列表
     */
    @ApiOperation("导出社保卡服务网点列表")
    @PreAuthorize("@ss.hasPermi('sbk:fwwd:export')")
    @Log(title = "社保卡服务网点", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SbkFwwd sbkFwwd) {
        List<SbkFwwd> list = sbkFwwdService.selectSbkFwwdList(sbkFwwd);
        ExcelUtil<SbkFwwd> util = new ExcelUtil<SbkFwwd>(SbkFwwd.class);
        util.exportExcel(response, list, "社保卡服务网点数据");
    }

    /**
     * 获取社保卡服务网点详细信息
     */
    @ApiOperation("获取社保卡服务网点详细信息")
    @PreAuthorize("@ss.hasPermi('sbk:fwwd:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(sbkFwwdService.getById(id));
    }

    /**
     * 新增社保卡服务网点
     */
    @ApiOperation("新增社保卡服务网点")
    @PreAuthorize("@ss.hasPermi('sbk:fwwd:add')")
    @Log(title = "社保卡服务网点", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SbkFwwd sbkFwwd) {
        return toAjax(sbkFwwdService.save(sbkFwwd));
    }

    /**
     * 修改社保卡服务网点
     */
    @ApiOperation("修改社保卡服务网点")
    @PreAuthorize("@ss.hasPermi('sbk:fwwd:edit')")
    @Log(title = "社保卡服务网点", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SbkFwwd sbkFwwd) {
        return toAjax(sbkFwwdService.updateById(sbkFwwd));
    }

    /**
     * 删除社保卡服务网点
     */
    @ApiOperation("删除社保卡服务网点")
    @PreAuthorize("@ss.hasPermi('sbk:fwwd:remove')")
    @Log(title = "社保卡服务网点", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(sbkFwwdService.removeByIds(Arrays.asList(ids)));
    }
}
