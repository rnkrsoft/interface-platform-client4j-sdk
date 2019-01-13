package com.rnkrsoft.platform.demo.service;

import com.rnkrsoft.platform.client.*;
import com.rnkrsoft.platform.client.configure.DefaultConfigureProvider;
import com.rnkrsoft.platform.client.connector.http.HttpInterfaceConnector;
import com.rnkrsoft.platform.client.log.Log;
import com.rnkrsoft.platform.client.log.LogPersistenceFormat;
import com.rnkrsoft.platform.client.log.LogProvider;
import com.rnkrsoft.platform.client.log.file.FileLogProvider;
import com.rnkrsoft.platform.protocol.AsyncHandler;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rnkrsoft.com on 2018/10/8.
 */
public class HelloServiceTest {

    @Test
    public void testHello() throws Exception {
        System.out.println(System.getProperties());
        System.out.println(System.getProperty("os.arch"));
        //注册远程配置提供者
//        ServiceFactory.registerConfigureProvider(new DefaultConfigureProvider());
        ServiceFactory.registerLogProvider(new FileLogProvider("./target", "test", "log"));
        //注册位置信息提供者
//        ServiceFactory.registerLocationProvider(new LocationProvider() {
//            int lng = 0;
//            int lat = 0;
//
//            @Override
//            public void locate(LocationStore locationStore) {
//                locationStore.refreshLocation(new Location(lng++, lat++));
//            }
//        });
        //设置远程配置服务器信息
//        ServiceFactory.settingConfigure(true, "gateway-configure.zxevpop.com", 8001, "/configure");
        //设置接口连接器实现
        ServiceFactory.getServiceConfigure().setInterfaceConnectorClass(HttpInterfaceConnector.class);
        //设置远程配置获取失败后的退回接口服务器信息
        ServiceFactory.settingFallback("test-channel", false, "localhost", 80, "/api");
        ServiceFactory.settingFallback("public", false, "localhost", 80, "/api");
        //是否自动获取定位信息
        ServiceFactory.getServiceConfigure().setAutoLocate(false);
        ServiceFactory.getServiceConfigure().setDeviceManufacturer("huawei");
        ServiceFactory.getServiceConfigure().setDeviceModel("honer6");
        ServiceFactory.getServiceConfigure().setMacAddress("44-45-53-54-00-00");
        //用户标识
        ServiceFactory.getServiceConfigure().setUid("sssss");
        ServiceFactory.getServiceConfigure().setUic("2542563b-a153-48af-84d4-d40542c8bc3b");
        //用户版本号
        ServiceFactory.getServiceConfigure().setAppVersion("4.0.0");
        //如果不使用TOKEN作为密码时的固定密码
        ServiceFactory.getServiceConfigure().setPassword("1234567890123456");
        //加密时或者解密时的秘钥向量
        ServiceFactory.getServiceConfigure().setKeyVector("1234567890654321");
        //启动调试信息，一般不启用
        ServiceFactory.getServiceConfigure().enableDebug();
        //启用啰嗦日志
        ServiceFactory.getServiceConfigure().enableVerboseLog();
        //增加需要调用的服务
        ServiceFactory.addServiceClasses(HelloService.class);
        //初始化服务
        ServiceFactory.init(0);
        //获取服务实现类
        HelloService helloService = ServiceFactory.get(HelloService.class, new AsyncHandler<Boolean>() {
            @Override
            public void fail(String code, String desc, String detail) {
                System.out.println(detail);
            }

            @Override
            public void success(Boolean response) {
                System.out.println(response);
            }
        });
        HelloRequest request = new HelloRequest();
        request.setName("rnkrsoft");
        for (int i = 0; i < 1000; i++) {
            HelloResponse response = helloService.hello(request);
            System.out.println(response);
        }
//        for (int i = 0; i < 100; i++) {
//            helloService.hello(request, new AsyncHandler<HelloResponse>() {
//                @Override
//                public void fail(String code, String desc, String detail) {
//                        System.out.println(detail);
//                }
//
//                @Override
//                public void success(HelloResponse response) {
//                    System.out.println(response);
//                }
//            });
//            try {
//                helloService.helloFail(request);
//            }catch (Exception e){
//
//            }
//        }
//        helloService.helloError(request);

        Thread.sleep(600 * 1000);

    }
}