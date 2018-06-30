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
        ServiceFactory.setting("127.0.0.1", 8080, "/api", "car_manage");
        ServiceFactory.scan("com.rnkrsoft.platform.demo.service", "com.rnkrsoft.platform.protocol.service");
        DemoService demoService = ServiceFactory.get(DemoService.class);
        DemoRequest request = new DemoRequest();
        request.setName("xxx");
//        demoService.demo(request, new Callback<DemoResponse>() {
//            @Override
//            public void fail(String code, String desc) {
//
//            }
//
//            @Override
//            public void success(DemoResponse response) {
//
//            }
//        });
        DemoResponse response = demoService.demo(request);
    }
}