package com.weijia.spring;

//the call back happens after dependency injection
public interface BeanNameAware {

    public void setBeanName(String beanName);
}
