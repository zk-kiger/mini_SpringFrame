package com.kiger.spring.frame.beans.config;

import com.sun.istack.internal.Nullable;

/**
 * bean初始化前后的后置回调接口
 * @author zk_kiger
 * @date 2020/8/2
 */

public interface MNBeanPostProcessor {

    @Nullable
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Nullable
    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }

}
