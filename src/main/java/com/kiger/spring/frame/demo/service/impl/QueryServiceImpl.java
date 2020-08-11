package com.kiger.spring.frame.demo.service.impl;

import com.kiger.spring.frame.annotation.MNService;
import com.kiger.spring.frame.demo.service.QueryService;

import java.time.LocalDateTime;

/**
 * @author zk_kiger
 * @date 2020/8/3
 */

@MNService
public class QueryServiceImpl implements QueryService {

    @Override
    public String query(String name) throws Exception {
        throw new RuntimeException("故意抛出异常..");
//        return LocalDateTime.now().toString();
    }

    @Override
    public String add() {
        return LocalDateTime.now().toString();
    }

}
