package com.kiger.spring.frame.aop.intercept;

/**
 * 方法拦截器是 AOP 代码增强的基本组成单元
 * @author zk_kiger
 * @date 2020/8/3
 */

public interface MNMethodIntercept {

    Object invoke(MNMethodInvocation mi) throws Throwable;
}
