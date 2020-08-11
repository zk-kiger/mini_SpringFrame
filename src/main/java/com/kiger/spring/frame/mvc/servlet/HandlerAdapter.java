package com.kiger.spring.frame.mvc.servlet;

import com.kiger.spring.frame.mvc.view.MNModelAndView;
import com.sun.istack.internal.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zk_kiger
 * @date 2020/8/3
 */

public interface HandlerAdapter {

    boolean supports(Object handler);

    @Nullable
    MNModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;

}
