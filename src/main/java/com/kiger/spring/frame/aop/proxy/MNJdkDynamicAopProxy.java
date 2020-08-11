package com.kiger.spring.frame.aop.proxy;

import com.kiger.spring.frame.aop.intercept.MNMethodInvocation;
import com.kiger.spring.frame.aop.support.MNAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author zk_kiger
 * @date 2020/8/3
 */

public class MNJdkDynamicAopProxy implements MNAopProxy, InvocationHandler {

    private MNAdvisedSupport config;

    public MNJdkDynamicAopProxy(MNAdvisedSupport config) {
        this.config = config;
    }

    @Override
    public Object getProxy() {
        return getProxy(config.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader, config.getTargetClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 将每一个 JoinPoint 被代理的业务方法(Method)封装成一个拦截器链
        List<Object> interceptorsAndDynamicMethodMatchers = config.getInterceptorAndDynamicInterceptionAdvice(method, config.getTargetClass());
        // 将拦截器链交给 MethodInvocation 的 process() 方法执行
        MNMethodInvocation invocation = new MNMethodInvocation(proxy, method, this.config.getTarget(),
                this.config.getTargetClass(), args, interceptorsAndDynamicMethodMatchers);
        return invocation.proceed();
    }
}
