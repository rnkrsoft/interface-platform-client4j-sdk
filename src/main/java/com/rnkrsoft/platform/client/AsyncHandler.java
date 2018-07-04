package com.rnkrsoft.platform.client;


import com.rnkrsoft.platform.protocol.InterfaceRspCode;

/**
 * Created by rnkrsoft.com on 2018/6/28.
 * 接口异步处理器
 */
public abstract class AsyncHandler<T> {
    /**
     *  通信层执行失败
     * @param code 错误码
     * @param desc 错误描述
     * @param detail 错误详情
     */
    public void fail(String code, String desc, String detail){
        throw new RuntimeException(code + ":" + desc + ", cause:" + detail);
    }

    /**
     * 通信层执行失败
     * @param rspCode 错误码定义枚举
     */
    public void fail(InterfaceRspCode rspCode, String detail){
        fail(rspCode.getCode(), rspCode.getDesc(), detail);
    }
    /**
     * 通信层执行成功
     * @param response 应答对象
     */
   public abstract void success(T response);
}
