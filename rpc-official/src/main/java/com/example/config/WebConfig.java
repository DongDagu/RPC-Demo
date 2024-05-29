package com.example.config;

import com.example.handler.HttpServerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private HttpServerHandler httpServerHandler;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截所有以 /dgdong/ 开头的URL
        registry.addInterceptor(httpServerHandler).addPathPatterns("/dgdong/**");
    }
}
