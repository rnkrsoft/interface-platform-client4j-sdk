package com.rnkrsoft.platform.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by rnkrsoft.com on 2018/6/27.
 * 服务配置对象
 */
public final class ServiceConfigure {
    /**
     * 扫描包
     */
    final List<String> basePackages = new ArrayList(Arrays.asList("com.rnkrsoft.platform.protocol.service"));
    /**
     * 渠道号
     */
    String channel;
    /**
     * 用户设备识别码
     */
    String uic;
    /**
     * 用户号
     */
    String uid;
    /**
     * 会话令牌
     */
    String token;
    /**
     * 通信模式
     */
    String schema = "http";
    /**
     * 服务器地址
     */
    String host = "127.0.0.1";
    /**
     * 服务器端口
     */
    int port = 80;
    /**
     * 上下文路径
     */
    String contextPath = "api";
    /**
     * 异步执行线程池大小
     */
    int asyncExecuteThreadPoolSize = 5;
    /**
     * HTTP链接超时时间
     */
    int httpConnectTimeoutSecond = 30;
    /**
     * HTTP读取超时时间
     */
    int httpReadTimeoutSecond = 30;

    public void addBasePackage(String...basePackages){
        if (basePackages.length == 0){
            throw new NullPointerException("basePackage is null!");
        }
        this.basePackages.addAll(Arrays.asList(basePackages));
    }
    public List<String> getBasePackages() {
        return Collections.unmodifiableList(basePackages);
    }

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

    public int getAsyncExecuteThreadPoolSize() {
        return asyncExecuteThreadPoolSize;
    }

    public void setAsyncExecuteThreadPoolSize(int asyncExecuteThreadPoolSize) {
        this.asyncExecuteThreadPoolSize = asyncExecuteThreadPoolSize;
    }

    public int getHttpReadTimeoutSecond() {
        return httpReadTimeoutSecond;
    }

    public void setHttpReadTimeoutSecond(int httpReadTimeoutSecond) {
        this.httpReadTimeoutSecond = httpReadTimeoutSecond;
    }

    public int getHttpConnectTimeoutSecond() {
        return httpConnectTimeoutSecond;
    }

    public void setHttpConnectTimeoutSecond(int httpConnectTimeoutSecond) {
        this.httpConnectTimeoutSecond = httpConnectTimeoutSecond;
    }
}
