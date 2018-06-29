package com.rnkrsoft.platform.android;

import com.zxevpop.platform.domains.QueryPlatformRequest;
import com.zxevpop.platform.domains.QueryPlatformResponse;
import com.zxevpop.platform.service.PlatformService;


/**
 * Created by rnkrsoft.com on 2018/6/27.
 */
public class ServiceFactoryTest {

    @org.junit.Test
    public void testGet() throws Exception {
        ServiceFactory.setting("127.0.0.1", 8080, "/api", "h5_user");
        ServiceFactory.init("com.zxevpop.manage.service", "com.zxevpop.platform.service", "com.rnkrsoft.platform.protocol.service");
//        ManageService manageService = ServiceFactory.get(ManageService.class);
//        LoginRequest request = new LoginRequest();
//        request.setMobilePhone("1234561");
//        request.setPassword("sss");
//        manageService.login(request);
        PlatformService platformService = ServiceFactory.get(PlatformService.class);
        QueryPlatformRequest request = new QueryPlatformRequest();
        request.setPlatformId(1);
        QueryPlatformResponse response = platformService.queryPlatform(request);
        System.out.println(response);
    }
}