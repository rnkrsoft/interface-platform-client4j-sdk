package com.rnkrsoft.platform.client;

import com.rnkrsoft.config.ConfigProvider;
import com.rnkrsoft.config.ConfigProviderFactory;
import com.rnkrsoft.platform.client.async.AsyncTask;
import com.rnkrsoft.platform.client.demo.domain.HelloRequest;
import com.rnkrsoft.platform.client.demo.domain.HelloResponse;
import com.rnkrsoft.platform.client.demo.service.HelloService;
import com.rnkrsoft.platform.client.logger.LoggerFactory;
import com.rnkrsoft.platform.client.logger.LoggerLevel;
import com.rnkrsoft.platform.client.logger.file.LoggerConstant;
import com.rnkrsoft.platform.protocol.AsyncHandler;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.Future;


/**
 * Created by rnkrsoft.com on 2019/1/17.
 */
public class ServiceFactoryTest {

    static {
        ConfigProvider config = null;
        try {
            config = ConfigProviderFactory.getPropertiesInstance("logger");
        } catch (Exception e) {
            e.printStackTrace();
        }
        config.param(LoggerConstant.LOGGER_DIRECTORY, "./target/logs");
        config.param(LoggerConstant.LOGGER_PREFIX, "interface-platform");
        config.param(LoggerConstant.LOGGER_SUFFIX, "log");
        config.param(LoggerConstant.LOGGER_LEVEL, LoggerLevel.TRACE.name());
        config.param(LoggerConstant.LOGGER_SOUT, "true");
        config.init("./target", 60 * 24);
        LoggerFactory.setting(config);
    }

    @Test
    public void testSuccess() throws Exception {
        ServiceFactory serviceFactory = ServiceFactory.newInstance();
        serviceFactory.settingFallback("public", false, "localhost", 8080, "api");
        serviceFactory.settingFallback("test-channel", false, "localhost", 8080, "api");
        serviceFactory.setPassword("1234567890");
        serviceFactory.setKeyVector("1234567890654321");
        serviceFactory.addServiceClasses(HelloService.class);
        serviceFactory.registerLocationProvider(new LocationProvider() {
            @Override
            public void locate(LocationStore locationStore) {
                locationStore.refreshLocation(new Location(1, 2));
            }
        });
//        serviceFactory.init();
//        serviceFactory.init(true, new AsyncHandler() {
//            @Override
//            public void fail(String code, String desc, String detail) {
//                System.out.println(desc);
//            }
//
//            @Override
//            public void success(Object response) {
//                System.out.println(response);
//            }
//        });
        HelloService helloService = serviceFactory.get(HelloService.class);
        HelloRequest request = new HelloRequest();
        request.setName("test");
        AsyncTask future = helloService.hello(request, new AsyncHandler<HelloResponse>() {
            @Override
            public void fail(String code, String desc, String detail) {
                System.out.println(detail);
            }

            @Override
            public void success(HelloResponse response) {
                System.out.println("------------------" + response);
            }
        });
        future.get();
    }

    @Test
    public void testFailure() throws Exception {
        ServiceFactory serviceFactory = ServiceFactory.newInstance();
        serviceFactory.settingFallback("public", false, "localhost", 8080, "api");
        serviceFactory.settingFallback("test-channel", false, "localhost", 8080, "api");
        serviceFactory.setPassword("1234567890");
        serviceFactory.setKeyVector("1234567890654321");
        serviceFactory.addServiceClasses(HelloService.class);
        serviceFactory.registerLocationProvider(new LocationProvider() {
            @Override
            public void locate(LocationStore locationStore) {
                locationStore.refreshLocation(new Location(1, 2));
            }
        });
//        serviceFactory.init(true, new AsyncHandler() {
//            @Override
//            public void fail(String code, String desc, String detail) {
//                System.out.println(desc);
//            }
//
//            @Override
//            public void success(Object response) {
//                System.out.println(response);
//            }
//        });
        HelloService helloService = serviceFactory.get(HelloService.class);
        HelloRequest request = new HelloRequest();
        request.setName("test");
        AsyncTask future = helloService.hello(request, new AsyncHandler<HelloResponse>() {
            @Override
            public void fail(String code, String desc, String detail) {
                System.out.println(code + ":" + desc + "," + detail);
            }

            @Override
            public void success(HelloResponse response) {
                System.out.println(response);
            }
        });
        if (future != null) {
            future.get();
        }

    }

    @Test
    public void testSettingFallback() throws Exception {
        ServiceFactory serviceFactory = ServiceFactory.newInstance();
        Assert.assertFalse(serviceFactory.getServiceConfigure().fallbackChannelAddresses.keySet().contains("test-channel"));
        serviceFactory.settingFallback("test-channel", false, "127.0.0.1", 80, "api");
        Assert.assertTrue(serviceFactory.getServiceConfigure().fallbackChannelAddresses.keySet().contains("test-channel"));
    }

    @Test
    public void testFailure1() throws Exception {
        ServiceFactory serviceFactory = ServiceFactory.newInstance();
        serviceFactory.settingFallback("public", false, "localhost", 80, "api");
        serviceFactory.settingFallback("test-channel", false, "localhost", 80, "api");
        serviceFactory.setPassword("1234567890");
        serviceFactory.setKeyVector("1234567890654321");
        serviceFactory.addServiceClasses(HelloService.class);
        serviceFactory.init();
        HelloService helloService = serviceFactory.get(HelloService.class);
        HelloRequest request = new HelloRequest();
        request.setName("test");
        AsyncTask future = helloService.hello(request, new AsyncHandler<HelloResponse>() {
            @Override
            public void fail(String code, String desc, String detail) {
                System.out.println(code + ":" + desc + "," + detail);
            }

            @Override
            public void success(HelloResponse response) {
                System.out.println(response);
            }
        });
        future.get();

    }

    @Test
    public void testAddServiceClasses() throws Exception {

    }

    @Test
    public void testScan() throws Exception {

    }

    @Test
    public void testGet() throws Exception {

    }

    @Test
    public void testGet1() throws Exception {

    }
}