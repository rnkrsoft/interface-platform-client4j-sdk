package com.rnkrsoft.platform;

import com.rnkrsoft.platform.client.AsyncHandler;
import com.rnkrsoft.platform.client.ServiceFactory;
import com.rnkrsoft.platform.demo.domains.DemoRequest;
import com.rnkrsoft.platform.demo.domains.DemoResponse;
import com.rnkrsoft.platform.demo.service.DemoService;
import com.rnkrsoft.platform.protocol.InterfaceRspCode;
import com.zxevpop.gateway.sgcc.domains.UserRegisterRequest;
import com.zxevpop.gateway.sgcc.domains.UserRegisterResponse;
import com.zxevpop.gateway.sgcc.facade.UserFacade;

import java.util.concurrent.Future;


/**
 * Created by rnkrsoft.com on 2018/6/27.
 */
public class ServiceFactoryTest {

    @org.junit.Test
    public void testGet() throws Exception {
        ServiceFactory.setting("localhost", 8081, "/api", "car_manage");
//        ServiceFactory.ssl();
        ServiceFactory.addServiceClasses(UserFacade.class);
        ServiceFactory.scan();
        ServiceFactory.getServiceConfigure().enableDebug();
        ServiceFactory.getServiceConfigure().setAutoLocate(false);
        UserFacade demoService = null;
        try {
            demoService = ServiceFactory.get(UserFacade.class);
        }finally {
            for (String log : ServiceFactory.getServiceConfigure().getLogs()){
                System.out.println(log);
            }

        }
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        for (int i = 0; i < 200; i++) {
           Future<Boolean> future = demoService.userRegister(registerRequest, new AsyncHandler<UserRegisterResponse>() {
               @Override
               public void fail(String code, String desc, String detail) {
                   System.out.println(code + detail);
               }

               @Override
               public void success(UserRegisterResponse response) {
                   System.out.println(response);
               }
           });
        }
//        DemoResponse response = demoService.login(request);
//        System.out.println(response);
        Thread.sleep(800 *1000L);
    }
}