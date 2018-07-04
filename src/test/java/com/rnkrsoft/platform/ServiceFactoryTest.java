package com.rnkrsoft.platform;

import com.rnkrsoft.platform.client.AsyncHandler;
import com.rnkrsoft.platform.client.ServiceFactory;
import com.rnkrsoft.platform.demo.domains.DemoRequest;
import com.rnkrsoft.platform.demo.domains.DemoResponse;
import com.rnkrsoft.platform.demo.service.DemoService;


/**
 * Created by woate on 2018/6/27.
 */
public class ServiceFactoryTest {

    @org.junit.Test
    public void testGet() throws Exception {
        ServiceFactory.setting("127.0.0.1", 8080, "/api", "test");
        ServiceFactory.scan("com.rnkrsoft.platform.demo.service", "com.rnkrsoft.platform.protocol.service");
        DemoService demoService = ServiceFactory.get(DemoService.class);
        DemoRequest request = new DemoRequest();
        AsyncHandler asyncHandler = new AsyncHandler<DemoResponse>() {
            @Override
            public void fail(String code, String desc, String detail) {
                System.out.println(Thread.currentThread() + ":" +  code);
                System.out.println(Thread.currentThread() + ":" +  "--------------------->");
                System.out.println(Thread.currentThread() + ":" +  desc);
            }

            @Override
            public void success(DemoResponse response) {
                System.out.println(Thread.currentThread() + ":" + response);
            }
        };
        for (int i = 0; i < 200; i++) {
            demoService.login(request, asyncHandler);
        }
//        DemoResponse response = demoService.demo(request);
        Thread.sleep(60 *1000L);
    }
}