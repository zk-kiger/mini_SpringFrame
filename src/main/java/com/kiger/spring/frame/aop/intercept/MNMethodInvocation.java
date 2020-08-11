package com.kiger.spring.frame.aop.intercept;

import com.kiger.spring.frame.aop.aspect.MNJoinPoint;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 执行拦截器链，相当于 Spring 中的 ReflectiveMethodInvocation
 * @author zk_kiger
 * @date 2020/8/3
 */

public class MNMethodInvocation implements MNJoinPoint {

    /** 代理对象 */
    private Object proxy;

    /** 代理的目标方法 */
    private Method method;

    /** 代理的目标对象 */
    private Object target;

    /** 代理的目标类 */
    private Class<?> targetClass;

    /** 代理的方法的实参列表 */
    private Object[] arguments;

    /** 通知方法链 */
    private List<Object> interceptorsAndDynamicMethodMatchers;

    /** 保存自定义属性 */
    private Map<String, Object> userAttributes;

    /** 记录当前执行拦截器的索引 */
    private int currentInterceptorIndex = -1;

    public MNMethodInvocation(Object proxy, Method method, Object target, Class<?> targetClass, Object[] arguments, List<Object> interceptorsAndDynamicMethodMatchers) {
        this.proxy = proxy;
        this.method = method;
        this.target = target;
        this.targetClass = targetClass;
        this.arguments = arguments;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    /**
     * 按照拦截器顺序执行通知方法以及业务方法
     * @return
     * @throws Throwable
     */
    public Object proceed() throws Throwable {
        // 如果通知方法执行完了，则执行 JoinPoint 业务方法
        if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
            return this.method.invoke(this.target, this.arguments);
        }

        Object interceptorOrInterceptionAdvice = this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);

        if (interceptorOrInterceptionAdvice instanceof MNMethodIntercept) {
            MNMethodIntercept mi = (MNMethodIntercept) interceptorOrInterceptionAdvice;
            return mi.invoke(this);
        } else {
            return proceed();
        }
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public Object getThis() {
        return this.target;
    }

    @Override
    public void setUserAttribute(String key, Object value) {
        if (value != null) {
            if (this.userAttributes == null) {
                this.userAttributes = new HashMap<>();
            }
            this.userAttributes.put(key, value);
        } else {
            if (this.userAttributes != null) {
                this.userAttributes.remove(key);
            }
        }
    }

    @Override
    public Object getUserAttribute(String key) {
        return (this.userAttributes != null) ? this.userAttributes.get(key) : null;
    }
}
