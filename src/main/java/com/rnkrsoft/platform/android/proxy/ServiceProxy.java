package com.rnkrsoft.platform.android.proxy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rnkrsoft.platform.android.ServiceConfigure;
import com.rnkrsoft.platform.android.ServiceFactory;
import com.rnkrsoft.platform.android.ServiceRegister;
import com.rnkrsoft.platform.android.client.ServiceClient;
import com.rnkrsoft.platform.android.scanner.InterfaceMetadata;
import com.rnkrsoft.platform.protocol.ApiRequest;
import com.rnkrsoft.platform.protocol.ApiResponse;
import com.rnkrsoft.platform.protocol.InterfaceRspCode;
import com.rnkrsoft.platform.protocol.service.InterfaceDefinition;
import com.rnkrsoft.platform.protocol.service.PublishService;
import com.rnkrsoft.security.AES;
import com.rnkrsoft.security.SHA;

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
        Object businessRequest = args[0];
        String txNo = null;
        String version = null;
        InterfaceMetadata metadata = ServiceRegister.lookupMetadata(serviceClass.getName(), method.getName());
        txNo = metadata.getTxNo();
        version = metadata.getVersion();
        String url = serviceConfigure.getSchema() + "://" + serviceConfigure.getHost() + ":" + serviceConfigure.getPort() + (serviceConfigure.getContextPath().startsWith("/") ? serviceConfigure.getContextPath() : ("/" + serviceConfigure.getContextPath()));
        ApiRequest request = new ApiRequest();

        request.setTxNo(txNo);
        request.setVersion(version);
        request.setSessionId(UUID.randomUUID().toString());
        request.setUic(serviceConfigure.getUic());
        request.setUid(serviceConfigure.getUid());
        request.setToken(serviceConfigure.getToken());
        request.setLat(1.0D);
        request.setLng(1.0D);
        request.setTimestamp("");
        request.setData(GSON.toJson(businessRequest));
        String password = "";
        if (serviceClass != PublishService.class) {
            request.setChannel(serviceConfigure.getChannel());
            InterfaceDefinition definition = ServiceRegister.lookupDefinition(request.getTxNo(), request.getVersion());
            if (definition.isUseTokenAsPassword()) {
                password = ServiceFactory.getServiceConfigure().getToken();
            }
            if (definition.isFirstSignSecondEncrypt()) {
                if ("SHA512".equals(definition.getSignAlgorithm())) {
                    String sign = SHA.SHA512(request.getData() + password);
                    request.setSign(sign);
                }
                if ("AES".equals(definition.getEncryptAlgorithm())) {
                    String data = AES.encrypt(password, request.getData());
                    request.setData(data);
                }
            } else {
                if ("AES".equals(definition.getEncryptAlgorithm())) {
                    String data = AES.encrypt(password, request.getData());
                    request.setData(data);
                }
                if ("SHA512".equals(definition.getSignAlgorithm())) {
                    String sign = SHA.SHA512(request.getData() + password);
                    request.setSign(sign);
                }
            }
        }else{
            request.setChannel("public");
        }

        ApiResponse response = ServiceClient.call(url, request);
        if (InterfaceRspCode.valueOfCode(response.getCode()) != InterfaceRspCode.SUCCESS) {
            throw new RuntimeException(response.getDesc());
        }
        String data = response.getData();
        if (data == null || data.isEmpty()) {
            throw new NullPointerException("data is null");
        }
        if (serviceClass != PublishService.class) {
            InterfaceDefinition definition = ServiceRegister.lookupDefinition(request.getTxNo(), request.getVersion());
            if (definition.isFirstVerifySecondDecrypt()) {
                if ("SHA512".equals(definition.getVerifyAlgorithm())) {
                    String sign = SHA.SHA512(request.getData() + password);
                    if (!sign.equals(response.getSign())) {
                        throw new IllegalArgumentException("无效签字");
                    }
                }
                if ("AES".equals(definition.getDecryptAlgorithm())) {
                    String data0 = AES.encrypt(password, response.getData());
                    response.setData(data0);
                }
            } else {

                if ("AES".equals(definition.getDecryptAlgorithm())) {
                    String data0 = AES.encrypt(password, response.getData());
                    response.setData(data0);
                }
                if ("SHA512".equals(definition.getVerifyAlgorithm())) {
                    String sign = SHA.SHA512(request.getData() + password);
                    if (!sign.equals(response.getSign())) {
                        throw new IllegalArgumentException("无效签字");
                    }
                }
            }
        }
        Object businessResponse = null;
        try {
            businessResponse = GSON.fromJson(response.getData(), method.getReturnType());
        } catch (Exception e) {
            //TODO
            throw new IllegalArgumentException("无效的JSON格式");
        }
        return businessResponse;
    }
}
