package com.example.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  为了跑通流程简历的本地注册器，将服务提供者注册到其本地
 */
public class LocalRegistry {

    // key-服务名、value-服务实现。可通过反射进行调用
    private static final Map<String, Class<?>> map = new ConcurrentHashMap<>();

    public static void register(String serviceName, Class<?> implClass){
        map.put(serviceName, implClass);
        System.out.println(map.entrySet());
    }

    public static Class<?> get(String serviceName) {
        return map.get(serviceName);
    }

    public static void remove(String serviceName) {
        map.remove(serviceName);
    }
}
