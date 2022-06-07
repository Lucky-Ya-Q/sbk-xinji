package com.ruoyi.sbk.factory;

import com.ruoyi.common.core.domain.entity.SbkUser;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.sbk.domain.SbkOperLog;
import com.ruoyi.sbk.enums.ChannelType;
import com.ruoyi.sbk.enums.ServiceType;
import com.ruoyi.sbk.service.ISbkOperLogService;
import com.ruoyi.system.service.ISysDeptService;

import java.util.List;
import java.util.TimerTask;

public class SbkAsyncFactory {
    /**
     * 服务平台社保卡操作日志记录
     *
     * @param sbkUser
     * @param serviceType
     * @return
     */
    public static TimerTask recordOper(SbkUser sbkUser, ServiceType serviceType) {
        return new TimerTask() {
            @Override
            public void run() {
                if (sbkUser.getAac002() == null) {
                    return;
                }

                SbkOperLog sbkOperLog = new SbkOperLog();
                sbkOperLog.setCardXm(sbkUser.getAac003());
                sbkOperLog.setCardSfzh(sbkUser.getAac002());
                sbkOperLog.setCardKh(sbkUser.getAaz500());
                sbkOperLog.setChannelType(ChannelType.GEREN.getValue());
                sbkOperLog.setServiceType(serviceType.getType());

                SpringUtils.getBean(ISbkOperLogService.class).save(sbkOperLog);
            }
        };
    }

    /**
     * 管理后台社保卡操作日志记录
     *
     * @param sbkUser
     * @param serviceType
     * @return
     */
    public static TimerTask recordOper(LoginUser loginUser, SbkUser sbkUser, ServiceType serviceType) {
        return new TimerTask() {
            @Override
            public void run() {
                if (sbkUser.getAac002() == null) {
                    return;
                }

                ISysDeptService sysDeptService = SpringUtils.getBean(ISysDeptService.class);
                List<SysDept> sysDeptList = sysDeptService.selectDeptListById(loginUser.getDeptId());

                SbkOperLog sbkOperLog = new SbkOperLog();
                sbkOperLog.setCardXm(sbkUser.getAac003());
                sbkOperLog.setCardSfzh(sbkUser.getAac002());
                sbkOperLog.setCardKh(sbkUser.getAaz500());
                switch (sysDeptList.size()) {
                    case 1:
                        sbkOperLog.setChannelType(2L);
                        sbkOperLog.setArea(sysDeptList.get(0).getDeptName());
                        break;
                    case 2:
                        sbkOperLog.setChannelType(3L);
                        sbkOperLog.setArea(sysDeptList.get(0).getDeptName());
                        sbkOperLog.setStreet(sysDeptList.get(1).getDeptName());
                        break;
                    case 3:
                        sbkOperLog.setChannelType(4L);
                        sbkOperLog.setArea(sysDeptList.get(0).getDeptName());
                        sbkOperLog.setStreet(sysDeptList.get(1).getDeptName());
                        sbkOperLog.setVillage(sysDeptList.get(2).getDeptName());
                }
                sbkOperLog.setServiceType(serviceType.getType());
                sbkOperLog.setUserId(loginUser.getUserId());
                sbkOperLog.setDeptId(loginUser.getDeptId());
                sbkOperLog.setCreateBy(loginUser.getUsername());

                SpringUtils.getBean(ISbkOperLogService.class).save(sbkOperLog);
            }
        };
    }
}
