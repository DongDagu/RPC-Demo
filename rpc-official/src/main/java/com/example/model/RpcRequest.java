package com.example.model;



import com.example.constant.RpcConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RPC 请求
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {

    // 服务名
    private String serviceName;

    // 方法名
    private String methodName;

    // 默认服务版本
    private String serviceVersion = RpcConstant.DEFAULT_VERSION;

    // 参数类型
    private Class<?>[] parameterTypes;

    // 参数列表
    private Object[] args;

}
