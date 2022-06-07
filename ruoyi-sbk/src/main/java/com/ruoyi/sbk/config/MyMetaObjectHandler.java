package com.ruoyi.sbk.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        try {
            this.strictInsertFill(metaObject, "userId", Long.class, SecurityUtils.getUserId());
            this.strictInsertFill(metaObject, "deptId", Long.class, SecurityUtils.getDeptId());
            this.strictInsertFill(metaObject, "createBy", String.class, SecurityUtils.getUsername());
        } catch (ServiceException e) {
            log.info(e.getMessage());
        }
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            this.strictUpdateFill(metaObject, "updateBy", String.class, SecurityUtils.getUsername());
        } catch (ServiceException e) {
            log.info(e.getMessage());
        }
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
    }
}
