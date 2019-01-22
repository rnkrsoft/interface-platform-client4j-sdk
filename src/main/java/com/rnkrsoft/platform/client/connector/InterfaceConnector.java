package com.rnkrsoft.platform.client.connector;

import com.rnkrsoft.platform.client.InterfaceSetting;
import com.rnkrsoft.platform.client.ServiceConfigure;
import com.rnkrsoft.platform.client.ServiceFactory;
import com.rnkrsoft.platform.protocol.ApiRequest;
import com.rnkrsoft.platform.protocol.ApiResponse;

/**
 * Created by rnkrsoft.com on 2018/10/6.
 * 接口连接器
 */
public interface InterfaceConnector {
    /**
     * 获取服务工厂
     *
     * @return 服务工厂
     */
    ServiceFactory getServiceFactory();

    /**
     * 调用接口服务
     *
     * @param request 请求对象
     * @param setting 配置对象
     * @return 应答对象
     */
    ApiResponse call(ApiRequest request, InterfaceSetting setting);
}