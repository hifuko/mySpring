package com.weijia.service;

import com.weijia.spring.BeanPostProcessor;
import com.weijia.spring.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(String beanName, Object bean) {
        //we can do some special things for userService bean...
        if ("userService".equals(beanName)){
            System.out.println("before initializing userService bean...");
        }
        //we can also process beans based on the class type
        if (bean.getClass().equals(UserService.class)){
            System.out.println("before initializing a bean of UserService class...");
        }
        //we can also process all beans...

        return bean;

    }

    @Override
    public Object postProcessAfterInitialization(String beanName, Object bean) {
        //if it's a userService bean, then return a proxy instance
        if (beanName.equals("userService")){
            Object proxyInstance = Proxy.newProxyInstance(MyBeanPostProcessor.class.getClassLoader(),
                    bean.getClass().getInterfaces(),
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            System.out.printf("[DEBUG] \tMethod: %s", method.getName());
                            return method.invoke(bean, args);
                        }
                    });
            return proxyInstance;
        }
        return bean;
    }
}
