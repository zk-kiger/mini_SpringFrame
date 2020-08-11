package com.kiger.spring.frame.context.support;

import com.kiger.spring.frame.annotation.MNAutowired;
import com.kiger.spring.frame.annotation.MNController;
import com.kiger.spring.frame.annotation.MNService;
import com.kiger.spring.frame.beans.MNBeanDefinition;
import com.kiger.spring.frame.beans.MNBeanWrapper;
import com.kiger.spring.frame.beans.config.MNBeanPostProcessor;
import com.kiger.spring.frame.core.MNBeanFactory;
import com.kiger.spring.frame.core.MNObjectFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 用户使用入口，主要实现AbstractApplicationContext的refresh()方法和BeanFactory的getBean()
 * 完成IOC、DI、AOP的衔接
 * @author zk_kiger
 * @date 2020/8/1
 */

public class MNApplicationContext extends MNDefaultListableBeanFactory implements MNBeanFactory {

    private String[] configLocations;
    private MNBeanDefinitionReader reader;

    /** 一级缓存: 单例bean IOC容器 */
    private Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    /** 二级缓存: 保存创建实例bean的工厂 */
    private final Map<String, MNObjectFactory<?>> singletonFactories = new HashMap<>(16);

    /** 三级缓存: 保存由工厂创建出来的实例 */
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

    /** 正在创建bean的集合 */
    private final Set<String> singletonsCurrentlyInCreation =
            Collections.newSetFromMap(new ConcurrentHashMap<>(16));

    /** 通用bean IOC容器缓存 */
    private Map<String, MNBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>(256);

    /** 保存 BeanPostProcessor */
    private List<MNBeanPostProcessor> beanPostProcessors = new CopyOnWriteArrayList<>();

    /** Whether to automatically try to resolve circular references between beans. */
    private boolean allowCircularReferences = true;

    public MNApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() throws Exception {
        // 1.定位配置文件
        reader = new MNBeanDefinitionReader(configLocations);

        // 2.加载配置文件，扫描相关的类，将类信息封装为 BeanDefinition
        // 此处为了 AOP 方便，顺便加载了指定包下的 BeanPostProcessor,Spring 中
        // 通常是使用 @Import 注解注入组件
        List<MNBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

        // 3.注册，把每个bean配置信息保存到容器中
        doRegisterBeanDefinition(beanDefinitions);

        // 4.注册 BeanPostProcessor
        registerBeanPostProcessors();

        // 5.初始化非延迟加载bean
        finishBeanFactoryInitialization();
    }

    /**
     * 注册 BeanPostProcessor
     */
    private void registerBeanPostProcessors() {
        List<MNBeanPostProcessor> list = new ArrayList<>();
        for (Map.Entry<String, MNBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            MNBeanDefinition beanDefinition = beanDefinitionEntry.getValue();
            try {
                Class clazz = Class.forName(beanDefinition.getBeanClassName());
                for (Class interfaceClass : clazz.getInterfaces()) {
                    if (interfaceClass == MNBeanPostProcessor.class) {
                        Object bean = getBean(beanDefinition.getFactoryBeanName());
                        list.add((MNBeanPostProcessor) bean);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.beanPostProcessors.addAll(list);
    }

    /**
     * 初始化非延迟加载的bean
     */
    private void finishBeanFactoryInitialization() {
        for (Map.Entry<String, MNBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将bean的注册信息保存到父类MNDefaultListableBeanFactory的beanDefinitionMap
     * @param beanDefinitions
     */
    private void doRegisterBeanDefinition(List<MNBeanDefinition> beanDefinitions) throws Exception {
        for (MNBeanDefinition beanDefinition : beanDefinitions) {
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The “" + beanDefinition.getFactoryBeanName() + "” is exist!");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }

    /** 依赖注入(DI)入口方法 */
    @Override
    public Object getBean(String beanName) throws Exception {

        // 尝试从 singletonCache 中获取
        Object bean = getSingleton(beanName);

        if (bean == null) {
            bean = getSingleton(beanName, () -> {
                return createBean(beanName, super.beanDefinitionMap.get(beanName));
            });
        }

        return bean;
    }

    /**
     * 创建 bean
     *  1.创建实例对象
     *  2.如果支持循环依赖，保存当前bean的 ObjectFactory 到二级缓存singletonFactories
     *  3.属性注入(依赖注入)
     *  4.实例化bean(实例化前后执行 BeanPostProcessor 接口方法)
     * @param beanName
     * @param beanDefinition
     * @return
     */
    private Object createBean(String beanName, MNBeanDefinition beanDefinition) {
        MNBeanWrapper instanceWrapper = null;

        if (instanceWrapper == null) {
            instanceWrapper = createBeanInstance(beanName, beanDefinition);
        }

        final Object bean = instanceWrapper.getWrappedInstance();
        boolean earlySingletonExposure = (this.allowCircularReferences &&
                isSingletonCurrentlyInCreation(beanName));
        if (earlySingletonExposure) {
            addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, beanDefinition, bean));
        }

        // Initialize the bean instance.
        Object exposedObject = bean;
        try {
            populateBean(beanName, beanDefinition, bean);
            exposedObject = initializeBean(beanName, exposedObject, beanDefinition);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exposedObject;
    }

    /**
     * 初始化实例对象调用，并在执行前后调用 BeanPostProcessor 接口方法处理回调
     * @param beanName
     * @param bean
     * @param beanDefinition
     * @return
     */
    private Object initializeBean(String beanName, Object bean, MNBeanDefinition beanDefinition) {

        // TODO 在执行构造方法之前处理 BeanFactoryAware 接口,注入容器

        Object wrappedBean = bean;
        // TODO 执行后置处理器前置通知
        // wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);

        try {
            // TODO 给 bean初始化机会，执行 InitializingBean 接口的初始化方法
            // invokeInitMethods(beanName, wrappedBean, beanDefinition);
        }
        catch (Throwable ex) {
        }

        // TODO 执行后置处理器后置通知
        wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);

        return wrappedBean;
    }

    /**
     * 执行 BeanPostProcessor 的后置通知
     * @param wrappedBean
     * @param beanName
     * @return
     */
    private Object applyBeanPostProcessorsAfterInitialization(Object wrappedBean, String beanName) {
        if (this.beanPostProcessors.isEmpty()) {
            return wrappedBean;
        }

        Object result = wrappedBean;
        // 遍历每一个 BeanPostProcessor 调用后置通知方法，AOP 会返回代理对象
        for (MNBeanPostProcessor processor : this.beanPostProcessors) {
            Object current = processor.postProcessAfterInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }

    /**
     * 创建bean的对象实例
     * @param beanName
     * @param beanDefinition
     * @return
     */
    private MNBeanWrapper createBeanInstance(String beanName, MNBeanDefinition beanDefinition) {
        MNBeanWrapper beanWrapper = null;
        String className = beanDefinition.getBeanClassName();

        try {
            if (this.singletonObjects.containsKey(beanName)) {
                beanWrapper = new MNBeanWrapper(this.singletonObjects.get(beanName));
            } else {
                Class<?> clazz = Class.forName(className);
                beanWrapper = new MNBeanWrapper(clazz.newInstance());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return beanWrapper;
    }

    /**
     * 对当前对象的属性(标有@Autowired的字段)进行依赖注入
     * @param beanName
     * @param beanDefinition
     * @param instance
     */
    private void populateBean(String beanName, MNBeanDefinition beanDefinition, Object instance) {
        Class clazz = instance.getClass();

        if (!(clazz.isAnnotationPresent(MNService.class) || clazz.isAnnotationPresent(MNController.class))) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(MNAutowired.class)) {
                continue;
            }
            // 获取用户指定注入的beanName
            MNAutowired autowired = field.getAnnotation(MNAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if (field.getType().isInterface()) {
                autowiredBeanName = field.getType().getName();
            } else if ("".equalsIgnoreCase(autowiredBeanName)) {
                // 如果没有指定名称，就以字段类型首字母小写作为 beanName
                String typeName = field.getType().getName();
                autowiredBeanName = toLowerFirstCase(typeName.substring(typeName.lastIndexOf(".") + 1));
            }
            field.setAccessible(true);
            try {
                field.set(instance, getBean(autowiredBeanName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 这里是在使用对象工厂创建实例时，可以添加 BeanPostProcessor 来对对象进行包装返回代理对象
     * 这也是为什么使用对象工厂的原因
     * @param beanName
     * @param beanDefinition
     * @param bean
     * @return
     */
    private Object getEarlyBeanReference(String beanName, MNBeanDefinition beanDefinition, Object bean) {
        // TODO 实现BeanPostProcessor对bean进行包装
        return bean;
    }

    /**
     * 保存当前实例的创建工厂用于处理循环依赖
     * @param beanName
     * @param singletonFactory
     */
    protected void addSingletonFactory(String beanName, MNObjectFactory<?> singletonFactory) {
        synchronized (this.singletonObjects) {
            if (!this.singletonObjects.containsKey(beanName)) {
                this.singletonFactories.put(beanName, singletonFactory);
                this.earlySingletonObjects.remove(beanName);
            }
        }
    }

    public Object getSingleton(String beanName) {
        return getSingleton(beanName, true);
    }

    /**
     * 尝试从一级缓存singletonObjects中获取bean实例，
     * 如果获取的bean正在创建中，则尝试在三级缓存中获取，
     * 如果三级缓存没有，则尝试使用二级缓存的工厂创建实例
     * (为了解决循环依赖问题)
     * @param beanName
     * @param allowEarlyReference
     * @return
     */
    protected Object getSingleton(String beanName, boolean allowEarlyReference) {
        Object singletonObject = singletonObjects.get(beanName);
        if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
            synchronized (this.singletonObjects) {
                singletonObject = this.earlySingletonObjects.get(beanName);
                if (singletonObject == null && allowEarlyReference) {
                    MNObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                    if (singletonFactory != null) {
                        singletonObject = singletonFactory.getObject();
                        this.earlySingletonObjects.put(beanName, singletonObject);
                        this.singletonFactories.remove(beanName);
                    }
                }
            }
        }
        return singletonObject;
    }

    /**
     * 第二次获取实例，如果获取不到就会调用对象工厂进行bean的创建
     * 并会将创建的bean保存到一级缓存singletonObjects
     * @param beanName
     * @param singletonFactory
     * @return
     */
    public Object getSingleton(String beanName, MNObjectFactory<?> singletonFactory) {
        synchronized (this.singletonObjects) {
            Object singletonObject = this.singletonObjects.get(beanName);
            if (singletonObject == null) {
                beforeSingletonCreation(beanName);
                boolean newSingleton = false;
                try {
                    singletonObject = singletonFactory.getObject();
                    newSingleton = true;
                } finally {
                    afterSingletonCreation(beanName);
                }
                if (newSingleton) {
                    addSingleton(beanName, singletonObject);
                }
            }
            return singletonObject;
        }
    }

    /**
     * 将创建好的实例bean保存到一级缓存singletonObjects
     * @param beanName
     * @param singletonObject
     */
    private void addSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.put(beanName, singletonObject);
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
        }
    }

    /**
     * bean创建完成从正在创建集合中移除
     * @param beanName
     */
    private void afterSingletonCreation(String beanName) {
        if (singletonsCurrentlyInCreation.contains(beanName)) {
            singletonsCurrentlyInCreation.remove(beanName);
        }
    }

    /**
     * 在创建之前将当前beanName保存到正在创建集合
     * @param beanName
     */
    private void beforeSingletonCreation(String beanName) {
        if (!singletonsCurrentlyInCreation.contains(beanName)) {
            singletonsCurrentlyInCreation.add(beanName);
        }
    }

    /**
     * 判断当前bean是否在正在创建集合中，如果存在说明是循环引用
     * @param beanName
     * @return
     */
    private boolean isSingletonCurrentlyInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }

    /** 依赖注入(DI)入口方法 */
    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return null;
    }

    public Properties getConfig() {
        return this.reader.getConfig();
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
}
