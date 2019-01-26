package com.rnkrsoft.platform.client.exception;

/**
 * Created by rnkrsoft.com on 2018/10/6.
 * 接口连接器错误
 */
public class InterfaceConnectorNotFoundException extends RuntimeException {
    public InterfaceConnectorNotFoundException(String message) {
        super(message);
    }
}