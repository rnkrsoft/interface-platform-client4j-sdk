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

import com.rnkrsoft.platform.client.exception.InitException;
import com.rnkrsoft.platform.client.exception.UnsupportedPlatformException;
import com.rnkrsoft.platform.client.log.LogProvider;
import com.rnkrsoft.platform.client.proxy.ServiceProxyFactory;
import com.rnkrsoft.platform.client.scanner.InterfaceMetadata;
import com.rnkrsoft.platform.client.scanner.MetadataClassPathScanner;
import com.rnkrsoft.platform.protocol.ApiResponse;
import com.rnkrsoft.platform.protocol.AsyncHandler;
import com.rnkrsoft.platform.protocol.enums.InterfaceRspCode;
import com.rnkrsoft.platform.protocol.service.FetchPublishRequest;
import com.rnkrsoft.platform.protocol.service.FetchPublishResponse;
import com.rnkrsoft.platform.protocol.service.PublishService;
import com.rnkrsoft.platform.protocol.utils.JavaEnvironmentDetector;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by rnkrsoft.com on 2018/6/27.
 */
public final class ServiceFactory {
    private final static ServiceConfigure SERVICE_CONFIGURE = new ServiceConfigure();
    /**
     * 定时器
     */
    static Timer TIMER;


    public static ServiceConfigure getServiceConfigure() {
        return SERVICE_CONFIGURE;
    }

    static {
        String msg = "  _____           _                    __                           _____    _           _      __                                 _____   _____    _  __\n" +
                " |_   _|         | |                  / _|                         |  __ \\  | |         | |    / _|                               / ____| |  __ \\  | |/ /\n" +
                "   | |    _ __   | |_    ___   _ __  | |_    __ _    ___    ___    | |__) | | |   __ _  | |_  | |_    ___    _ __   _ __ ___     | (___   | |  | | | ' / \n" +
                "   | |   | '_ \\  | __|  / _ \\ | '__| |  _|  / _` |  / __|  / _ \\   |  ___/  | |  / _` | | __| |  _|  / _ \\  | '__| | '_ ` _ \\     \\___ \\  | |  | | |  <  \n" +
                "  _| |_  | | | | | |_  |  __/ | |    | |   | (_| | | (__  |  __/   | |      | | | (_| | | |_  | |   | (_) | | |    | | | | | |    ____) | | |__| | | . \\ \n" +
                " |_____| |_| |_|  \\__|  \\___| |_|    |_|    \\__,_|  \\___|  \\___|   |_|      |_|  \\__,_|  \\__| |_|    \\___/  |_|    |_| |_| |_|   |_____/  |_____/  |_|\\_\\";
        System.out.println(msg);
    }

    static class FetchInterfaceMetadataTask extends TimerTask {
        @Override
        public void run() {
            fetchRemoteMetadata(true);
        }
    }

    /**
     * 注册位置提供者
     *
     * @param locationProvider 位置提供者
     */
    public static void registerLocationProvider(LocationProvider locationProvider) {
        SERVICE_CONFIGURE.setLocationProvider(locationProvider);
    }

    public static void registerConfigureProvider(ConfigureProvider configureProvider) {
        SERVICE_CONFIGURE.setConfigureProvider(configureProvider);
    }

    public static void registerLogProvider(LogProvider logProvider) {
        SERVICE_CONFIGURE.setLogProvider(logProvider);
    }

    /**
     * 获取配置网关失败退回配置
     *
     * @param channel     通道名
     * @param ssl         是否安全通信
     * @param host        主机地址
     * @param port        端口号
     * @param contextPath 上下文，默认api
     */
    public static final void settingFallback(String channel, boolean ssl, String host, int port, String contextPath) {
        if (contextPath == null) {
            contextPath = "api";
        }
        SERVICE_CONFIGURE.settingFallback(channel, ssl ? "https" : "http", host, port, (contextPath.startsWith("/") ? contextPath.substring(1) : contextPath));
    }

    /**
     * 设置服务配置信息
     *
     * @param ssl         是否安全通信
     * @param host        主机地址
     * @param port        端口号
     * @param contextPath 上下文，默认configure
     */
    public static final void settingConfigure(boolean ssl, String host, int port, String contextPath) {
        if (contextPath == null) {
            contextPath = "configure";
        }
        SERVICE_CONFIGURE.setConfigSchema(ssl ? "https" : "http");
        SERVICE_CONFIGURE.setConfigHost(host);
        SERVICE_CONFIGURE.setConfigPort(port);
        SERVICE_CONFIGURE.setConfigContextPath(contextPath.startsWith("/") ? contextPath.substring(1) : contextPath);
        SERVICE_CONFIGURE.setLocalConfigure(false);
    }
    /**
     * 初始化，调用配置提供者信息获取地址
     */
    public static synchronized final void init() {
        scan();
        SERVICE_CONFIGURE.init();
        if (TIMER != null) {
            TIMER.cancel();
            TIMER = null;
        }
    }

    /**
     * 初始化，调用配置提供者信息获取地址
     * @param fetchIntervalSeconds 拉取接口信息频率
     */
    public static synchronized final void init(int fetchIntervalSeconds) {
        init();
        boolean result = fetchRemoteMetadata(true);
        if (result){
            TIMER = new Timer("fetchInterfaceMetadataTask", true);
            if (fetchIntervalSeconds < 1){
                fetchIntervalSeconds = 60;
            }
            TIMER.schedule(new FetchInterfaceMetadataTask(), fetchIntervalSeconds * 1000, fetchIntervalSeconds * 1000);
        }
    }

    /**
     * 设置异步执行线程池大小
     *
     * @param size 异步执行线程池大小
     */
    public static final void setAsyncExecuteThreadPoolSize(int size) {
        SERVICE_CONFIGURE.setAsyncExecuteThreadPoolSize(size);
    }

    /**
     * 设置HTTP套接字链接超时时间，单位秒
     *
     * @param httpConnectTimeoutSecond HTTP套接字链接超时时间
     */
    public static final void setHttpConnectTimeoutSecond(int httpConnectTimeoutSecond) {
        SERVICE_CONFIGURE.setHttpConnectTimeoutSecond(httpConnectTimeoutSecond);
    }

    /**
     * 设置HTTP读取超时时间，单位秒
     *
     * @param httpReadTimeoutSecond HTTP读取超时时间
     */
    public static final void setHttpReadTimeoutSecond(int httpReadTimeoutSecond) {
        SERVICE_CONFIGURE.setHttpReadTimeoutSecond(httpReadTimeoutSecond);
    }

    public static final void addBasePackage(String... basePackages) {
        if (JavaEnvironmentDetector.isAndroid()) {
            throw new UnsupportedPlatformException("not supported android!");
        }
        SERVICE_CONFIGURE.addBasePackage(basePackages);
    }

    public static final void addServiceClasses(Class... serviceClass) {
        SERVICE_CONFIGURE.addServiceClasses(serviceClass);
    }

    public static final void scan() {
        Map<String, Set<InterfaceMetadata>> metadatas = new HashMap();
        if (!JavaEnvironmentDetector.isAndroid()) {
            Map<String, Set<InterfaceMetadata>> metadatas0 = MetadataClassPathScanner.scan(SERVICE_CONFIGURE.getBasePackages());
            metadatas.putAll(metadatas0);
        }
        Map<String, Set<InterfaceMetadata>> metadatas1 = MetadataClassPathScanner.scanClass(SERVICE_CONFIGURE.getServiceClasses());
        for (String channel : metadatas1.keySet()) {
            Set<InterfaceMetadata> metadatas0_ = metadatas.get(channel);
            if (metadatas0_ == null) {
                metadatas0_ = new HashSet();
                metadatas.put(channel, metadatas0_);
            }
            Set<InterfaceMetadata> metadatas1_ = metadatas1.get(channel);
            metadatas0_.addAll(metadatas1_);
        }
        ServiceRegistry.initMetadataSet(metadatas);
        List<String> channels = new ArrayList();
        for (String channel : metadatas.keySet()) {
            channels.add(channel);
        }
        SERVICE_CONFIGURE.getChannels().addAll(channels);
    }

    /**
     * 用于服务器端情况下手工拉去接口信息
     *
     * @param silent 是否为静默模式
     */
    public static synchronized boolean fetchRemoteMetadata(final boolean silent) {
        if (SERVICE_CONFIGURE.isDebug()) {
            SERVICE_CONFIGURE.debug("begin to fetch remote metadata...");
        }
        PublishService publishService = ServiceProxyFactory.newInstance(SERVICE_CONFIGURE, PublishService.class);
        FetchPublishRequest request = new FetchPublishRequest();
        request.getChannels().addAll(SERVICE_CONFIGURE.getChannels());
        Future<ApiResponse> future = publishService.fetchPublish(request, new AsyncHandler<FetchPublishResponse>() {
            @Override
            public void fail(String code, String desc, String detail) {
                getServiceConfigure().debug("call publishService.fetchPublish happens error!  {}:{} cause :{} ", code, desc, detail);
            }

            @Override
            public void success(FetchPublishResponse response) {
                ServiceRegistry.initChannels(response.getChannels());
                if (SERVICE_CONFIGURE.isDebug()) {
                    SERVICE_CONFIGURE.debug("finish fetch remote metadata...");
                }
            }
        });
        ApiResponse result = null;
        try {
            result = future.get(SERVICE_CONFIGURE.getHttpReadTimeoutSecond() * 2, TimeUnit.SECONDS); //取得结果，同时设置超时执行时间为5秒。同样可以用future.get()，不设置执行超时时间取得结果
        } catch (Exception e) {
            SERVICE_CONFIGURE.error("执行获取接口元信息发生错误");
            if (!silent) {
                throw new InitException("执行获取接口元信息发生错误");
            } else {
                return false;
            }
        }
        if (result == null) {
            SERVICE_CONFIGURE.error("获取发布的接口信息失败!");
            if (!silent) {
                throw new InitException("获取发布的接口信息失败");
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
            SERVICE_CONFIGURE.error("{}:{}", code, desc);
            if (!silent) {
                throw new InitException("获取发布的接口信息发生错误!");
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public static synchronized boolean fetchRemoteMetadata(final AsyncHandler<Boolean> asyncHandler) {
        if (SERVICE_CONFIGURE.isDebug()) {
            SERVICE_CONFIGURE.debug("begin to fetch remote metadata...");
        }
        PublishService publishService = ServiceProxyFactory.newInstance(SERVICE_CONFIGURE, PublishService.class);
        FetchPublishRequest request = new FetchPublishRequest();
        request.getChannels().addAll(SERVICE_CONFIGURE.getChannels());
        Future<ApiResponse> future = publishService.fetchPublish(request, new AsyncHandler<FetchPublishResponse>() {
            @Override
            public void fail(String code, String desc, String detail) {
                getServiceConfigure().debug("call publishService.fetchPublish happens error!  {}:{} cause :{} ", code, desc, detail);
            }

            @Override
            public void success(FetchPublishResponse response) {
                ServiceRegistry.initChannels(response.getChannels());
                if (SERVICE_CONFIGURE.isDebug()) {
                    SERVICE_CONFIGURE.debug("finish fetch remote metadata...");
                }
            }
        });
        ApiResponse result = null;
        try {
            result = future.get(SERVICE_CONFIGURE.getHttpReadTimeoutSecond() * 2, TimeUnit.SECONDS); //取得结果，同时设置超时执行时间为5秒。同样可以用future.get()，不设置执行超时时间取得结果
        } catch (Exception e) {
            asyncHandler.fail(InterfaceRspCode.FAIL, "执行获取接口元信息发生错误");
            return false;
        }
        if (result == null) {
            SERVICE_CONFIGURE.error("获取发布的接口信息失败!");
            asyncHandler.fail(InterfaceRspCode.INVALID_COMMUNICATION_MESSAGE, "获取发布的接口信息失败!");
            return false;
        }
        if (InterfaceRspCode.valueOfCode(result.getCode()) != InterfaceRspCode.SUCCESS) {
            String code = result.getCode();
            String desc = result.getDesc();
            if (JavaEnvironmentDetector.isAndroid()) {
                if (InterfaceRspCode.TIMESTAMP_ILLEGAL.getCode().equals(code)) {
                    desc = "手机" + result.getDesc();
                }
            }
            SERVICE_CONFIGURE.error("{}:{}", code, desc);
            asyncHandler.fail(code, desc, "获取发布的接口信息失败!");
            return false;
        } else {
            asyncHandler.success(true);
            return true;
        }
    }

    /**
     * 刷新地理位置坐标
     *
     * @param lng 经度
     * @param lat 纬度
     */
    public static final void refreshLocation(double lng, double lat) {
        SERVICE_CONFIGURE.refreshLocation(new Location(lng, lat));
    }

    /**
     * 获取接口对应的 stub实例，用于安卓设备上的使用
     *
     * @param serviceClass 服务类接口
     * @param asyncHandler 异步处理器
     * @param <T>
     * @return stub实例
     */
    public static synchronized final <T> T get(Class<T> serviceClass, final AsyncHandler<Boolean> asyncHandler) {
        if (asyncHandler == null) {
            return get(serviceClass);
        }
        if (!SERVICE_CONFIGURE.isInit()) {
            SERVICE_CONFIGURE.error("ServiceFactory 未进行初始化");
            init();
            boolean reulst = fetchRemoteMetadata(asyncHandler);
            if (!reulst){
                return null;
            }
        }
        T stub = ServiceRegistry.lookup(serviceClass, asyncHandler);
        if (stub == null) {
            stub = ServiceProxyFactory.newInstance(SERVICE_CONFIGURE, serviceClass);
            ServiceRegistry.register(stub);
            return stub;
        }
        if (SERVICE_CONFIGURE.isDebug()) {
            SERVICE_CONFIGURE.debug("get '{}' stub instance ", serviceClass);
        }
        return stub;
    }

    /**
     * 获取接口对应的 stub实例
     *
     * @param serviceClass 服务类接口
     * @param <T>
     * @return stub实例
     */
    public static synchronized final <T> T get(Class<T> serviceClass) {
        if (!SERVICE_CONFIGURE.isInit()) {
            SERVICE_CONFIGURE.error("ServiceFactory 未进行初始化");
            throw new InitException("ServiceFactory 未进行初始化");
        }
        T stub = ServiceRegistry.lookup(serviceClass);
        if (stub == null) {
            stub = ServiceProxyFactory.newInstance(SERVICE_CONFIGURE, serviceClass);
            ServiceRegistry.register(stub);
        }
        if (SERVICE_CONFIGURE.isDebug()) {
            SERVICE_CONFIGURE.debug("get '{}' stub instance ", serviceClass);
        }
        return stub;
    }
}
