package com.weijia.service;

import com.weijia.spring.MyApplicationContext;

public class Test {
    public static void main(String[] args) {
        MyApplicationContext applicationContext = new MyApplicationContext(AppConfig.class);
        //UserService userService = (UserService) applicationContext.getBean("userService");
        //check if they are the same bean (singleton/prototype)
//        System.out.println(applicationContext.getBean("userService"));//beanName must be matched with value of @Component
//        System.out.println(applicationContext.getBean("userService"));
//        System.out.println(applicationContext.getBean("userService"));
//        System.out.println(applicationContext.getBean("orderService"));

        UserInterface userService = (UserInterface) applicationContext.getBean("userService");

        //userService.test();

    }


}
