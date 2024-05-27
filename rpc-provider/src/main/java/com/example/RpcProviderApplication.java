package com.example;

import com.example.registry.LocalRegistry;
import com.example.service.impl.ProviderServiceImpl;
import org.example.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RpcProviderApplication {

	public static void main(String[] args) {
		// 注册服务
		LocalRegistry.register(UserService.class.getName(), ProviderServiceImpl.class);
		SpringApplication.run(RpcProviderApplication.class, args);
	}

}
