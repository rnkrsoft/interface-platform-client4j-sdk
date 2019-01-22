package com.rnkrsoft.platform.protocol.service;

import com.rnkrsoft.platform.client.ServiceFactory;
import com.rnkrsoft.platform.client.proxy.ServiceProxyFactory;
import org.junit.Test;

/**
 * Created by rnkrsoft.com on 2019/1/19.
 */
public class PublishServiceTest {
    @Test
    public void test1() throws NoSuchMethodException {
        ServiceFactory serviceFactory = new ServiceFactory();
        serviceFactory.settingFallback("public", true, "gateway-public.zxevpop.com", 8001, "api");
        PublishService publishService = ServiceProxyFactory.newInstance(serviceFactory, PublishService.class);
        FetchPublishRequest request = new FetchPublishRequest();
        request.getChannels().add("user_app");
        request.getChannels().add("payment");
        request.getChannels().add("chat");
        FetchPublishResponse response = publishService.fetchPublish(request);
        System.out.println(response);
    }
}
