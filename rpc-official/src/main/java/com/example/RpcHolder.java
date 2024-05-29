package com.example;

import com.example.config.RpcConfig;
import com.example.constant.RpcConstant;
import com.example.utils.ConfigUtils;

public enum RpcHolder {

    INSTANCE;

    public RpcConfig getRpcConfig() {
        return ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_PREFIX);
    }

}
