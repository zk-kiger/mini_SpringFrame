package com.kiger.spring.frame.context.support;

import com.kiger.spring.frame.beans.MNBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 对 application.properties 配置文件的解析工作
 *  查找、读取、解析
 * @author zk_kiger
 * @date 2020/8/1
 */

public class MNBeanDefinitionReader {

    private List<String> registyBeanClasses = new ArrayList<>();
    private Properties config = new Properties();

    /** 固定配置文件中的 key */
    private final String SCAN_PACKGE = "scanPackge";

    public MNBeanDefinitionReader(String... locations) {
        // 通过 URL 定位找到对应的文件，并转换为文件流，加载为配置类
        try(InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""))) {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        doScanner(config.getProperty(SCAN_PACKGE));
    }

    /**
     * 扫描指定包下的所有类，并存储全类名
     * @param scanPackge
     */
    private void doScanner(String scanPackge) {
        // 将包路径转换为文件路径
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackge.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackge + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = (scanPackge + "." + file.getName().replace(".class", ""));
                registyBeanClasses.add(className);
            }
        }
    }

    /**
     * 将配置文件中的配置信息转化为 BeanDefinition 对象
     * @return
     */
    public List<MNBeanDefinition> loadBeanDefinitions() {
        List<MNBeanDefinition> result = new ArrayList<>();

        try {
            for (String className : registyBeanClasses) {
                Class<?> beanClass = Class.forName(className);
                if (beanClass.isInterface()) {
                    continue;
                }
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));

                // 将当前类作为它所实现接口的实现类
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> i : interfaces) {
                    result.add(doCreateBeanDefinition(i.getName(), beanClass.getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 将每一个类信息都转换为 BeanDefinition
     * @param factoryBeanName
     * @param beanClassName
     * @return
     */
    private MNBeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
        MNBeanDefinition beanDefinition = new MNBeanDefinition();
        beanDefinition.setFactoryBeanName(factoryBeanName);
        beanDefinition.setBeanClassName(beanClassName);
        return beanDefinition;
    }

    /**
     * 类名首字母变为小写，方便逻辑判断
     * @param simpleName
     * @return
     */
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public Properties getConfig() {
        return config;
    }
}
