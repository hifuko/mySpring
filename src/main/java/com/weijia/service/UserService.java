package com.weijia.service;

import com.weijia.spring.*;

import javax.annotation.PostConstruct;

@Component("userService") //bean name
//@Scope("prototype")
public class UserService implements BeanNameAware, InitializingBean, UserInterface {

    @Autowired
    private OrderService orderService;
    private String beanName;
    private String attributeX;
    private User admin;//query db to get admin user

    public void test(){
        System.out.println(orderService);
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public void calculateAttributeX(){

    }

    @PostConstruct //will be called before initializing bean
    public void queryAdmin(){
        // mysql -> admin info -> User object -> assign to admin field
        this.admin = new User("Tom", 18);
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("Initializing method....");
    }

    @Override
    public String toString() {
        return "UserService{" +
                "orderService=" + orderService +
                ", beanName='" + beanName + '\'' +
                ", attributeX='" + attributeX + '\'' +
                ", admin=" + admin +
                '}';
    }
}
