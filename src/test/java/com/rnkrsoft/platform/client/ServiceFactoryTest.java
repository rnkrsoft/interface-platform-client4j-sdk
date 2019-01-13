package com.rnkrsoft.platform.client;

import com.rnkrsoft.platform.client.configure.DefaultConfigureProvider;
import com.rnkrsoft.platform.client.configure.MockConfigureProvider;
import com.rnkrsoft.platform.client.connector.http.HttpInterfaceConnector;
import com.rnkrsoft.platform.client.log.file.FileLogProvider;
import com.rnkrsoft.platform.demo.service.HelloRequest;
import com.rnkrsoft.platform.demo.service.HelloResponse;
import com.rnkrsoft.platform.demo.service.HelloService;
import com.rnkrsoft.platform.protocol.service.FetchPublishRequest;
import com.rnkrsoft.platform.protocol.service.FetchPublishResponse;
import com.rnkrsoft.platform.protocol.service.PublishService;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by rnkrsoft.com on 2018/8/6.
 */
public class ServiceFactoryTest {

    @Test
    public void testSettingConfigure() throws Exception {
        ServiceFactory.registerConfigureProvider(new DefaultConfigureProvider());
        ServiceFactory.registerLogProvider(new FileLogProvider());
        ServiceFactory.settingConfigure(true, "gateway-configure.zxevpop.com", 8001, "configure");
        ServiceFactory.settingFallback("test-channel", false, "localhost", 80, "/api");
        ServiceFactory.settingFallback("public", false, "localhost", 80, "/api");
        ServiceFactory.getServiceConfigure().setInterfaceConnectorClass(HttpInterfaceConnector.class);
        ServiceFactory.getServiceConfigure().setAutoLocate(false);
        ServiceFactory.getServiceConfigure().setAppVersion("4.0.0");
        ServiceFactory.getServiceConfigure().setPassword("");
        ServiceFactory.getServiceConfigure().enableDebug();
        ServiceFactory.getServiceConfigure().setAsyncExecuteThreadPoolSize(2);
        ServiceFactory.getServiceConfigure().enableVerboseLog();
        ServiceFactory.addServiceClasses(HelloService.class);
        ServiceFactory.init();
        ServiceFactory.fetchRemoteMetadata(true);
        HelloService helloService = ServiceFactory.get(HelloService.class);
        HelloRequest request = new HelloRequest();
        request.setName("rnkrsoft");
        for (int i = 0; i < 1000 ; i++) {
            HelloResponse response = helloService.hello(request);
            System.out.println(response);
        }
//        Thread.sleep(60 *1000);
    }
}