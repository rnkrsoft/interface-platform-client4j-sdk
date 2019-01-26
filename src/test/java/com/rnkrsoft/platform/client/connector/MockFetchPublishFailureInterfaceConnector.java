package com.rnkrsoft.platform.client.connector;

import com.rnkrsoft.platform.client.InterfaceSetting;
import com.rnkrsoft.platform.client.ServiceConfigure;
import com.rnkrsoft.platform.client.ServiceFactory;
import com.rnkrsoft.platform.client.logger.Logger;
import com.rnkrsoft.platform.client.logger.LoggerFactory;
import com.rnkrsoft.platform.protocol.ApiRequest;
import com.rnkrsoft.platform.protocol.ApiResponse;
import com.rnkrsoft.platform.protocol.enums.InterfaceRspCode;

/**
 * Created by rnkrsoft.com on 2019/1/19.
 */
public class MockFetchPublishFailureInterfaceConnector implements InterfaceConnector {
    ServiceFactory serviceFactory;
    ServiceConfigure serviceConfigure;

    public MockFetchPublishFailureInterfaceConnector(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
        this.serviceConfigure = serviceFactory.getServiceConfigure();
    }

    @Override
    public ServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    @Override
    public ApiResponse call(ApiRequest request, InterfaceSetting setting) {
        Logger logger = LoggerFactory.getLogger(MockFetchPublishFailureInterfaceConnector.class);
        logger.debug("channel:" + request.getChannel());
        logger.debug("txNo:" + request.getTxNo());
        logger.debug("version:" + request.getVersion());
        logger.debug("data:" + request.getData());
        ApiResponse response = new ApiResponse();
        response.setCode(InterfaceRspCode.FAIL);
        return response;
    }
}
