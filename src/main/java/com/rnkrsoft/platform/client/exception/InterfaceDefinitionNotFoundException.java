package com.rnkrsoft.platform.client.exception;

/**
 * Created by rnkrsoft.com on 2019/1/17.
 * 无效接口定义异常
 */
public class InterfaceDefinitionNotFoundException extends RuntimeException {
    public InterfaceDefinitionNotFoundException(String message) {
        super(message);
    }
}
