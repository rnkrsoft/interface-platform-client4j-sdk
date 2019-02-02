package com.rnkrsoft.platform.client.connector.http;

import com.rnkrsoft.platform.client.InterfaceSetting;
import com.rnkrsoft.platform.client.ServiceFactory;
import com.rnkrsoft.platform.client.logger.Logger;
import com.rnkrsoft.platform.client.logger.LoggerFactory;
import com.rnkrsoft.platform.protocol.ApiRequest;
import com.rnkrsoft.platform.protocol.ApiResponse;
import com.rnkrsoft.utils.DateUtils;
import org.junit.Test;

/**
 * Created by rnkrsoft.com on 2019/1/19.
 */
public class HttpInterfaceConnectorTest {

    @Test
    public void testCall0() throws Exception {
        Logger log = LoggerFactory.getLogger(HttpInterfaceConnectorTest.class);
        log.generateSessionId();
        ServiceFactory serviceFactory =ServiceFactory.newInstance();
        HttpInterfaceConnector connector = new HttpInterfaceConnector(serviceFactory);
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setChannel("user_app");
        apiRequest.setTxNo("010");
        apiRequest.setVersion("1");
        apiRequest.setSessionId(log.getSessionId());
        apiRequest.setUic("");
        apiRequest.setUid("");
        apiRequest.setToken("");
        apiRequest.setTimestamp(DateUtils.getTimestamp());
        apiRequest.setData("{}");
        ApiResponse apiResponse = connector.call0("https://localhost:8001/api", apiRequest, InterfaceSetting.builder().httpReadTimeoutSecond(20).httpConnectTimeoutSecond(20).build());
        System.out.println(apiResponse);
    }
}