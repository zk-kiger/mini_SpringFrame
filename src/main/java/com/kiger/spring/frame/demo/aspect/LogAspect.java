package com.kiger.spring.frame.demo.aspect;

import com.kiger.spring.frame.aop.aspect.MNJoinPoint;

import java.util.Arrays;

/**
 * @author zk_kiger
 * @date 2020/8/3
 */

public class LogAspect {

    public void before(MNJoinPoint joinPoint) {
        joinPoint.setUserAttribute("startTime" + joinPoint.getMethod().getName(),
                System.currentTimeMillis());
        System.out.println("执行前置通知...");
        System.out.println("TargetObject: " + joinPoint.getThis());
        System.out.println("args: " + Arrays.toString(joinPoint.getArguments()));
    }

    public void after(MNJoinPoint joinPoint, Object result) {
        System.out.println("执行后置通知...");
        System.out.println("TargetObject: " + joinPoint.getThis());
        System.out.println("args: " + Arrays.toString(joinPoint.getArguments()));
        System.out.println("方法返回结果: " + result);
        long startTime = (long) joinPoint.getUserAttribute("startTime" + joinPoint.getMethod().getName());
        long endTime = System.currentTimeMillis();
        System.out.println("use time = " + (endTime - startTime));
    }

    public void afterThrowing(MNJoinPoint joinPoint, Throwable e) {
        System.out.println("异常通知...");
        System.out.println("TargetObject: " + joinPoint.getThis());
        System.out.println("args: " + Arrays.toString(joinPoint.getArguments()));
        System.out.println("异常问题: " + e.toString());
    }

}
