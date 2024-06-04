package com.example;

import com.example.config.RegistryConfig;
import com.example.config.RpcConfig;
import com.example.constant.RpcConstant;
import com.example.registry.EtcdRegistry;
import com.example.registry.Registry;
import com.example.registry.RegistryFactory;
import com.example.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcOfficialApplication {

    private static volatile RpcConfig rpcConfig;


    public static void init(RpcConfig rpcCon){
        rpcConfig = rpcCon;
        log.info("rpc初始化：{}", rpcCon.toString());
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        // 初始化 etcd 客户端
        registry.init(registryConfig);
        log.info("注册中心初始化：{}", registryConfig);
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    public static void init() {
        RpcConfig rpcCon;
        rpcCon = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_PREFIX);
        init(rpcCon);
    }

    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcOfficialApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
