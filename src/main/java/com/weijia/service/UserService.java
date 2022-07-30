package com.weijia.service;

import com.weijia.spring.Autowired;
import com.weijia.spring.Component;
import com.weijia.spring.Scope;

@Component("userService") //bean name
//@Scope("prototype")
public class UserService {

    @Autowired
    private OrderService orderService;

    public void test(){
        System.out.println(orderService);
    }
}
