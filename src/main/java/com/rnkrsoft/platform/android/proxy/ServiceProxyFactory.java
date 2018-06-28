package com.rnkrsoft.platform.android.proxy;

import com.rnkrsoft.platform.android.ServiceConfigure;

import java.lang.reflect.Proxy;

/**
 * Created by rnkrsoft.com on 2018/6/27.
 * 服务代理工厂
 */
public class ServiceProxyFactory{
    /**
     * 根据配置对象和服务类接口创建Stub实例
     * @param serviceConfigure 配置对象
     * @param serviceClass 服务类
     * @param <Service>
     * @return Stub实例
     */
    public static  <Service> Service newInstance(ServiceConfigure serviceConfigure, Class<Service> serviceClass){
        ServiceProxy serviceProxy = new ServiceProxy(serviceConfigure, serviceClass);
        return (Service) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, serviceProxy);
    }
}
