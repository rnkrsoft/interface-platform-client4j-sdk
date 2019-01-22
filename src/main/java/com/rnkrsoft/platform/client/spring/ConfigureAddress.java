package com.rnkrsoft.platform.client.spring;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by woate on 2019/1/22.
 */
@Data
public final class ConfigureAddress implements Serializable{
    boolean ssl;
    String host;
    int port;
    String contextPath;
}
