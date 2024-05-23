package com.example.proxy;

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

        byte[] serialize = serializer.serialize(rpcRequest);

        // todo 发送http请求


        return null;

    }
}
