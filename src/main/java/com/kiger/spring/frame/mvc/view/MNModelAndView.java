package com.kiger.spring.frame.mvc.view;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 用于封装页面模板和要往页面传输的参数的对应关系
 * @author zk_kiger
 * @date 2020/8/3
 */

@Setter
@Getter
public class MNModelAndView {

    /** 页面模板名称 */
    private String viewName;

    /** 送往页面的参数 */
    private Map<String, ?> model;

    public MNModelAndView(String viewName) {
        this(viewName, null);
    }

    public MNModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }
}
