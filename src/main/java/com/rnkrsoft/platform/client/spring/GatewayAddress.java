package com.rnkrsoft.platform.client.spring;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by woate on 2019/1/22.
 */
@Data
public final class GatewayAddress implements Serializable {
    String channel;
    boolean ssl;
    String host;
    int port;
    String contextPath;
}
