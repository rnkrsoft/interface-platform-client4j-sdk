package com.rnkrsoft.platform.client;

import com.rnkrsoft.exception.UnsupportedPlatformException;
import com.rnkrsoft.platform.client.proxy.ServiceProxyFactory;
import com.rnkrsoft.platform.client.scanner.InterfaceMetadata;
import com.rnkrsoft.platform.client.scanner.MetadataClassPathScanner;
import com.rnkrsoft.platform.client.utils.DateUtil;
import com.rnkrsoft.platform.protocol.service.FetchPublishRequest;
import com.rnkrsoft.platform.protocol.service.FetchPublishResponse;
import com.rnkrsoft.platform.protocol.service.PublishService;
import com.rnkrsoft.utils.JavaEnvironmentDetector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by rnkrsoft.com on 2018/6/27.
 */
public final class ServiceFactory {
    private final static ServiceConfigure SERVICE_CONFIGURE = new ServiceConfigure();


    public static ServiceConfigure getServiceConfigure() {
        return SERVICE_CONFIGURE;
    }

    /**
     * 注册位置提供者
     * @param locationProvider 位置提供者
     */
    public static void registerLocationProvider(LocationProvider locationProvider){
        SERVICE_CONFIGURE.setLocationProvider(locationProvider);
    }
    /**
     * 配置服务地址
     *
     * @param host
     * @param port
     * @param contextPath
     */
    public static final void setting(String host, int port, String contextPath, String channel) {
        SERVICE_CONFIGURE.setHost(host);
        SERVICE_CONFIGURE.setPort(port);
        SERVICE_CONFIGURE.setContextPath(contextPath);
        SERVICE_CONFIGURE.setChannel(channel);
    }

    public static final void ssl(){
        SERVICE_CONFIGURE.setSchema("https");
    }
    public static final void http(){
        SERVICE_CONFIGURE.setSchema("http");
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
        if (JavaEnvironmentDetector.isAndroid()){
            throw new UnsupportedPlatformException("not supported android!");
        }
        SERVICE_CONFIGURE.addBasePackage(basePackages);
    }

    public static final void addServiceClasses(Class ... serviceClass){
        SERVICE_CONFIGURE.addServiceClasses(serviceClass);
    }

    public static final void scan() {
        List<InterfaceMetadata> metadatas = new ArrayList();
        if (!JavaEnvironmentDetector.isAndroid()){
            List<InterfaceMetadata> metadatas0 = MetadataClassPathScanner.scan(SERVICE_CONFIGURE.getBasePackages());
            metadatas.addAll(metadatas0);
        }
        List<InterfaceMetadata> metadatas1 = MetadataClassPathScanner.scanClass(SERVICE_CONFIGURE.getServiceClasses());
        metadatas.addAll(metadatas1);
        ServiceRegistry.initMetadatas(metadatas);
    }

    /**
     * 刷新地理位置坐标
     *
     * @param lng 经度
     * @param lat 纬度
     */
    public static final void refreshLocation(double lng, double lat) {
        SERVICE_CONFIGURE.refreshLocation(new LocationProvider.Location(lng, lat));
    }

    /**
     * 获取接口对应的 stub实例
     *
     * @param serviceClass 服务类接口
     * @param <T>
     * @return stub实例
     */
    public static synchronized final <T> T get(Class<T> serviceClass) {
        if (!ServiceRegistry.isInit()) {//如果服务未初始化，则调用发布接口获取已发布的接口信息
            SERVICE_CONFIGURE.generateSessionId();
            PublishService publishService = ServiceProxyFactory.newInstance(SERVICE_CONFIGURE, PublishService.class);
            FetchPublishRequest request = new FetchPublishRequest();
            request.setChannel(SERVICE_CONFIGURE.getChannel());
            Future<Boolean> future = publishService.fetchPublish(request, new AsyncHandler<FetchPublishResponse>() {
                @Override
                public void fail(String code, String desc, String detail) {
                    getServiceConfigure().log("call publishService.fetchPublish happens error!  {}:{} cause :{} ", code, desc, detail);

                }

                @Override
                public void success(FetchPublishResponse response) {
                    ServiceRegistry.initDefinitions(response.getInterfaces());
                }
            });
            try {
                if (!future.get()) {
                    throw new IllegalArgumentException("与接口平台通信失败,请检查网络或者配置是否正确");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            } catch (ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        }
        T stub = ServiceRegistry.lookup(serviceClass);
        if (stub == null) {
            stub = ServiceProxyFactory.newInstance(SERVICE_CONFIGURE, serviceClass);
            ServiceRegistry.register(stub);
        }
        if (SERVICE_CONFIGURE.isDebug()) {
            SERVICE_CONFIGURE.log("get '{}' stub instance ", serviceClass);
        }
        return stub;
    }
}
