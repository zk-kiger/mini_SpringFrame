package com.kiger.spring.frame.beans;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * BeanDefinition主要用于保存 bean 相关的配置信息
 * @author zk_kiger
 * @date 2020/8/1
 */

@Getter
@Setter
public class MNBeanDefinition {

    /** 原生 bean 的全类名 */
    private String beanClassName;

    /** 是否延迟加载 */
    private boolean lazyInit = false;

    /** 保存beanName，在IOC容器中存储的key */
    private String factoryBeanName;
}
