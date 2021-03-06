package com.ruoyi.framework.aspectj;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.BusinessStatus;
import com.ruoyi.common.enums.HttpMethod;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.system.domain.SysOperLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 操作日志记录处理
 *
 * @author ruoyi
 */
@Aspect
@Component
public class LogAspect
{
    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    @Before(value = "@annotation(controllerLog)")
    public void doBefore(JoinPoint joinPoint, Log controllerLog) {
        // 获取当前的用户
        LoginUser loginUser = null;
        try {
            loginUser = SecurityUtils.getLoginUser();
        } catch (Exception ex) {
            log.info(ex.getMessage());
        }
        if (loginUser != null) return;

        List<String> codeList = new ArrayList<>();
        codeList.add("f54791a523474e12b7c183f17c3cbcc2");
        codeList.add("b1159fe9c99fce6abd2ab6d6cab99ea6");
        codeList.add("f1a04d853c2f8212210267ebbda5eadb");

        SysOperLog operLog = new SysOperLog();
        operLog.setTitle(controllerLog.title());
        operLog.setBusinessType(controllerLog.businessType().ordinal());
        // 设置方法名称
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        operLog.setMethod(className + "." + methodName + "()");
        // 设置请求方式
        operLog.setRequestMethod(ServletUtils.getRequest().getMethod());
        // 设置操作人类别
        operLog.setOperatorType(controllerLog.operatorType().ordinal());
        String queryString = ServletUtils.getRequest().getQueryString();
        operLog.setOperUrl(ServletUtils.getRequest().getRequestURI() + (StrUtil.isEmpty(queryString) ? "" : "?" + queryString));

        operLog.setOperIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        // 是否需要保存request，参数和值
        if (controllerLog.isSaveRequestData()) {
            // 获取参数的信息，传入到数据库中。
            setRequestValue(joinPoint, operLog);
        }

        JSONObject jsonObject = JSON.parseObject(operLog.getOperParam());
        String code = jsonObject.getString("code");
        if (StrUtil.isEmpty(code)) {
            // 保存数据库
            operLog.setStatus(BusinessStatus.FAIL.ordinal());
            operLog.setErrorMsg("授权码不能为空");
            AsyncManager.me().execute(AsyncFactory.recordOper(operLog));
            throw new ServiceException("授权码不能为空");
        } else {
            if (!codeList.contains(code)) {
                // 保存数据库
                operLog.setStatus(BusinessStatus.FAIL.ordinal());
                operLog.setErrorMsg("授权码错误");
                AsyncManager.me().execute(AsyncFactory.recordOper(operLog));
                throw new ServiceException("授权码错误");
            }
        }
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object jsonResult)
    {
        handleLog(joinPoint, controllerLog, null, jsonResult);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e 异常
     */
    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Log controllerLog, Exception e)
    {
        handleLog(joinPoint, controllerLog, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, Log controllerLog, final Exception e, Object jsonResult)
    {

        // 获取当前的用户
        LoginUser loginUser = null;
        try {
            loginUser = SecurityUtils.getLoginUser();
        } catch (Exception ex) {
            log.info(ex.getMessage());
        }

        // *========数据库日志=========*//
        SysOperLog operLog = new SysOperLog();
        operLog.setStatus(BusinessStatus.SUCCESS.ordinal());
        // 请求的地址
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        operLog.setOperIp(ip);
        String queryString = ServletUtils.getRequest().getQueryString();
        operLog.setOperUrl(ServletUtils.getRequest().getRequestURI() + (StrUtil.isEmpty(queryString) ? "" : "?" + queryString));

        if (e != null)
        {
            operLog.setStatus(BusinessStatus.FAIL.ordinal());
            operLog.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 2000));
        }
        // 设置方法名称
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        operLog.setMethod(className + "." + methodName + "()");
        // 设置请求方式
        operLog.setRequestMethod(ServletUtils.getRequest().getMethod());
        // 处理设置注解上的参数
        getControllerMethodDescription(joinPoint, controllerLog, operLog, jsonResult);
        if (loginUser != null)
        {
            operLog.setOperName(loginUser.getUsername());
        } else {
            JSONObject jsonObject = JSON.parseObject(operLog.getOperParam());
            String code = jsonObject.getString("code");
//            if (StrUtil.isEmpty(code)){
//                // 保存数据库
//                operLog.setStatus(BusinessStatus.FAIL.ordinal());
//                operLog.setErrorMsg("授权码不能为空");
//                operLog.setJsonResult("");
//                AsyncManager.me().execute(AsyncFactory.recordOper(operLog));
//                throw new ServiceException("授权码不能为空");
//            }
            switch (code) {
                case "f54791a523474e12b7c183f17c3cbcc2":
                    operLog.setOperName("ceshi");
                    break;
                case "b1159fe9c99fce6abd2ab6d6cab99ea6":
                    operLog.setOperName("shenpiju");
                    break;
                case "f1a04d853c2f8212210267ebbda5eadb":
                    operLog.setOperName("weixin");
                    break;
//                default:
//                    // 保存数据库
//                    operLog.setStatus(BusinessStatus.FAIL.ordinal());
//                    operLog.setErrorMsg("授权码错误");
//                    operLog.setJsonResult("");
//                    AsyncManager.me().execute(AsyncFactory.recordOper(operLog));
//                    throw new ServiceException("授权码错误");
            }
        }
        // 保存数据库
        AsyncManager.me().execute(AsyncFactory.recordOper(operLog));
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param log 日志
     * @param operLog 操作日志
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, Log log, SysOperLog operLog, Object jsonResult)
    {
        // 设置action动作
        operLog.setBusinessType(log.businessType().ordinal());
        // 设置标题
        operLog.setTitle(log.title());
        // 设置操作人类别
        operLog.setOperatorType(log.operatorType().ordinal());
        // 是否需要保存request，参数和值
        if (log.isSaveRequestData())
        {
            // 获取参数的信息，传入到数据库中。
            setRequestValue(joinPoint, operLog);
        }
        // 是否需要保存response，参数和值
        if (log.isSaveResponseData() && StringUtils.isNotNull(jsonResult))
        {
            operLog.setJsonResult(StringUtils.substring(JSON.toJSONString(jsonResult), 0, 2000));
        }
    }

    /**
     * 获取请求的参数，放到log中
     *
     * @param operLog 操作日志
     */
    private void setRequestValue(JoinPoint joinPoint, SysOperLog operLog)
    {
        String requestMethod = operLog.getRequestMethod();
        if (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod))
        {
            String params = argsArrayToString(joinPoint.getArgs());
            operLog.setOperParam(StringUtils.substring(params, 0, 2000));
        }
        else
        {
            Map<?, ?> paramsMap = (Map<?, ?>) ServletUtils.getRequest().getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            operLog.setOperParam(StringUtils.substring(paramsMap.toString(), 0, 2000));
        }
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray)
    {
        String params = "";
        if (paramsArray != null && paramsArray.length > 0)
        {
            for (Object o : paramsArray)
            {
                if (StringUtils.isNotNull(o) && !isFilterObject(o))
                {
                    try
                    {
                        Object jsonObj = JSON.toJSON(o);
                        params += jsonObj.toString() + " ";
                    }
                    catch (Exception e)
                    {
                    }
                }
            }
        }
        return params.trim();
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o)
    {
        Class<?> clazz = o.getClass();
        if (clazz.isArray())
        {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        }
        else if (Collection.class.isAssignableFrom(clazz))
        {
            Collection collection = (Collection) o;
            for (Object value : collection)
            {
                return value instanceof MultipartFile;
            }
        }
        else if (Map.class.isAssignableFrom(clazz))
        {
            Map map = (Map) o;
            for (Object value : map.entrySet())
            {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }
}
