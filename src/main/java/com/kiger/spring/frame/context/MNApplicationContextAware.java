package com.kiger.spring.frame.context;

import com.kiger.spring.frame.context.support.MNAbstractApplicationContext;

/**
 * 用于组件获取IOC容器的顶层接口
 * @author zk_kiger
 * @date 2020/8/1
 */

public interface MNApplicationContextAware {

    void setApplicationContext(MNAbstractApplicationContext applicationContext);
}
