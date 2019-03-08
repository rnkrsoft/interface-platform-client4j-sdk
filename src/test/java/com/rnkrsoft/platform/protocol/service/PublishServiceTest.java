package com.rnkrsoft.platform.protocol.service;

import com.rnkrsoft.platform.client.ServiceFactory;
import com.rnkrsoft.platform.client.proxy.ServiceProxyFactory;
import com.rnkrsoft.platform.protocol.AsyncHandler;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

/**
 * Created by rnkrsoft.com on 2019/1/19.
 */
public class PublishServiceTest {
    @Test
    public void test1() throws NoSuchMethodException, ExecutionException, InterruptedException {
        ServiceFactory serviceFactory =ServiceFactory.newInstance();

        serviceFactory.settingFallback("public", true, "localhost", 8001, "api");
        PublishService publishService = ServiceProxyFactory.newInstance(serviceFactory, PublishService.class);
        FetchPublishRequest request = new FetchPublishRequest();
        request.getChannels().add("user_app");
        request.getChannels().add("payment");
        request.getChannels().add("chat");
//        FetchPublishResponse response = publishService.fetchPublish(request);
//        System.out.println(response);
        try {
            publishService.fetchPublish(request, new AsyncHandler<FetchPublishResponse>() {
                @Override
                public void fail(String code, String desc, String detail) {

                }

                @Override
                public void success(FetchPublishResponse response) {

                }
            }).get();
        }catch (Exception e){

        }
        try {
            publishService.fetchPublish(request, new AsyncHandler<FetchPublishResponse>() {
                @Override
                public void fail(String code, String desc, String detail) {

                }

                @Override
                public void success(FetchPublishResponse response) {

                }
            }).get();
        }catch (Exception e){

        }
    }
}
