package com.example.controller;

import com.example.proxy.ServiceProxyFactory;
import org.example.service.User;
import org.example.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rpc")
public class consumerController {


    @PostMapping("/startTest")
    public void consumerMethod() {

        // 初始化一个代理对象
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User dgdong = User.builder().name("dgdong").build();
        String string = userService.method(dgdong);
        System.out.println(string);

    }



}
