package com.kiger.spring.frame.beans;

import lombok.Data;

/**
 * 主要用于封装创建后的对象实例，代理对象或者原生对象
 * @author zk_kiger
 * @date 2020/8/1
 */

public class MNBeanWrapper {

    private Object wrappedInstance;
    private Class<?> wrappedClass;

    public MNBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance() {
        return wrappedInstance;
    }

    // 可能返回的是代理对象Class $Proxy0
    public Class<?> getWrappedClass() {
        return this.wrappedInstance.getClass();
    }
}
