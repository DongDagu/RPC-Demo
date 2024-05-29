package com.example.config;


import lombok.Data;

@Data
public class RpcConfig {

    // 服务名
    private String name;

    // 版本
    private String version = "1.0";

    // 主机名
    private String serverHost = "localhost";

    // 服务端口
    private Integer serverPort = 8080;

}
