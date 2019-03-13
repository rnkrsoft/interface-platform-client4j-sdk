package com.rnkrsoft.platform.client.demo.service;

import com.rnkrsoft.platform.client.Location;
import com.rnkrsoft.platform.client.LocationProvider;
import com.rnkrsoft.platform.client.LocationStore;
import com.rnkrsoft.platform.client.ServiceFactory;
import com.rnkrsoft.platform.client.demo.domain.HelloRequest;
import com.rnkrsoft.platform.client.demo.domain.HelloResponse;
import org.junit.Test;

/**
 * Created by rnkrsoft.com on 2019/1/21.
 */
public class HelloServiceTest {

    @Test
    public void testHello() throws Exception {
        ServiceFactory serviceFactory = ServiceFactory.newInstance();
        serviceFactory.settingConfigure(false, "localhost", 8080, "configure");
        serviceFactory.getServiceConfigure().setUic("123");
        serviceFactory.setPassword("1234567890");
        serviceFactory.addServiceClasses(HelloService.class);
        serviceFactory.registerLocationProvider(new LocationProvider() {
            @Override
            public void locate(LocationStore locationStore) {
                locationStore.refreshLocation(new Location(1, 2));
            }
        });
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