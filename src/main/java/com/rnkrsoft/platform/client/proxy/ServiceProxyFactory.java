package com.rnkrsoft.platform.client.proxy;


import com.rnkrsoft.platform.client.ServiceFactory;

import java.lang.reflect.Proxy;

/**
 * Created by rnkrsoft.com on 2018/6/27.
 * 服务代理工厂
 */
public class ServiceProxyFactory {
    /**
     * 根据配置对象和服务类接口创建Stub实例
     *
     * @param serviceFactory 服务工厂
     * @param serviceClass   服务类
     * @param <Service>
     * @return Stub实例
     */
    public static <Service> Service newInstance(ServiceFactory serviceFactory, Class<Service> serviceClass) {
        ServiceProxy serviceProxy = new ServiceProxy(serviceFactory, serviceClass);
        return (Service) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, serviceProxy);
    }
}
