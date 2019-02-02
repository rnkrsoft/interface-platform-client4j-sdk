package com.rnkrsoft.platform.client.spring;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by rnkrsoft.com on 2019/1/22.
 * 网关地址配置对象
 */
@Data
public final class GatewayAddress implements Serializable {
    /**
     * 通道号
     */
    String channel;
    /**
     * 是否安全协议HTTPS
     */
    boolean ssl;
    /**
     * 服务器地址
     */
    String host;
    /**
     * 服务器端口号
     */
    int port;
    /**
     * 上下文路径
     */
    String contextPath;
}
