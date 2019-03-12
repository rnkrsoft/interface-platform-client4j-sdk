package com.rnkrsoft.platform.client.configure;

import com.rnkrsoft.platform.client.connector.http.ConfigureService;
import com.rnkrsoft.platform.protocol.service.FetchConfigureRequest;
import com.rnkrsoft.platform.protocol.service.FetchConfigureResponse;
import org.junit.Test;

/**
 * Created by rnkrsoft.com on 2019/1/19.
 */
public class ConfigureServiceTest {

    @Test
    public void testFetchConfigure() throws Exception {
        ConfigureService configureService = new ConfigureService("http", "localhost", 80, "configure");
        FetchConfigureRequest request = new FetchConfigureRequest();
        request.setUic("456");
        request.setDeviceType("iOS");
        request.setAppVersion("4.0.0");
        request.setLat("1112");
        request.setLng("1112");
        request.getChannels().add("demo_channel");
        for (int i = 0; i < 10; i++) {
            FetchConfigureResponse resp = configureService.fetchConfigure(request);
            System.out.println(resp);
        }
    }
}