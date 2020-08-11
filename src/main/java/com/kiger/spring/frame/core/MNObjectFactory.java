package com.kiger.spring.frame.core;

/**
 * 近似于 FactoryBean，用于自定义创建某个bean实例
 * @author zk_kiger
 * @date 2020/8/2
 */

@FunctionalInterface
public interface MNObjectFactory<T> {

    /**
     * 使用函数式编程返回自定义实例
     * @return
     */
    T getObject();
}
