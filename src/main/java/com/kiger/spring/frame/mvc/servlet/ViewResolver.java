package com.kiger.spring.frame.mvc.servlet;

import com.kiger.spring.frame.mvc.view.MNView;
import com.sun.istack.internal.Nullable;

import java.util.Locale;

/**
 * @author zk_kiger
 * @date 2020/8/3
 */

public interface ViewResolver {

    @Nullable
    MNView resolveViewName(String viewName, Locale locale) throws Exception;
}
