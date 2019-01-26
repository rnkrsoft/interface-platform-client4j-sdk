package com.rnkrsoft.platform.client.exception;

/**
 * Created by rnkrsoft.com on 2018/7/5.
 * 不支持的平台异常
 */
public class UnsupportedPlatformException extends RuntimeException {
    public UnsupportedPlatformException(String message) {
        super(message);
    }
}
