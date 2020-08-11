package com.kiger.spring.frame.annotation;

import java.lang.annotation.*;

/**
 * @author zk_kiger
 * @date 2020/8/1
 */

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MNRequestMapping {
    String value() default "";
}
