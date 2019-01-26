package com.rnkrsoft.platform.client.spring;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by rnkrsoft.com on 2019/1/22.
 */
@Data
public final class ConfigureAddress implements Serializable {
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
