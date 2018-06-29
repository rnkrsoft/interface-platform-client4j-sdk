package com.rnkrsoft.platform.android;

import com.rnkrsoft.platform.android.proxy.ServiceProxyFactory;
import com.rnkrsoft.platform.android.scanner.InterfaceMetadata;
import com.rnkrsoft.platform.android.scanner.MetadataClassPathScanner;
import com.rnkrsoft.platform.protocol.domains.PublishRequest;
import com.rnkrsoft.platform.protocol.domains.PublishResponse;
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

    public static final void init(String... basePackages) {
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
            PublishRequest request = new PublishRequest();
            request.setChannel(SERVICE_CONFIGURE.getChannel());
            PublishResponse response = publishService.publish(request);
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
