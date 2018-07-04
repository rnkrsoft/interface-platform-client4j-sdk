package com.rnkrsoft.platform.client;

import com.rnkrsoft.platform.client.proxy.ServiceProxyFactory;
import com.rnkrsoft.platform.client.scanner.InterfaceMetadata;
import com.rnkrsoft.platform.client.scanner.MetadataClassPathScanner;
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
    final static ServiceConfigure SERVICE_CONFIGURE = new ServiceConfigure();

    public static ServiceConfigure getServiceConfigure() {
        return SERVICE_CONFIGURE;
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

    public static final void scan(String... basePackages) {
        List<InterfaceMetadata> metadatas = MetadataClassPathScanner.scan(basePackages);
        ServiceRegistry.initMetadatas(metadatas);
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
            PublishService publishService = ServiceProxyFactory.newInstance(SERVICE_CONFIGURE, PublishService.class);
            FetchPublishRequest request = new FetchPublishRequest();
            request.setChannel(SERVICE_CONFIGURE.getChannel());
            Future<Boolean> future = publishService.fetchPublish(request, new AsyncHandler<FetchPublishResponse>() {
                @Override
                public void success(FetchPublishResponse response) {
                    ServiceRegistry.initDefinitions(response.getInterfaces());
                }
            });
            try {
                if (!future.get()){
                    throw new IllegalArgumentException("初始化接口平台客户端失败");
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
        return stub;
    }
}
