package com.kiger.spring.frame.aop.aspect;

import com.kiger.spring.frame.aop.intercept.MNMethodIntercept;
import com.kiger.spring.frame.aop.intercept.MNMethodInvocation;

import java.lang.reflect.Method;

/**
 * @author zk_kiger
 * @date 2020/8/3
 */

public class MNMethodBeforeAdvice extends MNAbstractAspectJAdvice implements MNAdvice, MNMethodIntercept {

    private MNJoinPoint joinPoint;

    public MNMethodBeforeAdvice(Method aspectMethod, Object target) {
        super(aspectMethod, target);
    }

    @Override
    public Object invoke(MNMethodInvocation mi) throws Throwable {
        this.joinPoint = mi;
        this.before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }

    public void before(Method method, Object[] args, Object target) throws Throwable {
        invokeAdviceMethod(this.joinPoint, null, null);
    }
}
