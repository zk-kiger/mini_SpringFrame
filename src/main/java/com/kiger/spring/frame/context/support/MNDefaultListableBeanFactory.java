package com.kiger.spring.frame.context.support;

import com.kiger.spring.frame.beans.MNBeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DefaultListableBeanFactory 是众多IOC容器子类的典型代表
 * @author zk_kiger
 * @date 2020/8/1
 */

public class MNDefaultListableBeanFactory extends MNAbstractApplicationContext {

    /** 存储注册信息的 BeanDefinition */
    protected final Map<String, MNBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }
}
