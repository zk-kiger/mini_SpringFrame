package com.kiger.spring.frame.demo.controller;

import com.kiger.spring.frame.annotation.MNAutowired;
import com.kiger.spring.frame.annotation.MNController;
import com.kiger.spring.frame.annotation.MNRequestMapping;
import com.kiger.spring.frame.annotation.MNRequestParam;
import com.kiger.spring.frame.demo.service.QueryService;
import com.kiger.spring.frame.mvc.view.MNModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zk_kiger
 * @date 2020/8/3
 */

@MNController
@MNRequestMapping("/")
public class PageAction {

    @MNAutowired
    QueryService queryService;

    @MNRequestMapping("/first.html")
    public MNModelAndView query(@MNRequestParam("teacher") String teacher) throws Throwable {
        String result = queryService.query(teacher);
        Map<String, Object> model = new HashMap<>();
        model.put("teacher", teacher);
        model.put("data", result);
        model.put("token", "123456");
        return new MNModelAndView("first.html", model);
    }
}
