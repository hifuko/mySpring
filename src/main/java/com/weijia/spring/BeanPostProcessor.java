package com.weijia.spring;

public interface BeanPostProcessor {

    public Object postProcessBeforeInitialization(String beanName, Object object);
    public Object postProcessAfterInitialization(String beanName, Object object);

}
