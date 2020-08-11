package com.kiger.spring.frame.aop.support;

import com.kiger.spring.frame.aop.MNAopConfig;
import com.kiger.spring.frame.aop.aspect.MNAfterReturningAdvice;
import com.kiger.spring.frame.aop.aspect.MNAfterThrowingAdvice;
import com.kiger.spring.frame.aop.aspect.MNMethodBeforeAdvice;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 对 AOP 配置的解析
 * @author zk_kiger
 * @date 2020/8/3
 */

public class MNAdvisedSupport {

    private Class<?> targetClass;
    private Object target;
    private Pattern pointCutClassPattern;

    private transient Map<Method, List<Object>> methodCache;

    private MNAopConfig config;

    public MNAdvisedSupport(MNAopConfig config) {
        this.config = config;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public List<Object> getInterceptorAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws NoSuchMethodException {
        // 先从之前的方法缓存中获取
        List<Object> cached = methodCache.get(method);

        // 缓存未命中，就将新的目标类的方法进行缓存
        if (cached == null) {
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cached = methodCache.get(m);
            this.methodCache.put(m, cached);
        }

        return cached;
    }

    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }

    /**
     * 解析目标类
     */
    private void parse() {
        // 切点表达式
        String pointCut = config.getPointCut().replaceAll("\\.\\.", "\\.");
        String pointCutForClass = pointCut.substring(0, pointCut.lastIndexOf("("));
        pointCutClassPattern = Pattern.compile("class " + pointCutForClass.substring(pointCutForClass.lastIndexOf(" ") + 1));

        methodCache = new HashMap<>();
        Pattern pattern = Pattern.compile(pointCut);
        try {
            // 切面类
            Class aspectClass = Class.forName(config.getAspectClass());
            Map<String, Method> aspectMethods = new HashMap<>();
            // 通知方法
            for (Method m : aspectClass.getMethods()) {
                aspectMethods.put(m.getName(), m);
            }

            // 目标类的原生方法
            for (Method m : targetClass.getMethods()) {
                String methodString = m.toString();
                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }

                // 使用切点表达式与目标类的方法进行匹配
                Matcher matcher = pattern.matcher(methodString);
                if (matcher.matches()) {
                    // 满足切面规则的类，添加 AOP 通知方法
                    List<Object> advices = new LinkedList<>();
                    // 前置通知
                    if (!(config.getAspectBefore() == null || "".equals(config.getAspectBefore().trim()))) {
                        advices.add(new MNMethodBeforeAdvice(aspectMethods.get(config.getAspectBefore()), aspectClass.newInstance()));
                    }
                    // 后置通知
                    if (!(config.getAspectAfter() == null || "".equals(config.getAspectAfter().trim()))) {
                        advices.add(new MNAfterReturningAdvice(aspectMethods.get(config.getAspectAfter()), aspectClass.newInstance()));
                    }
                    // 异常通知
                    if (!(config.getAspectAfterThrow() == null || "".equals(config.getAspectAfterThrow().trim()))) {
                        MNAfterThrowingAdvice afterThrowingAdvice = new MNAfterThrowingAdvice(aspectMethods.get(config.getAspectAfterThrow()), aspectClass.newInstance());
                        afterThrowingAdvice.setThrowingName(config.getAspectAfterThrowingName());
                        advices.add(afterThrowingAdvice);
                    }
                    methodCache.put(m, advices);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

