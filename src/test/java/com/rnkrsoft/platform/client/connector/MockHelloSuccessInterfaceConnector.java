package com.rnkrsoft.platform.client.connector;

import com.rnkrsoft.com.google.gson.Gson;
import com.rnkrsoft.com.google.gson.GsonBuilder;
import com.rnkrsoft.platform.client.InterfaceSetting;
import com.rnkrsoft.platform.client.ServiceConfigure;
import com.rnkrsoft.platform.client.ServiceFactory;
import com.rnkrsoft.platform.client.demo.domain.HelloRequest;
import com.rnkrsoft.platform.client.demo.domain.HelloResponse;
import com.rnkrsoft.platform.protocol.ApiRequest;
import com.rnkrsoft.platform.protocol.ApiResponse;
import com.rnkrsoft.platform.protocol.enums.InterfaceRspCode;
import com.rnkrsoft.security.AES;

/**
 * Created by rnkrsoft.com on 2019/1/19.
 */
public class MockHelloSuccessInterfaceConnector implements InterfaceConnector{
    ServiceFactory serviceFactory;
    ServiceConfigure serviceConfigure;

    public MockHelloSuccessInterfaceConnector(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
        this.serviceConfigure = serviceFactory.getServiceConfigure();
    }

    @Override
    public ServiceFactory getServiceFactory() {
        return serviceFactory;
    }
    @Override
    public ApiResponse call(ApiRequest request, InterfaceSetting setting) {
        System.out.println("channel:" + request.getChannel());
        System.out.println("txNo:" + request.getTxNo());
        System.out.println("version:" + request.getVersion());
        System.out.println("data:" + request.getData());
        System.out.println("sign:" + request.getSign());
        ApiResponse response = new ApiResponse();
        response.setCode(InterfaceRspCode.SUCCESS);
        Gson gson = new GsonBuilder().create();
        HelloRequest helloRequest = gson.fromJson(request.getData(), HelloRequest.class);
        HelloResponse helloResponse = new HelloResponse();
        helloResponse.setText("hello," + helloRequest.getName());
        response.setData(gson.toJson(helloResponse));
        return response;
    }
}
