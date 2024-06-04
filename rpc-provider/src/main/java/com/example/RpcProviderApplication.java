package com.example;

import com.example.config.RegistryConfig;
import com.example.config.RpcConfig;
import com.example.model.ServiceMetaInfo;
import com.example.registry.LocalRegistry;
import com.example.registry.Registry;
import com.example.registry.RegistryFactory;
import com.example.service.impl.ProviderServiceImpl;
import org.example.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RpcProviderApplication {

	public static void main(String[] args) {
		// 注册服务
//		LocalRegistry.register(UserService.class.getName(), ProviderServiceImpl.class);

		// 加载配置文件中以rpc开头的配置项
//		RpcConfig rpcConfig = RpcHolder.INSTANCE.getRpcConfig();

		// RPC 框架初始化（配置和注册中心）
		RpcOfficialApplication.init();
		// 全局配置
		final RpcConfig rpcConfig = RpcOfficialApplication.getRpcConfig();

		// 注册服务到注册中心
		RegistryConfig registryConfig = rpcConfig.getRegistryConfig();

		// 注册服务
		try {
			ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
			serviceMetaInfo.setServiceName(rpcConfig.getName());
			serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
			serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
			Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
			registry.register(serviceMetaInfo);
		}catch (Exception e) {
			throw new RuntimeException(rpcConfig.getName() + " 服务注册失败", e);
		}


		SpringApplication.run(RpcProviderApplication.class, args);
	}

}
