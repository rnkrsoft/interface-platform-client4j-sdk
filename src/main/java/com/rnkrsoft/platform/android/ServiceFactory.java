package com.rnkrsoft.platform.android;

import com.rnkrsoft.platform.android.proxy.ServiceProxyFactory;
import com.rnkrsoft.platform.android.scanner.InterfaceMetadata;
import com.rnkrsoft.platform.android.scanner.ServiceClassPathScanner;
import com.rnkrsoft.platform.protocol.domains.InterfaceDefinition;
import com.rnkrsoft.platform.protocol.domains.PublishRequest;
import com.rnkrsoft.platform.protocol.domains.PublishResponse;
import com.rnkrsoft.platform.protocol.service.PublishService;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by rnkrsoft.com on 2018/6/27.
 */
public final class ServiceFactory {
    static ServiceConfigure SERVICE_CONFIGURE = new ServiceConfigure();
    /**
     * 初始化
     */
    static AtomicBoolean INIT = new AtomicBoolean(false);

    public static final void setting(ServiceConfigure serviceConfigure) {
        if (serviceConfigure == null) {
            throw new RuntimeException("配置对象为空");
        }
        SERVICE_CONFIGURE = serviceConfigure;
    }

    /**
     * 配置服务地址
     *
     * @param host
     * @param port
     * @param contextPath
     */
    public static final void setting(String host, int port, String contextPath) {
        SERVICE_CONFIGURE.setHost(host);
        SERVICE_CONFIGURE.setPort(port);
        SERVICE_CONFIGURE.setContextPath(contextPath);
    }

    public static final void init(String... basePackages) {
        List<InterfaceMetadata> metadatas = ServiceClassPathScanner.scan(basePackages);
        for (InterfaceMetadata metadata : metadatas){
//            ServiceRegister.register(serviceClass);
        }
    }

    /**
     * 获取接口对应的 stub实例
     *
     * @param serviceClass 服务类接口
     * @param <T>
     * @return stub实例
     */
    public static final <T> T get(Class<T> serviceClass) {
        T stub = ServiceRegister.lookup(serviceClass);
        if (stub == null) {
            if (ServiceRegister.isEmpty()) {
                PublishService publishService = ServiceProxyFactory.newInstance(SERVICE_CONFIGURE, PublishService.class);
                PublishRequest request = new PublishRequest();
                request.setChannel(SERVICE_CONFIGURE.getChannel());
                PublishResponse response = publishService.publish(request);
                ServiceRegister.init(response.getInterfaces());
            }
            stub = ServiceProxyFactory.newInstance(SERVICE_CONFIGURE, serviceClass);
            ServiceRegister.register(stub);
        }
        return stub;
    }
}
