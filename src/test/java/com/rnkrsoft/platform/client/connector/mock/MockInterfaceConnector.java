package com.rnkrsoft.platform.client.connector.mock;

import com.rnkrsoft.com.google.gson.Gson;
import com.rnkrsoft.com.google.gson.GsonBuilder;
import com.rnkrsoft.platform.client.InterfaceSetting;
import com.rnkrsoft.platform.client.ServiceConfigure;
import com.rnkrsoft.platform.client.connector.InterfaceConnector;
import com.rnkrsoft.platform.protocol.ApiRequest;
import com.rnkrsoft.platform.protocol.ApiResponse;
import com.rnkrsoft.platform.protocol.enums.InterfaceRspCode;
import com.rnkrsoft.platform.protocol.service.FetchPublishResponse;
import com.rnkrsoft.platform.protocol.service.InterfaceChannel;
import com.rnkrsoft.platform.protocol.service.InterfaceDefinition;

/**
 * Created by rnkrsoft.com on 2018/10/6.
 */
public class MockInterfaceConnector implements InterfaceConnector{
    ServiceConfigure serviceConfigure;

    public MockInterfaceConnector(ServiceConfigure serviceConfigure) {
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
        ApiResponse response = new ApiResponse();
        response.setCode(InterfaceRspCode.SUCCESS);
        Gson gson = new GsonBuilder().serializeNulls().create();
        FetchPublishResponse fetchPublishResponse = new FetchPublishResponse();
        InterfaceChannel interfaceChannel = new InterfaceChannel();
        InterfaceDefinition interfaceDefinition = new InterfaceDefinition();
        interfaceDefinition.setChannel("channel1");
        interfaceDefinition.setTxNo("010");
        interfaceDefinition.setVersion("1");
        interfaceDefinition.setEncryptAlgorithm("AES");
        interfaceDefinition.setDecryptAlgorithm("AES");
        interfaceDefinition.setSignAlgorithm("SHA512");
        interfaceDefinition.setVerifyAlgorithm("SHA512");
        interfaceChannel.getInterfaces().add(interfaceDefinition);
        fetchPublishResponse.getChannels().add(interfaceChannel);
        response.setData(gson.toJson(fetchPublishResponse));
        return response;
    }
}
