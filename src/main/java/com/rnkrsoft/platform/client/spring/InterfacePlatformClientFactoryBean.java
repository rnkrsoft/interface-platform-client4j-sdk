package com.rnkrsoft.platform.client.spring;

import com.rnkrsoft.platform.client.ServiceFactory;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;


/**
 * Created by rnkrsoft.com on 2019/1/22.
 */
public class InterfacePlatformClientFactoryBean<T> implements FactoryBean<T> {
    @Setter
    Class<T> serviceClass;
    @Setter
    ServiceFactory serviceFactory;

    @Override
    public T getObject() throws Exception {
        return serviceFactory.get(serviceClass);
    }

    @Override
    public Class<?> getObjectType() {
        return serviceClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
