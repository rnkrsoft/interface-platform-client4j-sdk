package com.rnkrsoft.platform.client;


import com.rnkrsoft.com.google.gson.Gson;
import com.rnkrsoft.com.google.gson.GsonBuilder;
import com.rnkrsoft.platform.client.configure.RemoteConfigureProvider;
import com.rnkrsoft.platform.client.connector.InterfaceConnector;
import com.rnkrsoft.platform.client.exception.InitException;
import com.rnkrsoft.platform.client.exception.LocationProviderNotFoundException;
import com.rnkrsoft.platform.client.logger.Logger;
import com.rnkrsoft.platform.client.logger.LoggerFactory;
import com.rnkrsoft.platform.client.logger.LoggerLevel;
import com.rnkrsoft.platform.client.proxy.ServiceProxyFactory;
import com.rnkrsoft.platform.client.scanner.ClassScanner;
import com.rnkrsoft.platform.client.scanner.MetadataClassPathScanner;
import com.rnkrsoft.platform.protocol.ApiResponse;
import com.rnkrsoft.platform.protocol.AsyncHandler;
import com.rnkrsoft.platform.protocol.enums.InterfaceRspCode;
import com.rnkrsoft.platform.protocol.service.*;
import com.rnkrsoft.platform.protocol.utils.JavaEnvironmentDetector;
import lombok.Getter;
import lombok.Setter;

import javax.web.doc.annotation.ApidocService;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by rnkrsoft.com on 2019/1/17.
 * 服务工厂，用于获取本地服务接口包装的实例
 */
public final class ServiceFactory {
    static Logger log = LoggerFactory.getLogger(ServiceFactory.class);
    /**
     * 接口连接器
     */
    private InterfaceConnector interfaceConnector;
    /**
     * 定义信息注册中心
     */
    @Getter
    private final DefinitionRegister definitionRegister = new DefinitionRegister();
    /**
     * 元信息信息注册中心
     */
    @Getter
    private final MetadataRegister metadataRegister = new MetadataRegister();
    /**
     * 服务注册中心
     */
    @Getter
    private final ServiceRegister serviceRegister = new ServiceRegister();
    /**
     * 配置对象
     */
    @Getter
    private final ServiceConfigure serviceConfigure = new ServiceConfigure();
    /**
     * 定位信息提供者
     */
    private LocationProvider locationProvider;
    /**
     * 远程配置提供者
     */
    private ConfigureProvider configureProvider = new RemoteConfigureProvider();

    /**
     * 已注册的服务类
     */
    final List<Class> serviceClasses = new ArrayList<Class>();

    final Gson gson = new GsonBuilder().create();

    final AtomicBoolean init = new AtomicBoolean(false);

    @Setter
    long fetchConfigureIntervalSecond = 0L;

    @Setter
    long fetchMetadataIntervalSecond = 0L;

    final ScheduledExecutorService scheduleExecutor = Executors.newScheduledThreadPool(2);

    /**
     * 工厂类，不能实例化
     */
    private ServiceFactory() {
    }

    public boolean isInit() {
        return init.get();
    }

    /**
     * 设置配置中心
     *
     * @param ssl         是否启用HTTPS
     * @param host        主机地址
     * @param port        端口号
     * @param contextPath 上下文路径
     */
    public final void settingConfigure(boolean ssl, String host, int port, String contextPath) {
        log.info("set configure setting, {}://{}:{}/{}", ssl ? "https" : "http", host, port, contextPath);
        serviceConfigure.configSchema = ssl ? "https" : "http";
        serviceConfigure.configHost = host;
        serviceConfigure.configPort = port;
        serviceConfigure.configContextPath = contextPath;
    }

    /**
     * 设置失败后退回配置
     *
     * @param channel     通道号
     * @param ssl         是否启用HTTPS
     * @param host        主机地址
     * @param port        端口号
     * @param contextPath 上下文路径
     */
    public final void settingFallback(String channel, boolean ssl, String host, int port, String contextPath) {
        log.info("set fallback setting, {} --> {}://{}:{}/{}", channel, ssl ? "https" : "http", host, port, contextPath);
        serviceConfigure.settingFallback(channel, ssl, host, port, contextPath);
    }

    /**
     * 设置客户端固定密码
     *
     * @param password 密码
     */
    public final void setPassword(String password) {
        serviceConfigure.setPassword(password);
    }

    /**
     * 设置客户端秘钥向量
     *
     * @param keyVector 向量
     */
    public final void setKeyVector(String keyVector) {
        serviceConfigure.setKeyVector(keyVector);
    }

    /**
     * 设置APP版本号
     *
     * @param appVersion APP版本号
     */
    public final void setAppVersion(String appVersion) {
        serviceConfigure.setAppVersion(appVersion);
    }

    /**
     * 注册位置提供者
     *
     * @param locationProvider 位置提供者
     */
    public void registerLocationProvider(LocationProvider locationProvider) {
        this.locationProvider = locationProvider;
    }

    /**
     * 注册远程配置提供者
     *
     * @param configureProvider 配置提供者
     */
    public void registerConfigureProvider(ConfigureProvider configureProvider) {
        this.configureProvider = configureProvider;
    }

    boolean fetchRemoteConfigure(boolean silent, AsyncHandler asyncHandler) {
        if (configureProvider == null) {
            log.warn("未配置远程配置, 启用本地配置");
            LoggerFactory.level(LoggerLevel.TRACE);
            serviceConfigure.setLocalConfigure(true);
            serviceConfigure.channelAddresses.clear();
            for (String channel : serviceConfigure.getChannels()) {
                serviceConfigure.channelAddresses.clear();
                //如果回退配置不存在这个通道，则不处理
                if (serviceConfigure.fallbackChannelAddresses.containsKey(channel)) {
                    serviceConfigure.channelAddresses.put(channel, Arrays.asList(serviceConfigure.fallbackChannelAddresses.get(channel)));
                }
            }
            return true;
        } else {
            if (serviceConfigure.isAutoLocate()) {
                refreshLocation();
            }
            Configure configure = configureProvider.load(serviceConfigure.configSchema, serviceConfigure.configHost, serviceConfigure.configPort, serviceConfigure.configContextPath, serviceConfigure.getChannels(), serviceConfigure.getUic(), serviceConfigure.getDeviceType(), serviceConfigure.getAppVersion(), serviceConfigure.getLat(), serviceConfigure.getLng());
            if (configure == null) {
                log.warn("远程配置初始化失败, 启用本地配置");
                serviceConfigure.setLocalConfigure(true);
                serviceConfigure.channelAddresses.clear();
                for (String channel : serviceConfigure.getChannels()) {
                    if (!serviceConfigure.fallbackChannelAddresses.containsKey(channel)) {
                        log.error("远程配置初始化失败，并且通道'{}'本地配置无效，请检查配置！", channel);
                        if (!silent) {
                            throw new InitException("远程配置初始化失败，并且通道'" + channel + "'本地配置无效，请检查配置！");
                        } else {
                            asyncHandler.fail(InterfaceRspCode.INTERFACE_FALLBACK_GATEWAY_IS_NOT_CONFIG, "远程配置初始化失败，并且通道'" + channel + "'本地配置无效，请检查配置！");
                            return false;
                        }
                    }
                    GatewayAddress gatewayAddress = serviceConfigure.getFallbackGatewayAddresses(channel);
                    if (gatewayAddress.getSchema() == null || gatewayAddress.getHost() == null || gatewayAddress.getPort() == 0 || gatewayAddress.getContextPath() == null) {
                        if (!silent) {
                            throw new InitException("远程配置初始化失败，并且通道'" + channel + "'本地配置并未配置参数值，请检查配置！");
                        } else {
                            asyncHandler.fail(InterfaceRspCode.INTERFACE_FALLBACK_GATEWAY_IS_NOT_CONFIG, "远程配置初始化失败，并且通道'" + channel + "'本地配置并未配置参数值，请检查配置！");
                            return false;
                        }
                    }
                }
                return true;
            } else {
                log.debug("远程配置初始化成功, 启用远程配置");
                if (configure.isVerboseLog()) {
                    LoggerFactory.level(LoggerLevel.TRACE);
                } else if (configure.isDebug()) {
                    LoggerFactory.level(LoggerLevel.DEBUG);
                } else {
                    LoggerFactory.level(LoggerLevel.INFO);
                }
                serviceConfigure.setAutoLocate(configure.isAutoLocate());
                serviceConfigure.setKeyVector(configure.getKeyVector());
                serviceConfigure.setHttpConnectTimeoutSecond(configure.getHttpConnectTimeoutSecond());
                serviceConfigure.setHttpReadTimeoutSecond(configure.getHttpReadTimeoutSecond());
                serviceConfigure.setAutoLocate(configure.isAutoLocate());
                serviceConfigure.setAsyncExecuteThreadPoolSize(configure.getAsyncExecuteThreadPoolSize());
                serviceConfigure.setEnv(configure.getEnv());
                serviceConfigure.setEnvDesc(configure.getEnvDesc());
                //不进行自动定位时，使用模拟定位数据
                if (!serviceConfigure.isAutoLocate()) {
                    serviceConfigure.refreshLocation(new Location(Double.valueOf(configure.getMockLng()), Double.valueOf(configure.getMockLat())));
                }
                //重设异步线程池
//                AsyncTask.setAsyncExecuteThreadPoolSize(configure.getAsyncExecuteThreadPoolSize());
                List<GatewayChannel> channels = configure.getChannels();
                log.debug("设置通道网关地址");
                serviceConfigure.channelAddresses.clear();
                for (GatewayChannel gatewayChannel : channels) {
                    serviceConfigure.channelAddresses.put(gatewayChannel.getChannel(), gatewayChannel.getGatewayAddresses());
                }
                log.debug("初始化线程池");
                return true;
            }
        }
    }

    /**
     * 进行初始化,将注册的服务类与接口信息进行绑定
     *
     * @return 是否执行失败成功
     */
    public synchronized final boolean init() {
        return init(false, null);
    }

    /**
     * 进行初始化,将注册的服务类与接口信息进行绑定
     */
    public synchronized final boolean init(final boolean silent, AsyncHandler asyncHandler) {
        if (silent && asyncHandler == null) {
            throw new InitException("静默模式下，必须传入AsyncHandler实例!");
        }
        Map<String, Set<InterfaceMetadata>> metadataMap = MetadataClassPathScanner.scan(serviceClasses);
        serviceConfigure.initChannels(metadataMap.keySet());
        for (Set<InterfaceMetadata> interfaceMetadataSet : metadataMap.values()) {
            for (InterfaceMetadata metadata : interfaceMetadataSet) {
                log.debug("register {}.{}-->{}:{}:{}", metadata.getInterfaceClass(), metadata.getInterfaceMethod(), metadata.getChannel(), metadata.getTxNo(), metadata.getVersion());
                metadataRegister.register(metadata);
            }
        }
        if (!fetchRemoteConfigure(silent, asyncHandler)) {
            return false;
        }
        if (fetchConfigureIntervalSecond > 0) {
            initScheduleFetchConfigure();
        }
        if (fetchMetadataIntervalSecond > 0) {
            initScheduleFetchMetadata();
        }
        PublishService publishService = ServiceProxyFactory.newInstance(this, PublishService.class);
        FetchPublishRequest request = new FetchPublishRequest();
        request.getChannels().addAll(serviceConfigure.getChannels());
        Future<ApiResponse> future = publishService.fetchPublish(request, new AsyncHandler<FetchPublishResponse>() {
            @Override
            public void fail(String code, String desc, String detail) {
                log.debug("call publishService.fetchPublish happens error!  {}:{} cause :{} ", code, desc, detail);
            }

            @Override
            public void success(FetchPublishResponse response) {
                definitionRegister.clear();
                for (InterfaceChannel interfaceChannel : response.getChannels()) {
                    definitionRegister.register(interfaceChannel);
                }
                log.debug("finish fetch remote metadata...");
            }
        });
        ApiResponse result = null;
        try {
            result = future.get(serviceConfigure.getHttpReadTimeoutSecond() * 2, TimeUnit.SECONDS); //取得结果，同时设置超时执行时间为5秒。同样可以用future.get()，不设置执行超时时间取得结果
        } catch (Exception e) {
            log.error("获取元信息发生错误!");
            if (!silent) {
                throw new InitException("获取元信息发生错误!");
            } else {
                return false;
            }
        }
        if (result == null) {
            log.error("获取发布信息失败!");
            if (!silent) {
                throw new InitException("获取发布信息失败!");
            } else {
                return false;
            }
        }
        if (InterfaceRspCode.valueOfCode(result.getCode()) != InterfaceRspCode.SUCCESS) {
            String code = result.getCode();
            String desc = result.getDesc();
            if (JavaEnvironmentDetector.isAndroid()) {
                if (InterfaceRspCode.TIMESTAMP_ILLEGAL.getCode().equals(code)) {
                    desc = "手机" + result.getDesc();
                }
            }
            log.error("{}:{}", code, desc);
            if (!silent) {
                throw new InitException(desc);
            } else {
                asyncHandler.fail(InterfaceRspCode.FAIL.getCode(), desc, desc);
                return false;
            }
        } else {
            FetchPublishResponse response = gson.fromJson(result.getData(), FetchPublishResponse.class);
            if (response.isSuccess()) {
                init.set(true);
                if (asyncHandler != null) {
                    asyncHandler.success(response);
                }
                return true;
            } else {
                if (!silent) {
                    throw new InitException("获取接口元信息失败！");
                } else {
                    asyncHandler.fail(InterfaceRspCode.FETCH_INTERFACE_METADATA_IS_FAILURE, "获取接口元信息失败！");
                    return false;
                }
            }
        }
    }

    /**
     * 增加服务类接口
     *
     * @param serviceClasses 服务类接口数组
     */
    public final void addServiceClasses(Class... serviceClasses) {
        if (isInit()) {
            throw new InitException("已经初始化不允许添加服务");
        }
        for (Class serviceClass : serviceClasses) {
            this.serviceClasses.add(serviceClass);
            log.debug("add service class {}", serviceClass);
        }
    }

    /**
     * 扫描指定包路径下的服务接口
     *
     * @param basePackages 包路径数组
     */
    public final void scan(String... basePackages) {
        scan(true, basePackages);
    }

    /**
     * 扫描指定包路径下的服务接口
     *
     * @param subPackage   是否扫描子包
     * @param basePackages 包路径数组
     */
    public final void scan(boolean subPackage, String... basePackages) {
        if (isInit()) {
            throw new InitException("已经初始化不允许添加服务");
        }
        ClassScanner scanner = new ClassScanner(this.getClass().getClassLoader(), subPackage);
        for (String basePackage : basePackages) {
            scanner.scan(basePackage, new ClassScanner.AnnotatedWithFilter(ApidocService.class));
        }
        serviceClasses.addAll(scanner.getClasses());
    }

    /**
     * 将服务接口包装为服务实例，在发生系统异常时回调异步处理器
     *
     * @param serviceClass 服务类接口
     * @param asyncHandler 异步处理器
     * @param <T>          服务实例
     * @return 服务实例
     */
    public synchronized final <T> T get(Class<T> serviceClass, final AsyncHandler<Boolean> asyncHandler) {
        T stub = get(serviceClass);
        if (stub == null) {
            log.error("stub '{}' is not definition!", serviceClass);
            asyncHandler.fail(InterfaceRspCode.INTERFACE_IS_ILLEGAL, "获取服务失败");
        }
        return stub;
    }

    /**
     * 将服务接口包装为服务实例，在发生系统异常时抛出运行时异常
     *
     * @param serviceClass 服务类接口
     * @param <T>          服务实例
     * @return 服务实例
     */
    public synchronized final <T> T get(Class<T> serviceClass) {
        T stub = serviceRegister.lookup(serviceClass);
        if (stub == null) {
            stub = ServiceProxyFactory.newInstance(this, serviceClass);
            serviceRegister.register(serviceClass, stub);
        }
        log.debug("get '{}' stub instance ", serviceClass);
        return stub;
    }

    /**
     * 获取接口连接器实例
     *
     * @return 连接器实例
     * @throws Exception 异常
     */
    public InterfaceConnector getInterfaceConnector() {
        if (interfaceConnector == null) {
            try {
                Constructor constructor = serviceConfigure.interfaceConnectorClass.getConstructor(ServiceFactory.class);
                interfaceConnector = (InterfaceConnector) constructor.newInstance(this);
            } catch (Exception e) {

            }
        }
        log.debug("get Interface Connector {}", serviceConfigure.interfaceConnectorClass);
        return interfaceConnector;
    }

    /**
     * 调用定位信息提供者进行定位
     */
    public void refreshLocation() {
        if (locationProvider == null) {
            throw new LocationProviderNotFoundException("location provider is not found!");
        }
        locationProvider.locate(serviceConfigure);
    }

    class FetchConfigureTask implements Runnable {
        @Override
        public void run() {
            //调用拉取远程配置
            try {
                fetchRemoteConfigure(false, null);
            } catch (Exception e) {
                log.error("fetch Remote Configure happens error!", e);
            }
        }
    }

    class FetchMetadataTask implements Runnable {
        @Override
        public void run() {
            try {
                PublishService publishService = ServiceProxyFactory.newInstance(ServiceFactory.this, PublishService.class);
                FetchPublishRequest request = new FetchPublishRequest();
                request.getChannels().addAll(serviceConfigure.getChannels());
                publishService.fetchPublish(request, new AsyncHandler<FetchPublishResponse>() {
                    @Override
                    public void fail(String code, String desc, String detail) {
                        log.debug("call publishService.fetchPublish happens error!  {}:{} cause :{} ", code, desc, detail);
                    }

                    @Override
                    public void success(FetchPublishResponse response) {
                        definitionRegister.clear();
                        for (InterfaceChannel interfaceChannel : response.getChannels()) {
                            definitionRegister.register(interfaceChannel);
                        }
                        log.debug("finish fetch remote metadata...");
                    }
                });
            } catch (Exception e) {
                log.error("fetch metadata happens error!", e);
            }
        }
    }

    /**
     * 初始化拉取远程配置定时任务
     */
    void initScheduleFetchConfigure() {
        scheduleExecutor.scheduleWithFixedDelay(new FetchConfigureTask(), fetchConfigureIntervalSecond, fetchConfigureIntervalSecond, TimeUnit.SECONDS);
    }

    /**
     * 初始化拉取接口元信息
     */
    void initScheduleFetchMetadata() {
        scheduleExecutor.scheduleWithFixedDelay(new FetchMetadataTask(), fetchMetadataIntervalSecond, fetchMetadataIntervalSecond, TimeUnit.SECONDS);
    }

    //-----------------------------------服务工厂单例对象-----------------------------------------------
    private static final ServiceFactory INSTANCE = new ServiceFactory();

    /**
     * 新建一个服务工厂对象
     * @return 服务工厂对象
     */
    public final static ServiceFactory newInstance(){
        return new ServiceFactory();
    }
    /**
     * 获取服务工厂的单例对象
     *
     * @return 服务工厂对象
     */
    public final static ServiceFactory getInstance() {
        return INSTANCE;
    }
}
