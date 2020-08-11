package com.kiger.spring.frame.aop.aspect;

import java.lang.reflect.Method;

/**
 * 封装拦截器回调的通用逻辑，主要封装反射动态调用方法，子类是需要控制调用顺序
 * @author zk_kiger
 * @date 2020/8/3
 */

public abstract class MNAbstractAspectJAdvice {

    private Method aspectMethod;
    private Object aspectTarget;

    public MNAbstractAspectJAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    /**
     * 统一执行切面类的通知方法
     * @param joinPoint
     * @param returnValue
     * @param ex
     * @return
     * @throws Throwable
     */
    protected Object invokeAdviceMethod(MNJoinPoint joinPoint, Object returnValue, Throwable ex) throws Throwable {
        Class<?>[] paramTypes = this.aspectMethod.getParameterTypes();
        if (paramTypes == null || paramTypes.length == 0) {
            return this.aspectMethod.invoke(aspectTarget);
        } else {
            Object[] args = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                if (paramTypes[i] == MNJoinPoint.class) {
                    args[i] = joinPoint;
                } else if (paramTypes[i] == Throwable.class) {
                    args[i] = ex;
                } else if (paramTypes[i] == Object.class) {
                    args[i] = returnValue;
                }
            }
            return this.aspectMethod.invoke(aspectTarget, args);
        }
    }
}
