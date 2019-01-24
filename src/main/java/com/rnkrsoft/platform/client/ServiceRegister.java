package com.rnkrsoft.platform.client;

import com.rnkrsoft.platform.client.logger.Logger;
import com.rnkrsoft.platform.client.logger.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rnkrsoft.com on 2019/1/17.
 * 服务注册中心
 */
public final class ServiceRegister {
    static Logger log = LoggerFactory.getLogger(ServiceRegister.class);
    /**
     * 已注册的服务缓存
     */
    private final Map<Class, Object> SERVICE_CACHES = new ConcurrentHashMap<Class, Object>();

    public ServiceRegister() {
    }

    /**
     * 注册服务存根
     *
     * @param serviceClass 服务类
     * @param stub         服务存根
     */
    public void register(Class serviceClass, Object stub) {
        SERVICE_CACHES.put(serviceClass, stub);
    }

    /**
     * 查询服务类
     *
     * @param serviceClass 服务类
     * @param <T>          服务存根
     * @return 服务存根
     */
    public <T> T lookup(Class serviceClass) {
        Object stub = SERVICE_CACHES.get(serviceClass);
        return (T) stub;
    }
}
