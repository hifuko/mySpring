package com.weijia.service;

import com.weijia.spring.*;

@Component("userService") //bean name
//@Scope("prototype")
public class UserService implements BeanNameAware, InitializingBean, UserInterface {

    @Autowired
    private OrderService orderService;
    private String beanName;
    private String attributeX;

    public void test(){
        System.out.println(orderService);
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public void calculateAttributeX(){

    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("Initializing method....");
    }
}
