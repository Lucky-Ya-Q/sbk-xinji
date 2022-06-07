package com.ruoyi.sbk.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.sbk.domain.SbkOperLog;
import com.ruoyi.sbk.domain.WxArchives;
import com.ruoyi.sbk.dto.Result;
import com.ruoyi.sbk.dto.XbkzgjyParam;
import com.ruoyi.sbk.enums.ServiceType;
import com.ruoyi.sbk.service.ISbkOperLogService;
import com.ruoyi.sbk.service.IWxArchivesService;
import com.ruoyi.sbk.service.IWxInfomationImgService;
import com.ruoyi.sbk.service.SbkService;
import com.ruoyi.sbk.util.SbkParamUtils;
import com.ruoyi.system.service.ISysUserService;
import com.tecsun.sm.utils.ParamUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 社保卡操作日志记录Controller
 *
 * @author lucky-ya-q
 * @date 2022-03-09
 */
@Slf4j
@Api(tags = "社保卡操作日志记录")
@RestController
@RequestMapping("/sbk/log")
public class SbkOperLogController extends BaseController {
    @Autowired
    private ISbkOperLogService sbkOperLogService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private SbkService sbkService;
    @Autowired
    private IWxArchivesService wxArchivesService;
    @Autowired
    private IWxInfomationImgService wxInfomationImgService;

    /**
     * 查询社保卡操作日志记录列表
     */
    @ApiOperation("查询社保卡操作日志记录列表")
    @PreAuthorize("@ss.hasPermi('sbk:log:list')")
    @GetMapping("/list")
    public TableDataInfo list(SbkOperLog sbkOperLog) throws IOException {
        startPage();
        List<SbkOperLog> list = sbkOperLogService.selectSbkOperLogList(sbkOperLog);
        for (SbkOperLog operLog : list) {
            String serviceType = operLog.getServiceType();
            if (serviceType.equals(ServiceType.SL.getType())) {
                XbkzgjyParam xbkzgjyParam = new XbkzgjyParam();
                xbkzgjyParam.setSfzh(operLog.getCardSfzh());
                WxArchives wxArchives = wxArchivesService.selectOneBySfzhAndXm(xbkzgjyParam);
                wxArchives.setWxInfomationImg(wxInfomationImgService.selectOneBySfzh(wxArchives.getCardNum()));
                sbkOperLog.setWxArchives(wxArchives);
            } else {
                // 社保卡基本信息查询
                Result result = sbkService.getResult("0811014", operLog.getCardSfzh() + "||");
                if (!"200".equals(result.getStatusCode())) {
                    log.info("操作记录ID：{} 身份证号：{} 查询失败！ 错误原因：{}",
                            operLog.getId(), operLog.getCardSfzh(), result.getMessage());
                    continue;
                }
                Map<String, String> data = (Map<String, String>) result.getData();
                String jbxxcx = ParamUtils.decrypted(SbkParamUtils.PRIVATEKEY, data.get("ReturnResult"));
                String[] jbxxcxArr = jbxxcx.split("\\|");
                operLog.setSbkBaseInfo(jbxxcxArr);
            }
        }
        return getDataTable(list);
    }

    /**
     * 导出社保卡操作日志记录列表
     */
    @ApiOperation("导出社保卡操作日志记录列表")
    @PreAuthorize("@ss.hasPermi('sbk:log:export')")
    @Log(title = "社保卡操作日志记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SbkOperLog sbkOperLog) {
        List<SbkOperLog> list = sbkOperLogService.selectSbkOperLogList(sbkOperLog);
        ExcelUtil<SbkOperLog> util = new ExcelUtil<SbkOperLog>(SbkOperLog.class);
        util.exportExcel(response, list, "社保卡操作日志记录数据");
    }

    /**
     * 获取社保卡操作日志记录详细信息
     */
    @ApiOperation("获取社保卡操作日志记录详细信息")
    @PreAuthorize("@ss.hasPermi('sbk:log:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) throws IOException {
        SbkOperLog sbkOperLog = sbkOperLogService.getById(id);
        Long userId = sbkOperLog.getUserId();
        if (userId != null) {
            SysUser sysUser = userService.selectUserById(userId);
            sbkOperLog.setNickName(sysUser.getNickName());
        }

        String serviceType = sbkOperLog.getServiceType();
        if (serviceType.equals(ServiceType.SL.getType())) {
            XbkzgjyParam xbkzgjyParam = new XbkzgjyParam();
            xbkzgjyParam.setSfzh(sbkOperLog.getCardSfzh());
            WxArchives wxArchives = wxArchivesService.selectOneBySfzhAndXm(xbkzgjyParam);
            wxArchives.setWxInfomationImg(wxInfomationImgService.selectOneBySfzh(wxArchives.getCardNum()));
            sbkOperLog.setWxArchives(wxArchives);
        } else {
            // 社保卡基本信息查询
            Result result = sbkService.getResult("0811014", sbkOperLog.getCardSfzh() + "||");
            if (!"200".equals(result.getStatusCode())) {
                return AjaxResult.error(result.getMessage());
            }
            Map<String, String> data = (Map<String, String>) result.getData();
            String jbxxcx = ParamUtils.decrypted(SbkParamUtils.PRIVATEKEY, data.get("ReturnResult"));
            String[] jbxxcxArr = jbxxcx.split("\\|");
            sbkOperLog.setSbkBaseInfo(jbxxcxArr);
        }
        return AjaxResult.success(sbkOperLog);
    }

    /**
     * 新增社保卡操作日志记录
     */
    @ApiOperation("新增社保卡操作日志记录")
    @PreAuthorize("@ss.hasPermi('sbk:log:add')")
    @Log(title = "社保卡操作日志记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SbkOperLog sbkOperLog) {
        return toAjax(sbkOperLogService.save(sbkOperLog));
    }

    /**
     * 修改社保卡操作日志记录
     */
    @ApiOperation("修改社保卡操作日志记录")
    @PreAuthorize("@ss.hasPermi('sbk:log:edit')")
    @Log(title = "社保卡操作日志记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SbkOperLog sbkOperLog) {
        return toAjax(sbkOperLogService.updateById(sbkOperLog));
    }

    /**
     * 删除社保卡操作日志记录
     */
    @ApiOperation("删除社保卡操作日志记录")
    @PreAuthorize("@ss.hasPermi('sbk:log:remove')")
    @Log(title = "社保卡操作日志记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(sbkOperLogService.removeByIds(Arrays.asList(ids)));
    }
}
