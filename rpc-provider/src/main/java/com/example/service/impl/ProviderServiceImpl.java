package com.example.service.impl;

import org.example.service.User;
import org.example.service.UserService;

public class ProviderServiceImpl implements UserService {
    @Override
    public String method(User user) {
        String string = user.getName() + "到此一游！";
        System.out.println("Provider:" + string);
        return string;
    }
}
