package com.rnkrsoft.platform.android.proxy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rnkrsoft.platform.android.ServiceConfigure;
import com.rnkrsoft.platform.android.client.ServiceClient;
import com.rnkrsoft.platform.protocol.ApiRequest;
import com.rnkrsoft.platform.protocol.ApiResponse;
import com.rnkrsoft.platform.protocol.ClientTypeEnum;
import com.rnkrsoft.platform.protocol.InterfaceRspCode;

import javax.web.doc.annotation.ApidocInterface;
import javax.web.doc.annotation.ApidocService;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by rnkrsoft.com on 2018/6/27.
 */
public class ServiceProxy<T> implements InvocationHandler {
    final static Gson GSON = new GsonBuilder().serializeNulls().create();
    /**
     * 服务配置对象
     */
    ServiceConfigure serviceConfigure;
    /**
     * 服务接口类
     */
    Class<T> serviceClass;

    public ServiceProxy(ServiceConfigure serviceConfigure, Class<T> serviceClass) {
        this.serviceConfigure = serviceConfigure;
        this.serviceClass = serviceClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ApidocService apidocService = serviceClass.getAnnotation(ApidocService.class);
        if (apidocService == null) {
            throw new Exception("@ApidocService 注解未标注");
        }
        ApidocInterface apidocInterface = method.getAnnotation(ApidocInterface.class);
        if (apidocInterface == null) {
            throw new Exception("@ApidocInterface 注解未标注");
        }
        Object businessRequest = args[0];
        String txNo = apidocInterface.name();
        String version = apidocInterface.version();
        String url = serviceConfigure.getSchema() + "://" + serviceConfigure.getHost() + ":" + serviceConfigure.getPort() + (serviceConfigure.getContextPath().startsWith("/") ? serviceConfigure.getContextPath() : ("/" + serviceConfigure.getContextPath()));
        ApiRequest request = new ApiRequest();
        request.setSessionId(UUID.randomUUID().toString());
        request.setUic(serviceConfigure.getUic());
        request.setUid(serviceConfigure.getUid());
        request.setToken(serviceConfigure.getToken());
        request.setLat(1.0D);
        request.setLng(1.0D);
        request.setClientType(ClientTypeEnum.MANAGER_APP);
        request.setTimestamp("");
        request.setTxNo(txNo);
        request.setVersion(version);
        request.setData(GSON.toJson(businessRequest));
        ApiResponse response = ServiceClient.call(url, request);
        if (InterfaceRspCode.valueOfCode(response.getCode()) != InterfaceRspCode.SUCCESS) {
            //TODO
            throw new RuntimeException(response.getDesc());
        }
        String data = response.getData();
        if (data == null || data.isEmpty()) {
            //TODO
        }
        Object businessResponse = null;
        try {
            businessResponse = GSON.fromJson(data, method.getReturnType());
        } catch (Exception e) {
            //TODO
        }
        return businessResponse;
    }
}
