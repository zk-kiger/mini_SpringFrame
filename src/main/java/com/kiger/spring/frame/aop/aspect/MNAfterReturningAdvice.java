package com.kiger.spring.frame.aop.aspect;

import com.kiger.spring.frame.aop.intercept.MNMethodIntercept;
import com.kiger.spring.frame.aop.intercept.MNMethodInvocation;

import java.lang.reflect.Method;

/**
 * @author zk_kiger
 * @date 2020/8/3
 */

public class MNAfterReturningAdvice extends MNAbstractAspectJAdvice implements MNAdvice, MNMethodIntercept {

    private MNJoinPoint joinPoint;

    public MNAfterReturningAdvice(Method aspectMethod, Object target) {
        super(aspectMethod, target);
    }

    @Override
    public Object invoke(MNMethodInvocation mi) throws Throwable {
        Object retVal = mi.proceed();
        this.joinPoint = mi;
        this.afterReturning(retVal, mi.getMethod(), mi.getArguments(), mi.getThis());
        return retVal;
    }

    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        invokeAdviceMethod(joinPoint, returnValue, null);
    }
}
