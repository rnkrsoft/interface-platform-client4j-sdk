package com.rnkrsoft.platform.client.exception;

/**
 * Created by rnkrsoft.com on 2018/8/6.
 * 配置提供者未发现
 */
public class ConfigureProviderNotFoundException extends RuntimeException {
    public ConfigureProviderNotFoundException(String message) {
        super(message);
    }
}