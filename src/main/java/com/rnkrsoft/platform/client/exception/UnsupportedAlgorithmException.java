package com.rnkrsoft.platform.client.exception;

/**
 * Created by rnkrsoft.com on 2018/7/5.
 * 不支持的算法异常
 */
public class UnsupportedAlgorithmException extends RuntimeException {
    public UnsupportedAlgorithmException(String message) {
        super(message);
    }
}