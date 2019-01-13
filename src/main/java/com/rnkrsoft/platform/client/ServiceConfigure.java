/**
 * RNKRSOFT OPEN SOURCE SOFTWARE LICENSE TERMS ver.1
 * - 氡氪网络科技(重庆)有限公司 开源软件许可条款(版本1)
 * 氡氪网络科技(重庆)有限公司 以下简称Rnkrsoft。
 * 这些许可条款是 Rnkrsoft Corporation（或您所在地的其中一个关联公司）与您之间达成的协议。
 * 请阅读本条款。本条款适用于所有Rnkrsoft的开源软件项目，任何个人或企业禁止以下行为：
 * .禁止基于删除开源代码所附带的本协议内容、
 * .以非Rnkrsoft的名义发布Rnkrsoft开源代码或者基于Rnkrsoft开源源代码的二次开发代码到任何公共仓库,
 * 除非上述条款附带有其他条款。如果确实附带其他条款，则附加条款应适用。
 * <p/>
 * 使用该软件，即表示您接受这些条款。如果您不接受这些条款，请不要使用该软件。
 * 如下所述，安装或使用该软件也表示您同意在验证、自动下载和安装某些更新期间传输某些标准计算机信息以便获取基于 Internet 的服务。
 * <p/>
 * 如果您遵守这些许可条款，将拥有以下权利。
 * 1.阅读源代码和文档
 * 如果您是个人用户，则可以在任何个人设备上阅读、分析、研究Rnkrsoft开源源代码。
 * 如果您经营一家企业，则禁止在任何设备上阅读Rnkrsoft开源源代码,禁止分析、禁止研究Rnkrsoft开源源代码。
 * 2.编译源代码
 * 如果您是个人用户，可以对Rnkrsoft开源源代码以及修改后产生的源代码进行编译操作，编译产生的文件依然受本协议约束。
 * 如果您经营一家企业，不可以对Rnkrsoft开源源代码以及修改后产生的源代码进行编译操作。
 * 3.二次开发拓展功能
 * 如果您是个人用户，可以基于Rnkrsoft开源源代码进行二次开发，修改产生的元代码同样受本协议约束。
 * 如果您经营一家企业，不可以对Rnkrsoft开源源代码进行任何二次开发，但是可以通过联系Rnkrsoft进行商业授予权进行修改源代码。
 * 完整协议。本协议以及开源源代码附加协议，共同构成了Rnkrsoft开源软件的完整协议。
 * <p/>
 * 4.免责声明
 * 该软件按“原样”授予许可。 使用本文档的风险由您自己承担。Rnkrsoft 不提供任何明示的担保、保证或条件。
 * 5.版权声明
 * 本协议所对应的软件为 Rnkrsoft 所拥有的自主知识产权，如果基于本软件进行二次开发，在不改变本软件的任何组成部分的情况下的而二次开发源代码所属版权为贵公司所有。
 */
package com.rnkrsoft.platform.client;

import com.rnkrsoft.platform.client.connector.InterfaceConnector;
import com.rnkrsoft.platform.client.connector.http.HttpInterfaceConnector;
import com.rnkrsoft.platform.client.exception.InitException;
import com.rnkrsoft.platform.client.exception.LocationProviderNotFoundException;
import com.rnkrsoft.platform.client.exception.UnsupportedPlatformException;
import com.rnkrsoft.platform.client.log.LogProvider;
import com.rnkrsoft.platform.client.log.LogTrace;
import com.rnkrsoft.platform.protocol.enums.EnvironmentEnum;
import com.rnkrsoft.platform.protocol.service.GatewayAddress;
import com.rnkrsoft.platform.protocol.service.GatewayChannel;
import com.rnkrsoft.platform.protocol.service.PublishService;
import com.rnkrsoft.platform.protocol.utils.JavaEnvironmentDetector;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by rnkrsoft.com on 2018/6/27.
 * 服务配置对象
 */
public final class ServiceConfigure extends LogTrace implements LocationStore {
    static ThreadPoolExecutor THREAD_POOL_EXECUTOR = null;
    static InterfaceConnector INTERFACE_CONNECTOR = null;
    /**
     * 应用版本号
     */
    String appVersion;
    /**
     * 本地配置是否启用
     */
    boolean localConfigure = true;
    /**
     * 位置提供者
     */
    LocationProvider locationProvider;
    /**
     * 配置信息提供者
     */
    ConfigureProvider configureProvider;
    /**
     * 接口连接器类对象
     */
    Class<? extends InterfaceConnector> interfaceConnectorClass = HttpInterfaceConnector.class;
    /**
     * 配置服务通信模式
     */
    String configSchema = "http";
    /**
     * 配置服务服务器地址
     */
    String configHost = "localhost";
    /**
     * 配置服务服务器端口
     */
    int configPort = 80;
    /**
     * 配置服务上下文路径
     */
    String configContextPath = "config";
    /**
     * 扫描包
     */
    final Set<String> basePackages = new HashSet(Arrays.asList("com.rnkrsoft.platform.protocol.service"));
    /**
     * 服务类集合
     */
    final Set<Class> serviceClasses = new HashSet(Arrays.asList(PublishService.class));
    /**
     * 渠道号
     */
    final List<String> channels = new ArrayList();
    /**
     * 设备厂商 例如,xiaomi,apple
     */
    String deviceManufacturer;
    /**
     * 设备型号 例如 xiaomi note, iphone 6s
     */
    String deviceModel;
    /**
     * MAC地址 例如 44-45-53-54-00-00
     */
    String macAddress;
    /**
     * 设备类型 例如 iOS,Android,H5
     */
    String deviceType = JavaEnvironmentDetector.getOsName();
    /**
     * 操作系统版本 例如 iOS 8
     */
    String osVersion = JavaEnvironmentDetector.getOsVersion();
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
     * 用户密码
     */
    String password;
    /**
     * 密钥向量
     */
    String keyVector = "1234567890654321";
    /**
     * 会话号
     */
    final static ThreadLocal<String> SESSION = new ThreadLocal();
    /**
     * 通道地址信息
     */
    final Map<String, List<GatewayAddress>> channelAddresses = new HashMap<String, List<GatewayAddress>>();
    /**
     * 失败退回地址信息
     */
    final Map<String, GatewayAddress> fallbackChannelAddresses = new HashMap<String, GatewayAddress>();
    /**
     * 异步执行线程池大小
     */
    int asyncExecuteThreadPoolSize = 5;
    /**
     * HTTP链接超时时间
     */
    int httpConnectTimeoutSecond = 3;
    /**
     * HTTP读取超时时间
     */
    int httpReadTimeoutSecond = 5;
    /**
     * 当前定位位置
     */
    Location location = new Location();

    /**
     * 是否自动定位
     */
    boolean autoLocate = true;
    /**
     * 环境
     */
    int env = EnvironmentEnum.PRO.getCode();
    /**
     * 环境描述
     */
    String envDesc = "";

    AtomicBoolean init = new AtomicBoolean(false);


    public void init() {
        synchronized (ServiceConfigure.class) {
            if (isInit()) {
                throw new InitException("重复初始化");
            }
            if (this.logProvider != null){
                this.logProvider.init();
            }
            if (configureProvider != null) {
                Configure configure = null;
                try {
                    if (locationProvider != null){
                        locationProvider.locate(this);
                    }
                    configure = configureProvider.load(configSchema, configHost, configPort, configContextPath, channels, uic, getDeviceType(), appVersion, getLat(), getLng());
                    if (configure == null) {
                        localConfigure = true;
                        for (String channel : channels) {
                            if (!fallbackChannelAddresses.containsKey(channel)) {
                                error("远程配置初始化失败，并且通道'{}'本地配置无效，请检查配置！", channel);
                                throw new InitException("远程配置初始化失败，并且通道'" + channel + "'本地配置无效，请检查配置！");
                            }
                            GatewayAddress gatewayAddress = fallbackChannelAddresses.get(channel);
                            if (gatewayAddress.getSchema() == null || gatewayAddress.getHost() == null || gatewayAddress.getPort() == 0 || gatewayAddress.getContextPath() == null) {
                                throw new InitException("远程配置初始化失败，并且通道'" + channel + "'本地配置并未配置参数值，请检查配置！");
                            }
                        }
                        enableDebug();
                        enableVerboseLog();
                        error("远程配置初始化失败, 启用本地配置");
                        this.channelAddresses.clear();
                        for (String channel : channels) {
                            //如果回退配置不存在这个通道，则不处理
                            if (fallbackChannelAddresses.containsKey(channel)) {
                                this.channelAddresses.put(channel, Arrays.asList(fallbackChannelAddresses.get(channel)));
                            }
                        }
                    } else {
                        debug("远程配置初始化成功, 启用远程配置");
                        //远程配置初始化成功
                        keyVector = configure.keyVector;
                        httpReadTimeoutSecond = configure.httpReadTimeoutSecond;
                        httpConnectTimeoutSecond = configure.httpConnectTimeoutSecond;
                        asyncExecuteThreadPoolSize = configure.asyncExecuteThreadPoolSize;
                        List<GatewayChannel> channels = configure.getChannels();
                        for (GatewayChannel gatewayChannel : channels) {
                            this.channelAddresses.put(gatewayChannel.getChannel(), gatewayChannel.getGatewayAddresses());
                        }
                        if (configure.debug) {
                            enableDebug();
                            debug("远程配置初始化成功, 启用debug日志");
                        } else {
                            disableDebug();
                            debug("远程配置初始化成功, 禁用debug日志");
                        }
                        if (configure.verboseLog) {
                            enableVerboseLog();
                            debug("远程配置初始化成功, 启用啰嗦日志");
                        } else {
                            disableVerboseLog();
                            debug("远程配置初始化成功, 禁用啰嗦日志");
                        }
                        if (configure.autoLocate) {
                            autoLocate = true;
                            debug("远程配置初始化成功, 启用自动定位");
                        } else {
                            autoLocate = false;
                            debug("远程配置初始化成功, 禁止自动定位");
                        }
                        env = configure.getEnv();
                        envDesc = configure.getEnvDesc();
                        debug("keyVector({})", keyVector);
                        debug("httpReadTimeoutSecond({})", httpReadTimeoutSecond);
                        debug("httpConnectTimeoutSecond({})", httpConnectTimeoutSecond);
                        debug("asyncExecuteThreadPoolSize({})", asyncExecuteThreadPoolSize);
                        debug("autoLocate({})", autoLocate);
                        debug("env({}:{})", env, configure.getEnvDesc());
                    }
                    //初始化线池
                    initThreadPool();
                    init.set(true);
                } catch (Exception e) {
                    error("获取配置信息发生异常,cause:{}", e);
                    return;
                }
            } else {
                initFallbackConfig();
            }
        }
    }

    void initFallbackConfig() {
        this.channelAddresses.clear();
        for (String channel : channels) {
            //如果回退配置不存在这个通道，则不处理
            if (fallbackChannelAddresses.containsKey(channel)) {
                this.channelAddresses.put(channel, Arrays.asList(fallbackChannelAddresses.get(channel)));
            }
        }
        initThreadPool();
        init.set(true);
    }

    void initThreadPool() {
        //双重锁定进行线程池初始化
        if (asyncExecuteThreadPoolSize > 0) {
            synchronized (ServiceConfigure.class) {
                if (asyncExecuteThreadPoolSize > 0) {
                    debug("初始化线程池");
                    if (THREAD_POOL_EXECUTOR != null) {
                        THREAD_POOL_EXECUTOR.shutdownNow();
                    }
                    THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
                            asyncExecuteThreadPoolSize,
                            asyncExecuteThreadPoolSize,
                            5,
                            TimeUnit.SECONDS,
                            new ArrayBlockingQueue<Runnable>(200),
                            new ThreadPoolExecutor.DiscardOldestPolicy());
                }
            }
        }
    }

    /**
     * 设置失败退回配置
     *
     * @param channel     通道
     * @param schema      模式
     * @param host        地址
     * @param port        端口
     * @param contextPath 上下文路径
     */
    public void settingFallback(String channel, String schema, String host, int port, String contextPath) {
        GatewayAddress gatewayAddress = fallbackChannelAddresses.get(channel);
        if (gatewayAddress == null) {
            fallbackChannelAddresses.put(channel, new GatewayAddress("", schema, host, port, contextPath));
        } else {
            gatewayAddress.setSchema(schema);
            gatewayAddress.setHost(host);
            gatewayAddress.setPort(port);
            gatewayAddress.setContextPath(contextPath);
        }
    }

    /**
     * 增加基础包路径
     *
     * @param basePackages 基础包路径
     */
    public void addBasePackage(String... basePackages) {
        if (isInit()) {
            debug("已经初始化，不允许添加包路径扫描");
            return;
        }
        if (JavaEnvironmentDetector.isAndroid()) {
            throw new UnsupportedPlatformException("not supported android!");
        }
        if (basePackages.length == 0) {
            throw new NullPointerException("basePackage is null!");
        }
        this.basePackages.addAll(Arrays.asList(basePackages));
    }

    public void addServiceClasses(Class... serviceClass) {
        if (isInit()) {
            debug("已经初始化，不允许添加服务类");
            return;
        }
        if (serviceClass == null) {
            throw new NullPointerException("serviceClass is null!");
        }
        if (serviceClass.length == 0) {
            throw new NullPointerException("serviceClass is length 0!");
        }
        this.serviceClasses.addAll(Arrays.asList(serviceClass));
    }

    public void setLocationProvider(LocationProvider locationProvider) {
        this.locationProvider = locationProvider;
    }


    public List<GatewayAddress> getGatewayAddresses(String channel) {
        List<GatewayAddress> addresses = channelAddresses.get(channel);
        return addresses;
    }

    public GatewayAddress getFallbackGatewayAddresses(String channel) {
        return fallbackChannelAddresses.get(channel);
    }

    /**
     * 获取基础包路径
     *
     * @return 基础包路径
     */
    public Set<String> getBasePackages() {
        if (JavaEnvironmentDetector.isAndroid()) {
            throw new UnsupportedPlatformException("not supported android!");
        }
        return Collections.unmodifiableSet(basePackages);
    }

    public void setInterfaceConnectorClass(Class<? extends InterfaceConnector> interfaceConnectorClass) {
        this.interfaceConnectorClass = interfaceConnectorClass;
    }

    public InterfaceConnector getInterfaceConnector() {
        if (INTERFACE_CONNECTOR == null) {
            try {
                Constructor constructor = interfaceConnectorClass.getConstructor(ServiceConfigure.class);
                INTERFACE_CONNECTOR = (InterfaceConnector) constructor.newInstance(this);
            } catch (Exception e) {
                error("获取接口连接器失败", e);
            }
        }
        return INTERFACE_CONNECTOR;
    }

    public ThreadPoolExecutor getThreadPool() {
        return THREAD_POOL_EXECUTOR;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Set<Class> getServiceClasses() {
        return Collections.unmodifiableSet(serviceClasses);
    }

    public String getDeviceManufacturer() {
        return deviceManufacturer;
    }

    public void setDeviceManufacturer(String deviceManufacturer) {
        this.deviceManufacturer = deviceManufacturer;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getOsVersion() {
        return osVersion;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKeyVector() {
        return keyVector;
    }

    public void setKeyVector(String keyVector) {
        this.keyVector = keyVector;
    }

    public List<String> getChannels() {
        return channels;
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

    public boolean isAutoLocate() {
        return autoLocate;
    }

    public void setAutoLocate(boolean autoLocate) {
        this.autoLocate = autoLocate;
    }

    /**
     * 生成会话号
     */
    public void generateSessionId() {
        SESSION.set(UUID.randomUUID().toString());
    }

    public void setSessionId(String sessionId) {
        SESSION.set(sessionId);
    }

    /**
     * 获取当前会话号
     *
     * @return 会话号
     */
    public String getSessionId() {
        String session = SESSION.get();
        return session == null ? "" : session;
    }

    /**
     * 使用位置提供者刷新定位信息
     */
    public void refreshLocation() {
        if (locationProvider != null) {
            locationProvider.locate(this);
        } else {
            throw new LocationProviderNotFoundException("位置提供者未注册");
        }
    }

    @Override
    public void refreshLocation(Location location) {
        this.location.setLat(location.getLat());
        this.location.setLng(location.getLng());
    }

    public double getLng() {
        return location.getLng();
    }

    public double getLat() {
        return location.getLat();
    }

    public Location getLocation() {
        return location;
    }


    public boolean isInit() {
        return init.get();
    }

    public void setConfigSchema(String configSchema) {
        this.configSchema = configSchema;
    }

    public void setConfigHost(String configHost) {
        this.configHost = configHost;
    }

    public void setConfigPort(int configPort) {
        this.configPort = configPort;
    }

    public void setConfigContextPath(String configContextPath) {
        this.configContextPath = configContextPath;
    }

    public void setEnv(EnvironmentEnum env) {
        this.env = env.getCode();
    }

    public int getEnv() {
        return env;
    }

    public String getEnvDesc() {
        return envDesc;
    }

    public void setEnvDesc(String envDesc) {
        this.envDesc = envDesc;
    }

    public ConfigureProvider getConfigureProvider() {
        return configureProvider;
    }

    public void setConfigureProvider(ConfigureProvider configureProvider) {
        this.configureProvider = configureProvider;
    }
    public void setLocalConfigure(boolean localConfigure) {
        this.localConfigure = localConfigure;
    }


}
