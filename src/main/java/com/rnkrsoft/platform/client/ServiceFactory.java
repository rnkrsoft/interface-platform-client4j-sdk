package com.rnkrsoft.platform.client;

import com.rnkrsoft.platform.client.proxy.ServiceProxyFactory;
import com.rnkrsoft.platform.client.scanner.InterfaceMetadata;
import com.rnkrsoft.platform.client.scanner.MetadataClassPathScanner;
import com.rnkrsoft.platform.client.utils.DateUtil;
import com.rnkrsoft.platform.client.utils.MessageFormatter;
import com.rnkrsoft.platform.protocol.service.FetchPublishRequest;
import com.rnkrsoft.platform.protocol.service.FetchPublishResponse;
import com.rnkrsoft.platform.protocol.service.PublishService;

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
        SERVICE_CONFIGURE.addBasePackage(basePackages);
    }

    public static final void scan() {
        List<InterfaceMetadata> metadatas = MetadataClassPathScanner.scan(SERVICE_CONFIGURE.getBasePackages());
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
    public static final <T> T get(Class<T> serviceClass) {
        if (!ServiceRegistry.isInit()) {//如果服务未初始化，则调用发布接口获取已发布的接口信息
            SERVICE_CONFIGURE.generateSessionId();
            PublishService publishService = ServiceProxyFactory.newInstance(SERVICE_CONFIGURE, PublishService.class);
            FetchPublishRequest request = new FetchPublishRequest();
            request.setChannel(SERVICE_CONFIGURE.getChannel());
            Future<Boolean> future = publishService.fetchPublish(request, new AsyncHandler<FetchPublishResponse>() {
                @Override
                public void fail(String code, String desc, String detail) {
                    getServiceConfigure().log("{} sessionId[{}] call publishService.fetchPublish happens error!  {}:{} cause :{} ", DateUtil.getDate(), getServiceConfigure().getSessionId(), code, desc, detail);

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
        SERVICE_CONFIGURE.generateSessionId();
        if (SERVICE_CONFIGURE.isDebug()) {
            SERVICE_CONFIGURE.log("{} sessionId[{}] get '{}' stub instance ", DateUtil.getDate(), SERVICE_CONFIGURE.getSessionId(), serviceClass);
        }
        return stub;
    }
}
