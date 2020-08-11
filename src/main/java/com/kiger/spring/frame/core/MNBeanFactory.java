package com.kiger.spring.frame.core;

/**
 * bean工厂：Spring IOC容器的顶层接口，管理bean，实例化、定位、配置应用程序中对象之间的依赖
 * @author zk_kiger
 * @date 2020/8/1
 */

public interface MNBeanFactory {

    /**
     * 根据 beanName 从 IOC容器中获取一个实例 bean
     * @param beanName
     * @return
     */
    Object getBean(String beanName) throws Exception;

    Object getBean(Class<?> beanClass) throws Exception;
}
