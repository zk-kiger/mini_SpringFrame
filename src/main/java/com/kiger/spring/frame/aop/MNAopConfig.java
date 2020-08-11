package com.kiger.spring.frame.aop;

import lombok.Data;

/**
 * AOP 配置封装，与 properties 中的 AOP 属性一一对应
 * @author zk_kiger
 * @date 2020/8/3
 */

@Data
public class MNAopConfig {
    /** 切面表达式 */
    private String pointCut;

    /** 要织入的切面类 */
    private String aspectClass;

    /** 前置通知方法名 */
    private String aspectBefore;

    /** 后置通知方法名 */
    private String aspectAfter;

    /** 异常通知方法名 */
    private String aspectAfterThrow;

    /** 需要通知的异常类型 */
    private String aspectAfterThrowingName;
}
