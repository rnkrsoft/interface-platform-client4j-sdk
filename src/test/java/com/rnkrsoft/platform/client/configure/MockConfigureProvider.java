package com.rnkrsoft.platform.client.configure;

import com.rnkrsoft.platform.client.Configure;
import com.rnkrsoft.platform.client.ConfigureProvider;
import com.rnkrsoft.platform.protocol.enums.EnvironmentEnum;
import com.rnkrsoft.platform.protocol.service.GatewayAddress;
import com.rnkrsoft.platform.protocol.service.GatewayChannel;

import java.util.List;

/**
 * Created by rnkrsoft.com on 2018/10/6.
 */
public class MockConfigureProvider implements ConfigureProvider{
    @Override
    public Configure load(String schema, String host, int port, String contextPath, List<String> channels, String uic, String deviceType, String appVersion, double lat, double lng) {
        Configure configure = new Configure();
        configure.setAsyncExecuteThreadPoolSize(10);
        configure.setAutoLocate(true);
        configure.setDebug(true);
        configure.setEnv(EnvironmentEnum.DEV.getCode());
        configure.setEnvDesc(EnvironmentEnum.DEV.getDesc());
        configure.setHttpConnectTimeoutSecond(5);
        configure.setHttpReadTimeoutSecond(5);
        configure.setVerboseLog(true);
        configure.setKeyVector("123456");
        GatewayAddress gatewayAddress1 = new GatewayAddress();
        gatewayAddress1.setHost("api.rnkrsoft.com");
        gatewayAddress1.setPort(80);
        gatewayAddress1.setSchema("http");
        gatewayAddress1.setContextPath("/test");
        GatewayAddress gatewayAddress2 = new GatewayAddress();
        gatewayAddress2.setHost("api.dev.rnkrsoft.com");
        gatewayAddress2.setPort(8081);
        gatewayAddress2.setSchema("https");
        gatewayAddress2.setContextPath("/api");
        GatewayChannel gatewayChannel1 = new GatewayChannel("test1");
        gatewayChannel1.getGatewayAddresses().add(gatewayAddress1);
        configure.getChannels().add(gatewayChannel1);

        GatewayChannel gatewayChannel2 = new GatewayChannel("test2");
        gatewayChannel1.getGatewayAddresses().add(gatewayAddress2);
        configure.getChannels().add(gatewayChannel2);
        return configure;
    }
}
