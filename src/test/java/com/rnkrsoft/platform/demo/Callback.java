package com.rnkrsoft.platform.demo;


/**
 * Created by woate on 2018/6/28.
 */
public interface Callback<T> {
    void fail(String code, String desc);
    void success(T response);
}
