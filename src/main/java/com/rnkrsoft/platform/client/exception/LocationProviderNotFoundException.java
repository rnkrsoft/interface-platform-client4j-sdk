package com.rnkrsoft.platform.client.exception;

/**
 * Created by rnkrsoft.com on 2018/8/6.
 * 定位提供者未发现
 */
public class LocationProviderNotFoundException extends RuntimeException {
    public LocationProviderNotFoundException(String message) {
        super(message);
    }
}