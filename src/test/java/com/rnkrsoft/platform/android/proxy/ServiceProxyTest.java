package com.rnkrsoft.platform.android.proxy;

import com.rnkrsoft.platform.android.AsyncHandler;
import com.rnkrsoft.platform.android.ServiceConfigure;
import com.rnkrsoft.platform.demo.domains.DemoRequest;
import com.rnkrsoft.platform.demo.domains.DemoResponse;
import com.rnkrsoft.platform.demo.service.DemoService;
import org.junit.Test;

import java.lang.reflect.Proxy;

import static org.junit.Assert.*;

/**
 * Created by woate on 2018/7/4.
 */
public class ServiceProxyTest {

    @Test
    public void testInvoke() throws Exception {
        ServiceConfigure serviceConfigure = new ServiceConfigure();
        Class serviceClass = DemoService.class;
        ServiceProxy serviceProxy = new ServiceProxy(serviceConfigure, serviceClass);
        DemoService demoService = (DemoService) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, serviceProxy);
        DemoRequest request = new DemoRequest();
        demoService.demo(request, new AsyncHandler<DemoResponse>() {
            @Override
            public void success(DemoResponse response) {
                System.out.println(response);
            }
        });
    }
}