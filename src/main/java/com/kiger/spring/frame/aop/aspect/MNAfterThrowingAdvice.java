package com.kiger.spring.frame.aop.aspect;

import com.kiger.spring.frame.aop.intercept.MNMethodIntercept;
import com.kiger.spring.frame.aop.intercept.MNMethodInvocation;

import java.lang.reflect.Method;

/**
 * @author zk_kiger
 * @date 2020/8/3
 */

public class MNAfterThrowingAdvice extends MNAbstractAspectJAdvice implements MNAdvice, MNMethodIntercept {

    private String throwingName;

    public MNAfterThrowingAdvice(Method aspectMethod, Object target) {
        super(aspectMethod, target);
    }

    public void setThrowingName(String throwingName) {
        this.throwingName = throwingName;
    }

    @Override
    public Object invoke(MNMethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        } catch (Throwable e) {
            invokeAdviceMethod(mi, null, e.getCause());
            throw e;
        }
    }
}
