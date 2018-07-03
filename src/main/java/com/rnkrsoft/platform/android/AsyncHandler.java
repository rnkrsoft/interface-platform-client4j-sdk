package com.rnkrsoft.platform.android;


/**
 * Created by rnkrsoft.com on 2018/6/28.
 * 接口异步处理器
 */
public interface AsyncHandler<T> {
    /**
     *  通信层执行失败
     * @param code 错误码
     * @param desc 错误描述
     */
    void fail(String code, String desc);

    /**
     * 通信层执行成功
     * @param response 应答对象
     */
    void success(T response);
}
