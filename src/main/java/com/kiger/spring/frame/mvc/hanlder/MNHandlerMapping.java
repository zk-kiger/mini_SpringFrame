package com.kiger.spring.frame.mvc.hanlder;

import com.kiger.spring.frame.mvc.servlet.HandlerMapping;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author zk_kiger
 * @date 2020/8/3
 */

@Getter
@Setter
@AllArgsConstructor
public class MNHandlerMapping implements HandlerMapping {

    /** 目标方法所在的 Controller 对象 */
    private Object controller;

    /** URL 对应的目标方法 */
    private Method method;

    /** URL 封装为模式 */
    private Pattern pattern;
}
