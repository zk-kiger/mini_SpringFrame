package com.kiger.spring.frame.annotation;

import java.lang.annotation.*;

/**
 * 业务逻辑，注入接口
 * @author zk_kiger
 * @date 2020/8/1
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MNService {
    String value() default "";
}
