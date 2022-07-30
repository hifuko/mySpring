package com.weijia.spring;

public interface BeanPostProcessor {

    public void postProcessBeforeInitialization(String beanName, Object object);
    public void postProcessAfterInitialization(String beanName, Object object);

}
