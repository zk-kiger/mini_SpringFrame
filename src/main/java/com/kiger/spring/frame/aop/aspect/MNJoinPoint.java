package com.kiger.spring.frame.aop.aspect;

import java.lang.reflect.Method;

/**
 * 回调连接点，通过它可以获得被代理的业务方法的所有信息
 * @author zk_kiger
 * @date 2020/8/3
 */

public interface MNJoinPoint {
    /**
     * 业务方法本身
     * @return
     */
    Method getMethod();

    /**
     * 业务方法的实参列表
     * @return
     */
    Object[] getArguments();

    /**
     * 业务方法所属的实例对象
     * @return
     */
    Object getThis();

    /**
     * 在 JoinPoint 中添加自定义属性
     */
    void setUserAttribute(String key, Object value);

    Object getUserAttribute(String key);
}
