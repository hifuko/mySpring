package com.weijia.spring;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class MyApplicationContext {
    private Class configClass;
    //to store beans. <beanName, BeanDefinition>
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    //singleton bean pool
    private ConcurrentHashMap<String, Object> singletonObjectMap = new ConcurrentHashMap<>();

    private ArrayList<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    //1. find the directory path to scan (value of @ComponentScan of configClass)
    //2. parse the path to get all files under the directory
    //3. if it is a .class file, check if it is a bean (if @Component exists)
    //4. if it is a bean, get beanName (@Component value), set class type and scope of BeanDefinition, put into beanDefinitionMap
    //5. instantiate all singleton beans and put into singletonObjectMap
    public MyApplicationContext(Class configClass) {
        this.configClass = configClass;
        //scan the .class files under the directory
        if (configClass.isAnnotationPresent(ComponentScan.class)){
            ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            String value = componentScanAnnotation.value(); //com.weijia.service
            String path = value.replace(".", "/"); //com/weijia/service -> relative path
            ClassLoader classLoader = MyApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);
            File file = new File(resource.getFile());
            if (file.isDirectory()){
                //get all files under the directory
                File[] files = file.listFiles();
                for (File f : files) {
                    String fileName = f.getAbsolutePath();
                    System.out.println(fileName);
                    ///home/ivana/IdeaProjects/MySpring/MySpring/target/classes/com/weijia/service/UserService.class
                    ///home/ivana/IdeaProjects/MySpring/MySpring/target/classes/com/weijia/service/AppConfig.class
                    ///home/ivana/IdeaProjects/MySpring/MySpring/target/classes/com/weijia/service/Test.class
                    if (fileName.endsWith(".class")){
                        //check it the file is a bean
                        String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"))//com/weijia/service/UserService
                                .replace("/", ".");//com.weijia.service.UserService
                        System.out.println(className);
                        //com.weijia.service.UserService
                        //com.weijia.service.AppConfig
                        //com.weijia.service.Test

                        //create BeanDefinition and put the bean into the map
                        try {
                            Class<?> clazz = classLoader.loadClass(className);
                            //is a bean
                            if (clazz.isAnnotationPresent(Component.class)){
                                //check if clazz implements BeanPostProcessor
                                if (BeanPostProcessor.class.isAssignableFrom(clazz)){
                                    BeanPostProcessor instance = (BeanPostProcessor) clazz.newInstance();
                                    beanPostProcessorList.add(instance);
                                }
                                String beanName = clazz.getAnnotation(Component.class).value();
                                //if no bean name is given in annotation
                                if (beanName.equals("")){
                                    //create a name for the bean
                                    beanName = Introspector.decapitalize(clazz.getSimpleName());
                                }

                                BeanDefinition beanDefinition = new BeanDefinition();
                                beanDefinition.setType(clazz);

                                if (clazz.isAnnotationPresent(Scope.class)) {
                                    //singleton or prototype
                                    beanDefinition.setScope(clazz.getAnnotation(Scope.class).value());
                                } else {
                                    //no value(default): singleton
                                    beanDefinition.setScope("singleton");
                                }
                                beanDefinitionMap.put(beanName, beanDefinition);
                            }
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        } catch (InstantiationException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }

        //instantiate singleton beans
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if ("singleton".equals(beanDefinition.getScope())){
                Object bean = createBean(beanName, beanDefinition);
                singletonObjectMap.put(beanName, bean);
            }
        }
    }

    //1. instantiate beans
    //2. dependency injection
    private Object createBean(String beanName, BeanDefinition beanDefinition){
        Object instance = null;
        try {
            instance = beanDefinition.getType().getConstructor().newInstance();

            //dependency injection
            //check if the bean is declared as a field with @Autowired
            for (Field field: instance.getClass().getDeclaredFields()){
                if (field.isAnnotationPresent(Autowired.class)){
                    field.setAccessible(true); //so that the bean can be assigned value
                    //set the object as the field: use the field name to find the bean object in map
                    field.set(instance, getBean(field.getName()));
                }
            }
            //BeanNameAware call back
            if (instance instanceof BeanNameAware){
                ((BeanNameAware) instance).setBeanName(beanName);
            }

            //AOP - before initializing bean
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                beanPostProcessor.postProcessBeforeInitialization(beanName, instance);
            }

            //initialize bean
            if (instance instanceof InitializingBean){
                ((InitializingBean) instance).afterPropertiesSet();
            }

            //AOP - after initializing bean
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                beanPostProcessor.postProcessAfterInitialization(beanName, instance);
            }


        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return instance;
    }

    //if bean is singleton, take it from the singleton pool
    //if bean is prototype, create one
    public Object getBean(String beanName){
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null){
            throw new NullPointerException();
        } else {
            String scope = beanDefinition.getScope();
            if ("singleton".equals(scope)){
                Object bean = singletonObjectMap.get(beanName);
                if (bean == null){
                    //if it doesn't exist in the singleton pool
                    Object bean1 = createBean(beanName, beanDefinition);
                    singletonObjectMap.put(beanName, bean1);
                    return bean1;
                }
                return bean;
            } else {
                //prototype
                return createBean(beanName, beanDefinition);
            }
        }
    }
}
