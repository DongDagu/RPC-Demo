package com.example.registry;

import com.example.config.RegistryConfig;
import com.example.model.ServiceMetaInfo;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 注册中心
 * 定义服务初始化、注册服务、注销服务、获取服务节点、服务销毁等方法
 */
public interface Registry {

    // 初始化
    void init(RegistryConfig registryConfig);

    // 注册服务
    void register(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException;

    // 注销服务
    void unRegister(ServiceMetaInfo serviceMetaInfo);

    // 获取服务节点
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    // 心跳检测
    void heartBeat();

    // 服务监听
    void watch(String serviceNodeKey);

    // 服务销毁
    void destroy();
}
