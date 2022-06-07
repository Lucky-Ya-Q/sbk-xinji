package com.ruoyi.sbk.common;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.sbk.dto.Result;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

public class SbkBaseController {
    @Autowired
    private HttpServletRequest request;

    protected AjaxResult toAjax(Result result) {
        if ("200".equals(result.getStatusCode())) {
            return AjaxResult.success(result.getMessage());
        } else {
            return AjaxResult.error(result.getMessage());
        }
    }

    protected AjaxResult toAjax(Result result, String success) {
        if ("200".equals(result.getStatusCode())) {
            return AjaxResult.success(success);
        } else {
            return AjaxResult.error(result.getMessage());
        }
    }
}
