package com.ruoyi.sbk.util;

import com.ruoyi.common.core.domain.entity.SbkUser;
import com.ruoyi.common.core.domain.model.SbkLoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SbkSecurityUtils {
    /**
     * 获取用户
     */
    public static SbkUser getSbkUser() {
        return getSbkLoginUser().getUser();
    }

    /**
     * 获取登录用户
     */
    public static SbkLoginUser getSbkLoginUser() {
        return (SbkLoginUser) getAuthentication().getPrincipal();
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
