package com.rnkrsoft.platform;

import com.rnkrsoft.platform.client.AsyncHandler;
import com.rnkrsoft.platform.client.ServiceFactory;
import com.rnkrsoft.platform.demo.domains.DemoRequest;
import com.rnkrsoft.platform.demo.domains.DemoResponse;
import com.rnkrsoft.platform.demo.service.DemoService;


/**
 * Created by rnkrsoft.com on 2018/6/27.
 */
public class ServiceFactoryTest {

    @org.junit.Test
    public void testGet() throws Exception {
        ServiceFactory.setting("127.0.0.1", 8080, "/api", "test");
        ServiceFactory.addBasePackage("com.rnkrsoft.platform.demo.service");
        ServiceFactory.scan();
        DemoService demoService = ServiceFactory.get(DemoService.class);
        DemoRequest request = new DemoRequest();
        request.setMobilePhone("18223478223");
        request.setPassword("pengsong123.");
        AsyncHandler asyncHandler = new AsyncHandler<DemoResponse>() {
            @Override
            public void fail(String code, String desc, String detail) {
                System.out.println(Thread.currentThread() + ":--------------------->" +  code + ":" + desc);
            }

            @Override
            public void success(DemoResponse response) {
                System.out.println(Thread.currentThread() + ":" + response);
            }
        };
        for (int i = 0; i < 200; i++) {
            demoService.login(request, asyncHandler);
        }
//        DemoResponse response = demoService.login(request);
//        System.out.println(response);
        Thread.sleep(800 *1000L);
    }
}