package com.weijia.service;

import com.weijia.spring.BeanPostProcessor;
import com.weijia.spring.Component;

@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public void postProcessBeforeInitialization(String beanName, Object bean) {
        //we can do some special things for userService bean...
        if ("userService".equals(beanName)){
            System.out.println("before initializing userService bean...");
        }
        //we can also process beans based on the class type
        if (bean.getClass().equals(UserService.class)){
            System.out.println("before initializing a bean of UserService class...");
        }

        //we can also process all beans...

    }

    @Override
    public void postProcessAfterInitialization(String beanName, Object bean) {

    }
}
