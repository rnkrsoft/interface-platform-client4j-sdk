package com.rnkrsoft.platform.client.connector;

import com.rnkrsoft.com.google.gson.Gson;
import com.rnkrsoft.com.google.gson.GsonBuilder;
import com.rnkrsoft.platform.client.InterfaceSetting;
import com.rnkrsoft.platform.client.ServiceConfigure;
import com.rnkrsoft.platform.client.ServiceFactory;
import com.rnkrsoft.platform.protocol.ApiRequest;
import com.rnkrsoft.platform.protocol.ApiResponse;
import com.rnkrsoft.platform.protocol.enums.InterfaceRspCode;
import com.rnkrsoft.platform.protocol.service.FetchPublishResponse;
import com.rnkrsoft.platform.protocol.service.InterfaceChannel;
import com.rnkrsoft.platform.protocol.service.InterfaceDefinition;

/**
 * Created by rnkrsoft.com on 2019/1/19.
 */
public class MockFetchPublishSuccessInterfaceConnector implements InterfaceConnector{
    ServiceFactory serviceFactory;
    ServiceConfigure serviceConfigure;

    public MockFetchPublishSuccessInterfaceConnector(ServiceFactory serviceFactory) {
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
        ApiResponse response = new ApiResponse();
        response.setCode(InterfaceRspCode.SUCCESS);
        Gson gson = new GsonBuilder().serializeNulls().create();
        FetchPublishResponse fetchPublishResponse = new FetchPublishResponse();
        InterfaceChannel interfaceChannel = new InterfaceChannel();
        InterfaceDefinition interfaceDefinition = new InterfaceDefinition();
        interfaceDefinition.setChannel("test-channel");
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
