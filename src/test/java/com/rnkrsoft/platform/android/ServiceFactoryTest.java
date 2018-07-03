package com.rnkrsoft.platform.android;

import com.rnkrsoft.platform.demo.domains.DemoRequest;
import com.rnkrsoft.platform.demo.domains.DemoResponse;
import com.rnkrsoft.platform.demo.service.DemoService;


/**
 * Created by woate on 2018/6/27.
 */
public class ServiceFactoryTest {

    @org.junit.Test
    public void testGet() throws Exception {
        ServiceFactory.setting("127.0.0.1", 80, "/api", "CAR_MANAGE");
        ServiceFactory.scan("com.rnkrsoft.platform.demo.service", "com.rnkrsoft.platform.protocol.service");
        DemoService demoService = ServiceFactory.get(DemoService.class);
        DemoRequest request = new DemoRequest();
        demoService.demo(request, new AsyncHandler<DemoResponse>() {
            @Override
            public void fail(String code, String desc) {
                System.out.println(code + ":" + desc);
            }

            @Override
            public void success(DemoResponse response) {
                System.out.println(response);
            }
        });
        DemoResponse response = demoService.demo(request);
    }
}