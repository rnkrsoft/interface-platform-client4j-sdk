package com.rnkrsoft.platform.client.configure;

import com.rnkrsoft.platform.protocol.service.FetchConfigureRequest;
import com.rnkrsoft.platform.protocol.service.FetchConfigureResponse;
import org.junit.Test;

import java.util.UUID;

/**
 * Created by rnkrsoft.com on 2018/10/7.
 */
public class ConfigureServiceTest {

    @Test
    public void testFetchConfigure() throws Exception {
        ConfigureService configureService = new ConfigureService("https", "gateway-configure.zxevpop.com", 8001, "configure");
        FetchConfigureRequest request = new FetchConfigureRequest();
        request.setUic("80B78DE6-EFF4-4A98-B725-1F012326B5E1");
        request.setDeviceType("iOS");
        request.setAppVersion("4.0.0");
        request.setLat("1112");
        request.setLng("1112");
        request.getChannels().add("user_app");
        request.getChannels().add("public");
        for (int i = 0; i <10000 ; i++) {
            FetchConfigureResponse resp = configureService.fetchConfigure(request);
            System.out.println(resp);
        }
    }
}