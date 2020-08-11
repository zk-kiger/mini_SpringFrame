package com.kiger.spring.frame.annotation;

import java.lang.annotation.*;

/**
 * 自动注入
 * @author zk_kiger
 * @date 2020/8/1
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MNAutowired {
    String value() default "";
}
