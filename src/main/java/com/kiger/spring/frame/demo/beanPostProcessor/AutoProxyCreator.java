package com.kiger.spring.frame.demo.beanPostProcessor;

import com.kiger.spring.frame.aop.MNAopConfig;
import com.kiger.spring.frame.aop.proxy.MNAopProxy;
import com.kiger.spring.frame.aop.proxy.MNCglibAopProxy;
import com.kiger.spring.frame.aop.proxy.MNJdkDynamicAopProxy;
import com.kiger.spring.frame.aop.support.MNAdvisedSupport;
import com.kiger.spring.frame.beans.config.MNBeanPostProcessor;
import com.sun.istack.internal.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * bean 进行包装，返回 AOP 代理对象
 * @author zk_kiger
 * @date 2020/8/4
 */

public class AutoProxyCreator implements MNBeanPostProcessor {

    private final String aopLocation = "classpath:application.properties";

    @Override
    public Object postProcessAfterInitialization(@Nullable Object bean, String beanName) {
        if (bean != null) {
            return wrapIfNecessary(bean, beanName);
        }
        return bean;
    }

    protected Object wrapIfNecessary(Object bean, String beanName) {
        Object result = bean;
        MNAdvisedSupport config = getAopConfig();
        config.setTarget(bean);
        config.setTargetClass(bean.getClass());

        if (config.pointCutMatch()) {
            result = createProxy(config).getProxy();
            return result;
        }

        return bean;
    }

    private MNAopProxy createProxy(MNAdvisedSupport config) {
        Class targetClass = config.getTargetClass();
        if (targetClass.getInterfaces().length > 0) {
            return new MNJdkDynamicAopProxy(config);
        }
        return new MNCglibAopProxy();
    }

    private MNAdvisedSupport getAopConfig() {
        MNAopConfig aopConfig = new MNAopConfig();
        Properties config = new Properties();
        try(InputStream is = this.getClass().getClassLoader().getResourceAsStream(aopLocation.replace("classpath:", ""))) {
            config.load(is);
            aopConfig.setPointCut(config.getProperty("pointCut"));
            aopConfig.setAspectClass(config.getProperty("aspectClass"));
            aopConfig.setAspectBefore(config.getProperty("aspectBefore"));
            aopConfig.setAspectAfter(config.getProperty("aspectAfter"));
            aopConfig.setAspectAfterThrow(config.getProperty("aspectAfterThrow"));
            aopConfig.setAspectAfterThrowingName(config.getProperty("aspectAfterThrowingName"));

            return new MNAdvisedSupport(aopConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
