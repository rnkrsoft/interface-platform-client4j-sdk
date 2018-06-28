package com.rnkrsoft.platform.android;

/**
 * Created by rnkrsoft.com on 2018/6/27.
 * 服务配置对象
 */
public final class ServiceConfigure {
    String channel;
    String uic;
    String uid;
    String token;
    String schema = "http";
    String host = "127.0.0.1";
    int port = 80;
    String contextPath = "api";

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getUic() {
        return uic;
    }

    public void setUic(String uic) {
        this.uic = uic;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
}
