package com.kiger.spring.frame.mvc.view;

import com.kiger.spring.frame.mvc.servlet.ViewResolver;

import java.io.File;
import java.util.Locale;

/**
 * @author zk_kiger
 * @date 2020/8/3
 */

public class MNViewResolver implements ViewResolver {

    private final String DEFAULT_TEMPATE_SUFFIX = ".html";

    private File templateRootDir;
    private String viewName;

    public MNViewResolver(String templateRootPath) {
        this.templateRootDir = new File(templateRootPath);
    }

    @Override
    public MNView resolveViewName(String viewName, Locale locale) throws Exception {
        this.viewName = viewName;
        if (viewName == null || "".equals(viewName.trim())) { return null; }
        viewName = viewName.endsWith(DEFAULT_TEMPATE_SUFFIX) ? viewName : (viewName + DEFAULT_TEMPATE_SUFFIX);
        File templateFile = new File((this.templateRootDir.getPath() + "/" + viewName).replaceAll("/+", "/"));
        return new MNView(templateFile);
    }
}
