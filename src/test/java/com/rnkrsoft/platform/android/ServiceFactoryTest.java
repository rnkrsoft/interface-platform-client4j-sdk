package com.rnkrsoft.platform.android;

import com.rnkrsoft.platform.demo.Callback;
import com.rnkrsoft.platform.demo.domains.DemoRequest;
import com.rnkrsoft.platform.demo.domains.DemoResponse;
import com.rnkrsoft.platform.demo.service.DemoService;
import com.zxevpop.manage.domains.LoginRequest;
import com.zxevpop.manage.service.ManageService;


/**
 * Created by rnkrsoft.com on 2018/6/27.
 */
public class ServiceFactoryTest {

    @org.junit.Test
    public void testGet() throws Exception {
        ServiceFactory.setting("127.0.0.1", 80, "/api", "CAR_MANAGE");
        ServiceFactory.init("com.zxevpop.manage.service", "com.rnkrsoft.platform.protocol.service");
        ManageService manageService = ServiceFactory.get(ManageService.class);
        LoginRequest request = new LoginRequest();
        request.setMobilePhone("1234561");
        request.setPassword("sss");
        manageService.login(request);
    }
}