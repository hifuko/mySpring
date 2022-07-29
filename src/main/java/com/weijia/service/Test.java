package com.weijia.service;

import com.weijia.spring.MyApplicationContext;

public class Test {
    public static void main(String[] args) {
        MyApplicationContext myApplicationContext = new MyApplicationContext(AppConfig.class);
        UserService userService = (UserService) myApplicationContext.getBean("userService");

    }


}
