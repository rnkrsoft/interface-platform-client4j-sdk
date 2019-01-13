package com.rnkrsoft.platform.client;

import com.rnkrsoft.platform.client.connector.InterfaceConnector;
import com.rnkrsoft.platform.client.connector.mock.MockInterfaceConnector;
import com.rnkrsoft.platform.protocol.ApiRequest;
import com.rnkrsoft.platform.protocol.ApiResponse;
import org.junit.Test;

/**
 * Created by rnkrsoft.com on 2018/10/6.
 */
public class ServiceConfigureTest {

    @Test
    public void testSetInterfaceConnectorClass() throws Exception {
        ServiceConfigure serviceConfigure = new ServiceConfigure();
        serviceConfigure.setInterfaceConnectorClass(MockInterfaceConnector.class);
        InterfaceConnector interfaceConnector = serviceConfigure.getInterfaceConnector();
        ApiRequest request = new ApiRequest();
        request.setChannel("test-channel");
        request.setTxNo("010");
        request.setVersion("1");
        request.setData("{}");
        ApiResponse apiResponse = interfaceConnector.call(request, InterfaceSetting.builder().build());
        System.out.println(apiResponse);
    }
}