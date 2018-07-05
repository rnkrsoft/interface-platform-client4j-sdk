package com.rnkrsoft.exception;

/**
 * Created by rnkrsoft.com on 2018/7/5.
 */
public class UnsupportedPlatformException extends RuntimeException{
    public UnsupportedPlatformException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedPlatformException(String message) {
        super(message);
    }
}
