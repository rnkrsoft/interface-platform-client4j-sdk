package com.rnkrsoft.platform.client.connector;

import com.rnkrsoft.com.google.gson.Gson;
import com.rnkrsoft.com.google.gson.GsonBuilder;
import com.rnkrsoft.platform.client.InterfaceSetting;
import com.rnkrsoft.platform.client.ServiceConfigure;
import com.rnkrsoft.platform.client.ServiceFactory;
import com.rnkrsoft.platform.client.connector.InterfaceConnector;
import com.rnkrsoft.platform.client.demo.domain.HelloRequest;
import com.rnkrsoft.platform.client.demo.domain.HelloResponse;
import com.rnkrsoft.platform.protocol.ApiRequest;
import com.rnkrsoft.platform.protocol.ApiResponse;
import com.rnkrsoft.platform.protocol.enums.InterfaceRspCode;

/**
 * Created by rnkrsoft.com on 2019/1/19.
 */
public class MockHelloFailureInterfaceConnector implements InterfaceConnector{
    ServiceFactory serviceFactory;
    ServiceConfigure serviceConfigure;

    public MockHelloFailureInterfaceConnector(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
        this.serviceConfigure = serviceFactory.getServiceConfigure();
    }

    @Override
    public ServiceFactory getServiceFactory() {
        return serviceFactory;
    }
    @Override
    public ApiResponse call(ApiRequest request, InterfaceSetting setting) {
        System.out.println(request);
        ApiResponse response = new ApiResponse();
        response.setCode(InterfaceRspCode.SYSTEM_MAINTENANCE);
        return response;
    }
}
