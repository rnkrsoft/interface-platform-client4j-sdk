package com.rnkrsoft.platform.client.demo.service;

import com.rnkrsoft.platform.client.ServiceFactory;
import com.rnkrsoft.platform.client.demo.domain.HelloRequest;
import com.rnkrsoft.platform.client.demo.domain.HelloResponse;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by rnkrsoft.com on 2019/1/21.
 */
public class HelloServiceTest {

    @Test
    public void testHello() throws Exception {
        ServiceFactory serviceFactory = new ServiceFactory();
        serviceFactory.addServiceClasses(HelloService.class);
        serviceFactory.init();
        serviceFactory.getServiceConfigure().setAutoLocate(false);
        HelloService helloService = serviceFactory.get(HelloService.class);
        HelloRequest request = new HelloRequest();
        request.setName("test");
        HelloResponse helloResponse = helloService.hello(request);
        System.out.println(helloResponse);
    }

    @Test
    public void testHelloError() throws Exception {

    }

    @Test
    public void testHelloFail() throws Exception {

    }
}