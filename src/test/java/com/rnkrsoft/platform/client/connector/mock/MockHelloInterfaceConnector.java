package com.rnkrsoft.platform.client.connector.mock;

import com.rnkrsoft.com.google.gson.Gson;
import com.rnkrsoft.com.google.gson.GsonBuilder;
import com.rnkrsoft.platform.client.InterfaceSetting;
import com.rnkrsoft.platform.client.ServiceConfigure;
import com.rnkrsoft.platform.client.connector.InterfaceConnector;
import com.rnkrsoft.platform.demo.service.HelloRequest;
import com.rnkrsoft.platform.demo.service.HelloResponse;
import com.rnkrsoft.platform.protocol.ApiRequest;
import com.rnkrsoft.platform.protocol.ApiResponse;
import com.rnkrsoft.platform.protocol.enums.InterfaceRspCode;
import com.rnkrsoft.security.AES;

/**
 * Created by rnkrsoft.com on 2018/10/6.
 */
public class MockHelloInterfaceConnector implements InterfaceConnector{
    ServiceConfigure serviceConfigure;

    public MockHelloInterfaceConnector(ServiceConfigure serviceConfigure) {
        this.serviceConfigure = serviceConfigure;
    }

    @Override
    public ServiceConfigure getServiceConfigure() {
        return serviceConfigure;
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
        Gson gson = new GsonBuilder().serializeNulls().create();
        String data = AES.decrypt("", request.getData());
        HelloRequest helloRequest = gson.fromJson(data, HelloRequest.class);
        HelloResponse helloResponse = new HelloResponse();
        helloResponse.setText("hello," + helloRequest.getName());
        response.setData(gson.toJson(helloResponse));
        return response;
    }
}
