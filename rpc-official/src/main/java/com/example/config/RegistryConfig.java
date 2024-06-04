package com.example.config;

import lombok.Data;

/**
 * 配置链接注册中心所需的信息
 */
@Data
public class RegistryConfig {


    // 使用etcd作为注册中心类别
    private String registry = "etcd";

    // 注册中心地址，etcd服务2379端口用于客户端链接，2380用于节点通讯
    private String address = "http://localhost:2379";

    // 用户名
    private String username;

    // 密码
    private String password;

    // 超时时间
    private Long timeout = 10000L;
}
