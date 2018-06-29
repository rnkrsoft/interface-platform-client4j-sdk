package com.rnkrsoft.platform.android;

import com.rnkrsoft.platform.android.proxy.ServiceProxyFactory;
import com.rnkrsoft.platform.android.scanner.InterfaceMetadata;
import com.rnkrsoft.platform.android.scanner.MetadataClassPathScanner;
import com.rnkrsoft.platform.protocol.service.FetchPublishRequest;
import com.rnkrsoft.platform.protocol.service.FetchPublishResponse;
import com.rnkrsoft.platform.protocol.service.PublishService;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by rnkrsoft.com on 2018/6/27.
 */
public final class ServiceFactory {
   final static ServiceConfigure SERVICE_CONFIGURE = new ServiceConfigure();
    /**
     * 初始化
     */
    static AtomicBoolean INIT = new AtomicBoolean(false);

    public static ServiceConfigure getServiceConfigure(){
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

    public static final void scan(String... basePackages) {
        List<InterfaceMetadata> metadatas = MetadataClassPathScanner.scan(basePackages);
        ServiceRegister.initMetadatas(metadatas);
    }

    /**
     * 获取接口对应的 stub实例
     *
     * @param serviceClass 服务类接口
     * @param <T>
     * @return stub实例
     */
    public static final <T> T get(Class<T> serviceClass) {
        if (ServiceRegister.isEmpty()) {
            PublishService publishService = ServiceProxyFactory.newInstance(SERVICE_CONFIGURE, PublishService.class);
            FetchPublishRequest request = new FetchPublishRequest();
            request.setChannel(SERVICE_CONFIGURE.getChannel());
            FetchPublishResponse response = publishService.fetchPublish(request);
            ServiceRegister.initDefinitions(response.getInterfaces());
        }
        T stub = ServiceRegister.lookup(serviceClass);
        if (stub == null) {
            stub = ServiceProxyFactory.newInstance(SERVICE_CONFIGURE, serviceClass);
            ServiceRegister.register(stub);
        }
        return stub;
    }
}
