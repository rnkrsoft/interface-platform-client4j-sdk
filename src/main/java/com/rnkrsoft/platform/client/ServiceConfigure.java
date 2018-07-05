package com.rnkrsoft.platform.client;

import com.rnkrsoft.platform.client.utils.MessageFormatter;

import java.util.*;

/**
 * Created by rnkrsoft.com on 2018/6/27.
 * 服务配置对象
 */
public final class ServiceConfigure implements LocationStore{
    LocationProvider locationProvider;
    /**
     * 扫描包
     */
    final Set<String> basePackages = new HashSet<String>(Arrays.asList("com.rnkrsoft.platform.protocol.service"));
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
     * 会话号
     */
    String sessionId;
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
    /**
     * 当前定位位置
     */
    LocationProvider.Location location = new LocationProvider.Location();
    /**
     * 日志
     */
    final List<String> logs = Collections.synchronizedList(new ArrayList());
    /**
     * 调试模式
     */
    boolean debug = false;
    /**
     * 是否自动定位
     */
    boolean autoLocate = true;

    /**
     * 增加基础包路径
     * @param basePackages 基础包路径
     */
    public void addBasePackage(String...basePackages){
        if (basePackages.length == 0){
            throw new NullPointerException("basePackage is null!");
        }
        this.basePackages.addAll(Arrays.asList(basePackages));
    }

    public void setLocationProvider(LocationProvider locationProvider) {
        this.locationProvider = locationProvider;
    }



    /**
     * 获取基础包路径
     * @return 基础包路径
     */
    public Set<String> getBasePackages() {
        return Collections.unmodifiableSet(basePackages);
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

    /**
     * 获取日志记录
     * @return
     */
    public List<String> getLogs() {
        return Collections.unmodifiableList(logs);
    }

    /**
     * 记录日志
     * @param format 日志格式
     * @param args 参数
     */
    public void log(String format, Object ... args){
        String log = MessageFormatter.format(format, args);
        logs.add(log);
    }

    /**
     * 是否为调试模式
     * @return 调试模式
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * 开启调试模式
     */
    public void enableDebug(){
        this.debug = true;
    }

    /**
     * 关闭调试模式
     */
    public synchronized void disableDebug(){
        this.debug = false;
        this.logs.clear();
    }

    public boolean isAutoLocate() {
        return autoLocate;
    }

    public void setAutoLocate(boolean autoLocate) {
        this.autoLocate = autoLocate;
    }

    /**
     * 生成会话号
     */
    public void generateSessionId(){
        this.sessionId = UUID.randomUUID().toString();
    }

    /**
     * 获取当前会话号
     * @return 会话号
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * 使用位置提供者刷新定位信息
     */
    public void refreshLocation(){
        if (locationProvider != null) {
            locationProvider.locate(this);
        }else{
            throw new NullPointerException("位置提供者未注册");
        }
    }

    @Override
    public void refreshLocation(LocationProvider.Location location) {
        this.location.setLat(location.getLat());
        this.location.setLng(location.getLng());
    }
    public double getLng(){
        return location.getLng();
    }

    public double getLat(){
        return location.getLat();
    }
    public LocationProvider.Location getLocation() {
        return location;
    }
}
