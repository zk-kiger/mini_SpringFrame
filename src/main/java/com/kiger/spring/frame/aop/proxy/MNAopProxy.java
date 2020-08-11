package com.kiger.spring.frame.aop.proxy;

/**
 * 代理工厂的顶层接口，提供获取代理对象的入口
 * @author zk_kiger
 * @date 2020/8/3
 */

public interface MNAopProxy {

    /** 获取代理对象 */
    Object getProxy();

    /** 通过自定义类加载器获取代理对象 */
    Object getProxy(ClassLoader classLoader);
}
