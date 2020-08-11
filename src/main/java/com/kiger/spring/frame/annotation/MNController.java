package com.kiger.spring.frame.annotation;

import java.lang.annotation.*;

/**
 * 请求Handler
 * @author zk_kiger
 * @date 2020/8/1
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MNController {
    String value() default "";
}
