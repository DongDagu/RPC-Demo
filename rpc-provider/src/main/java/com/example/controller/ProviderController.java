package com.example.controller;


import com.example.service.impl.ProviderServiceImpl;
import org.example.service.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dgdong")
public class ProviderController {


    @PostMapping("/providerMethod")
    @ResponseBody
    public String providerMethod(User user) {
        ProviderServiceImpl providerService = new ProviderServiceImpl();
        return providerService.method(user);
    }

}
