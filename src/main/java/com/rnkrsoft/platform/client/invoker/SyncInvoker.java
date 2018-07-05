package com.rnkrsoft.platform.client.invoker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rnkrsoft.platform.client.ServiceClient;
import com.rnkrsoft.platform.client.ServiceConfigure;
import com.rnkrsoft.platform.client.ServiceFactory;
import com.rnkrsoft.platform.client.ServiceRegistry;
import com.rnkrsoft.platform.client.scanner.InterfaceMetadata;
import com.rnkrsoft.platform.protocol.ApiRequest;
import com.rnkrsoft.platform.protocol.ApiResponse;
import com.rnkrsoft.platform.protocol.InterfaceRspCode;
import com.rnkrsoft.platform.protocol.service.InterfaceDefinition;
import com.rnkrsoft.platform.protocol.service.PublishService;
import com.rnkrsoft.security.AES;
import com.rnkrsoft.security.SHA;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by rnkrsoft.com on 2018/7/4.
 * 同步执行器
 */
public class SyncInvoker {
    final static Gson GSON = new GsonBuilder().serializeNulls().create();

    /**
     * 同步调用
     * @param serviceConfigure
     * @param serviceClass
     * @param methodName
     * @param requestClass
     * @param responseClass
     * @param request
     * @return
     */
    public static Object sync(ServiceConfigure serviceConfigure, Class serviceClass, String methodName, Class requestClass, Class responseClass, Object request) {
        serviceConfigure.generateSessionId();
        String txNo = null;
        String version = null;
        if (serviceClass != PublishService.class){
            InterfaceMetadata metadata = ServiceRegistry.lookupMetadata(serviceClass.getName(), methodName);
            if (metadata == null) {
                throw new NullPointerException("not found " + serviceClass.getName() + "." + methodName);
            }
            txNo = metadata.getTxNo();
            version = metadata.getVersion();
        }else{
            txNo = "000";
            version = "1";
        }
        String url = serviceConfigure.getSchema() + "://" + serviceConfigure.getHost() + ":" + serviceConfigure.getPort() + (serviceConfigure.getContextPath().startsWith("/") ? serviceConfigure.getContextPath() : ("/" + serviceConfigure.getContextPath()));
        ApiRequest apiRequest = new ApiRequest();

        apiRequest.setTxNo(txNo);
        apiRequest.setVersion(version);
        apiRequest.setSessionId(serviceConfigure.getSessionId());
        apiRequest.setUic(serviceConfigure.getUic());
        apiRequest.setUid(serviceConfigure.getUid());
        apiRequest.setToken(serviceConfigure.getToken());
        apiRequest.setLat(serviceConfigure.getLat());
        apiRequest.setLng(serviceConfigure.getLng());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        apiRequest.setTimestamp(dateFormat.format(new Date()));
        apiRequest.setData(GSON.toJson(request));
        String password = "";
        if (serviceClass != PublishService.class) {
            apiRequest.setChannel(serviceConfigure.getChannel());
            InterfaceDefinition definition = ServiceRegistry.lookupDefinition(apiRequest.getTxNo(), apiRequest.getVersion());
            if (definition.isUseTokenAsPassword()) {
                password = ServiceFactory.getServiceConfigure().getToken();
            }
            if (definition.isFirstSignSecondEncrypt()) {
                if (definition.getSignAlgorithm() == null || definition.getSignAlgorithm().isEmpty()) {

                } else if ("SHA512".equals(definition.getSignAlgorithm())) {
                    String sign = SHA.SHA512(apiRequest.getData() + password);
                    apiRequest.setSign(sign);
                } else {
                    throw new IllegalArgumentException("不支持的算法" + definition.getVerifyAlgorithm());
                }
                if (definition.getEncryptAlgorithm() == null || definition.getEncryptAlgorithm().isEmpty()) {

                } else if ("AES".equals(definition.getEncryptAlgorithm())) {
                    String data = AES.encrypt(password, apiRequest.getData());
                    apiRequest.setData(data);
                } else {
                    throw new IllegalArgumentException("不支持的算法" + definition.getEncryptAlgorithm());
                }
            } else {

                if (definition.getEncryptAlgorithm() == null || definition.getEncryptAlgorithm().isEmpty()) {

                } else if ("AES".equals(definition.getEncryptAlgorithm())) {
                    String data = AES.encrypt(password, apiRequest.getData());
                    apiRequest.setData(data);
                } else {
                    throw new IllegalArgumentException("不支持的算法" + definition.getEncryptAlgorithm());
                }
                if (definition.getSignAlgorithm() == null || definition.getSignAlgorithm().isEmpty()) {

                } else if ("SHA512".equals(definition.getSignAlgorithm())) {
                    String sign = SHA.SHA512(apiRequest.getData() + password);
                    apiRequest.setSign(sign);
                } else {
                    throw new IllegalArgumentException("不支持的算法" + definition.getVerifyAlgorithm());
                }
            }
        } else {
            apiRequest.setChannel("public");
        }

        ApiResponse apiResponse = ServiceClient.call(serviceConfigure, url, apiRequest);
        if (InterfaceRspCode.valueOfCode(apiResponse.getCode()) != InterfaceRspCode.SUCCESS) {
            throw new RuntimeException(apiResponse.getDesc());
        }
        String data = apiResponse.getData();
        if (data == null || data.isEmpty()) {
            throw new NullPointerException("data is null");
        }
        if (serviceClass != PublishService.class) {
            InterfaceDefinition definition = ServiceRegistry.lookupDefinition(apiRequest.getTxNo(), apiRequest.getVersion());
            if (definition.isFirstVerifySecondDecrypt()) {
                if (definition.getVerifyAlgorithm() == null || definition.getVerifyAlgorithm().isEmpty()) {

                } else if ("SHA512".equals(definition.getVerifyAlgorithm())) {
                    String sign = SHA.SHA512(apiResponse.getData() + password);
                    if (!sign.equals(apiResponse.getSign())) {
                        throw new IllegalArgumentException("无效签字");
                    }
                } else {
                    throw new IllegalArgumentException("不支持的算法" + definition.getVerifyAlgorithm());
                }

                if (definition.getDecryptAlgorithm() == null || definition.getDecryptAlgorithm().isEmpty()) {

                } else if ("AES".equals(definition.getDecryptAlgorithm())) {
                    String data0 = AES.decrypt(password, apiResponse.getData());
                    apiResponse.setData(data0);
                } else {
                    throw new IllegalArgumentException("不支持的算法" + definition.getDecryptAlgorithm());
                }
            } else {
                if (definition.getDecryptAlgorithm() == null || definition.getDecryptAlgorithm().isEmpty()) {

                } else if ("AES".equals(definition.getDecryptAlgorithm())) {
                    String data0 = AES.decrypt(password, apiResponse.getData());
                    apiResponse.setData(data0);
                } else {
                    throw new IllegalArgumentException("不支持的算法" + definition.getDecryptAlgorithm());
                }
                if (definition.getVerifyAlgorithm() == null || definition.getVerifyAlgorithm().isEmpty()) {

                } else if ("SHA512".equals(definition.getVerifyAlgorithm())) {
                    String sign = SHA.SHA512(apiResponse.getData() + password);
                    if (!sign.equals(apiResponse.getSign())) {
                        throw new IllegalArgumentException("无效签字");
                    }
                } else {
                    throw new IllegalArgumentException("不支持的算法" + definition.getVerifyAlgorithm());
                }
            }
        }
        Object response = null;
        try {
            response = GSON.fromJson(apiResponse.getData(), responseClass);
        } catch (Exception e) {
            throw new IllegalArgumentException("无效的JSON格式");
        }
        return response;
    }
}
