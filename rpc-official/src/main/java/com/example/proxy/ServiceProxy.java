package com.example.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.example.RpcOfficialApplication;
import com.example.config.RpcConfig;
import com.example.constant.RpcConstant;
import com.example.model.RpcRequest;
import com.example.model.RpcResponse;
import com.example.model.ServiceMetaInfo;
import com.example.registry.EtcdRegistry;
import com.example.registry.Registry;
import com.example.registry.RegistryFactory;
import com.example.serializer.JdkSerializer;
import com.example.serializer.Serializer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  集成JDK动态代理处理器，实现invoke方法，调用被代理对象的方法时会首先执行invoke方法中的逻辑。
 */
public class ServiceProxy implements InvocationHandler {

    private static final Serializer serializer = new JdkSerializer();

    /**
     *  @param proxy 代理对象
     *  @param method 调用的方法
     *  @param args 参数列表
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//         指定序列化器
//        JdkSerializer serializer = new JdkSerializer();

        // 请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        // 从注册中心获取服务提供者地址
        RpcConfig rpcConfig = RpcOfficialApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_VERSION);
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            throw new RuntimeException("暂无服务地址");
        }
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", rpcRequest.getMethodName());
        // 实验机器就一个节点
        ServiceMetaInfo serviceMeta = serviceMetaInfoList.get(0);
        return doHttpRequest(serviceMeta, serializer.serialize(rpcRequest));
    }


    private static RpcResponse doHttpRequest(ServiceMetaInfo selectedServiceMetaInfo, byte[] bodyBytes) throws IOException {

        // 发送 HTTP 请求
        try (HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
                .body(bodyBytes)
                .execute()) {
            byte[] result = httpResponse.bodyBytes();
            // 反序列化
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return rpcResponse;
        }
    }
}
