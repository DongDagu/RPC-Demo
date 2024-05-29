package com.example.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.example.model.RpcRequest;
import com.example.serializer.JdkSerializer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *  集成JDK动态代理处理器，实现invoke方法，调用被代理对象的方法时会首先执行invoke方法中的逻辑。
 */
public class ServiceProxy implements InvocationHandler {


    /**
     *  @param proxy 代理对象
     *  @param method 调用的方法
     *  @param args 参数列表
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        JdkSerializer serializer = new JdkSerializer();

        // 请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        // 序列化
        byte[] bodyBytes = serializer.serialize(rpcRequest);

        try {
            // 发送请求
            // todo 注意，这里地址被硬编码了（需要使用注册中心和服务发现机制解决）
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8010/dgdong/providerMethod")
                    .body(bodyBytes)
                    .execute()) {
                byte[] result = httpResponse.bodyBytes();
                // 反序列化
//                String rpcResponse = serializer.deserialize(result, String.class);
//                return rpcResponse;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
