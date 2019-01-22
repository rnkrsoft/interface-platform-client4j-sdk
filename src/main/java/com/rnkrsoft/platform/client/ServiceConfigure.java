package com.rnkrsoft.platform.client;

import com.rnkrsoft.platform.client.connector.InterfaceConnector;
import com.rnkrsoft.platform.client.connector.http.HttpInterfaceConnector;
import com.rnkrsoft.platform.client.environment.Environment;
import com.rnkrsoft.platform.client.environment.EnvironmentDetector;
import com.rnkrsoft.platform.client.logger.Logger;
import com.rnkrsoft.platform.client.logger.LoggerFactory;
import com.rnkrsoft.platform.protocol.AsyncHandler;
import com.rnkrsoft.platform.protocol.enums.EnvironmentEnum;
import com.rnkrsoft.platform.protocol.service.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by rnkrsoft.com on 2019/1/17.
 */
public final class ServiceConfigure implements LocationStore {
    static Logger log = LoggerFactory.getLogger(ServiceConfigure.class);
    final Environment environment = new EnvironmentDetector();
    /**
     * 本地配置是否启用
     */
    @Setter
    @Getter
    boolean localConfigure = true;
    /**
     * 应用版本号
     */
    @Setter
    @Getter
    String appVersion;
    /**
     * 配置服务通信模式
     */
    @Setter
    String configSchema = "http";
    /**
     * 配置服务服务器地址
     */
    @Setter
    String configHost = "localhost";
    /**
     * 配置服务服务器端口
     */
    @Setter
    int configPort = 80;
    /**
     * 配置服务上下文路径
     */
    @Setter
    String configContextPath = "config";
    /**
     * 设备厂商 例如,xiaomi,apple
     */
    @Getter
    String deviceManufacturer = environment.getDeviceManufacturer();
    /**
     * 设备型号 例如 xiaomi note, iphone 6s
     */
    @Getter
    String deviceModel = environment.getDeviceModel();
    /**
     * MAC地址 例如 44-45-53-54-00-00
     */
    @Setter
    @Getter
    String macAddress;
    /**
     * 设备类型 例如 iOS,Android,H5
     */
    @Getter
    String deviceType = environment.getDeviceType();
    /**
     * 操作系统版本 例如 iOS 8
     */
    @Getter
    String osVersion = environment.getOsVersion();
    /**
     * 用户设备识别码
     */
    @Setter
    @Getter
    String uic;
    /**
     * 用户号
     */
    @Setter
    @Getter
    String uid;
    /**
     * 会话令牌
     */
    @Setter
    @Getter
    String token;
    /**
     * 用户密码
     */
    @Setter
    @Getter
    String password;
    /**
     * 密钥向量
     */
    @Setter
    @Getter
    String keyVector = "1234567890654321";
    /**
     * 异步执行线程池大小
     */
    @Setter
    @Getter
    int asyncExecuteThreadPoolSize = Math.max(2, Math.min(Runtime.getRuntime().availableProcessors() - 1, 4));
    /**
     * HTTP链接超时时间
     */
    @Setter
    @Getter
    int httpConnectTimeoutSecond = 10;
    /**
     * HTTP读取超时时间
     */
    @Setter
    @Getter
    int httpReadTimeoutSecond = 12;
    /**
     * 是否自动定位
     */
    @Setter
    @Getter
    boolean autoLocate = false;
    /**
     * 环境
     */
    @Setter
    @Getter
    int env = EnvironmentEnum.PRO.getCode();
    /**
     * 环境描述
     */
    @Setter
    @Getter
    String envDesc = "";

    /**
     * 纬度
     */
    @Getter
    double lat;
    /**
     * 经度
     */
    @Getter
    double lng;

    final List<String> channels = new ArrayList<String>();

    /**
     * 通道地址信息
     */
    final Map<String, List<GatewayAddress>> channelAddresses = new HashMap();
    /**
     * 失败退回地址信息
     */
    final Map<String, GatewayAddress> fallbackChannelAddresses = new HashMap();
    /**
     * 接口连接器类对象
     */
    @Setter
    Class<? extends InterfaceConnector> interfaceConnectorClass = HttpInterfaceConnector.class;

    public ServiceConfigure() {

    }

    public List<String> getChannels() {
        return Collections.unmodifiableList(channels);
    }

    public void addChannel(String channel){
        this.channels.add(channel);
    }
    public void initChannels(Set<String> channels) {
        this.channels.addAll(channels);
    }

    public List<GatewayAddress> getGatewayAddresses(String channel) {
        List<GatewayAddress> addresses = channelAddresses.get(channel);
        return addresses;
    }

    /**
     * 获取失败退回配置
     *
     * @param channel 通道号
     * @return 网关地址信息
     */
    public GatewayAddress getFallbackGatewayAddresses(String channel) {
        return fallbackChannelAddresses.get(channel);
    }

    /**
     * 设置失败退回配置
     *
     * @param channel     通道号
     * @param ssl         是否安全协议
     * @param host        地址
     * @param port        端口
     * @param contextPath 上下文路径
     */
    public void settingFallback(String channel, boolean ssl, String host, int port, String contextPath) {
        GatewayAddress gatewayAddress = fallbackChannelAddresses.get(channel);
        if (gatewayAddress == null) {
            fallbackChannelAddresses.put(channel, new GatewayAddress("", ssl ? "https" : "http", host, port, contextPath));
        } else {
            gatewayAddress.setSchema(ssl ? "https" : "http");
            gatewayAddress.setHost(host);
            gatewayAddress.setPort(port);
            gatewayAddress.setContextPath(contextPath);
        }
    }

    @Override
    public void refreshLocation(Location location) {
        this.lat = location.lat;
        this.lng = location.lng;
    }
}
