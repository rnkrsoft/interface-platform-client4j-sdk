package com.rnkrsoft.platform.android.proxy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rnkrsoft.platform.android.ServiceConfigure;
import com.rnkrsoft.platform.android.ServiceFactory;
import com.rnkrsoft.platform.android.ServiceRegistry;
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
import java.text.SimpleDateFormat;
import java.util.Date;
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
        InterfaceMetadata metadata = ServiceRegistry.lookupMetadata(serviceClass.getName(), method.getName());
        if (metadata == null) {
            throw new NullPointerException("not found " + serviceClass.getName() + "." + method.getName());
        }
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        request.setTimestamp(dateFormat.format(new Date()));
        request.setData(GSON.toJson(businessRequest));
        String password = "";
        if (serviceClass != PublishService.class) {
            request.setChannel(serviceConfigure.getChannel());
            InterfaceDefinition definition = ServiceRegistry.lookupDefinition(request.getTxNo(), request.getVersion());
            if (definition.isUseTokenAsPassword()) {
                password = ServiceFactory.getServiceConfigure().getToken();
            }
            if (definition.isFirstSignSecondEncrypt()) {
                if (definition.getSignAlgorithm() == null || definition.getSignAlgorithm().isEmpty()) {

                }else if ("SHA512".equals(definition.getSignAlgorithm())) {
                    String sign = SHA.SHA512(request.getData() + password);
                    request.setSign(sign);
                }else{
                    throw new IllegalArgumentException("不支持的算法" + definition.getVerifyAlgorithm());
                }
                if (definition.getEncryptAlgorithm() == null || definition.getEncryptAlgorithm().isEmpty()) {

                }else if ("AES".equals(definition.getEncryptAlgorithm())) {
                    String data = AES.encrypt(password, request.getData());
                    request.setData(data);
                }else{
                    throw new IllegalArgumentException("不支持的算法" + definition.getEncryptAlgorithm());
                }
            } else {

                if (definition.getEncryptAlgorithm() == null || definition.getEncryptAlgorithm().isEmpty()) {

                }else if ("AES".equals(definition.getEncryptAlgorithm())) {
                    String data = AES.encrypt(password, request.getData());
                    request.setData(data);
                }else{
                    throw new IllegalArgumentException("不支持的算法" + definition.getEncryptAlgorithm());
                }
                if (definition.getSignAlgorithm() == null || definition.getSignAlgorithm().isEmpty()) {

                }else if ("SHA512".equals(definition.getSignAlgorithm())) {
                    String sign = SHA.SHA512(request.getData() + password);
                    request.setSign(sign);
                }else{
                    throw new IllegalArgumentException("不支持的算法" + definition.getVerifyAlgorithm());
                }
            }
        } else {
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
            InterfaceDefinition definition = ServiceRegistry.lookupDefinition(request.getTxNo(), request.getVersion());
            if (definition.isFirstVerifySecondDecrypt()) {
                if (definition.getVerifyAlgorithm() == null || definition.getVerifyAlgorithm().isEmpty()) {

                } else if ("SHA512".equals(definition.getVerifyAlgorithm())) {
                    String sign = SHA.SHA512(response.getData() + password);
                    if (!sign.equals(response.getSign())) {
                        throw new IllegalArgumentException("无效签字");
                    }
                } else {
                    throw new IllegalArgumentException("不支持的算法" + definition.getVerifyAlgorithm());
                }

                if (definition.getDecryptAlgorithm() == null || definition.getDecryptAlgorithm().isEmpty()) {

                } else if ("AES".equals(definition.getDecryptAlgorithm())) {
                    String data0 = AES.decrypt(password, response.getData());
                    response.setData(data0);
                } else {
                    throw new IllegalArgumentException("不支持的算法" + definition.getDecryptAlgorithm());
                }
            } else {
                if (definition.getDecryptAlgorithm() == null || definition.getDecryptAlgorithm().isEmpty()) {

                } else if ("AES".equals(definition.getDecryptAlgorithm())) {
                    String data0 = AES.decrypt(password, response.getData());
                    response.setData(data0);
                } else {
                    throw new IllegalArgumentException("不支持的算法" + definition.getDecryptAlgorithm());
                }
                if (definition.getVerifyAlgorithm() == null || definition.getVerifyAlgorithm().isEmpty()) {

                } else if ("SHA512".equals(definition.getVerifyAlgorithm())) {
                    String sign = SHA.SHA512(response.getData() + password);
                    if (!sign.equals(response.getSign())) {
                        throw new IllegalArgumentException("无效签字");
                    }
                } else {
                    throw new IllegalArgumentException("不支持的算法" + definition.getVerifyAlgorithm());
                }
            }
        }
        Object businessResponse = null;
        try {
            businessResponse = GSON.fromJson(response.getData(), method.getReturnType());
        } catch (Exception e) {
            throw new IllegalArgumentException("无效的JSON格式");
        }
        return businessResponse;
    }
}
