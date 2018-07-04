package com.rnkrsoft.platform.client.invoker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.rnkrsoft.platform.client.*;
import com.rnkrsoft.platform.client.scanner.InterfaceMetadata;
import com.rnkrsoft.platform.protocol.ApiRequest;
import com.rnkrsoft.platform.protocol.ApiResponse;
import com.rnkrsoft.platform.protocol.InterfaceRspCode;
import com.rnkrsoft.platform.protocol.TokenReadable;
import com.rnkrsoft.platform.protocol.service.InterfaceDefinition;
import com.rnkrsoft.platform.protocol.service.PublishService;
import com.rnkrsoft.security.AES;
import com.rnkrsoft.security.SHA;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Callable;


/**
 * Created by rnkrsoft.com on 2018/7/4.
 * 异步执行器
 */
public class AsyncInvoker implements Callable<Boolean> {
    final static Gson GSON = new GsonBuilder().serializeNulls().create();
    ServiceConfigure serviceConfigure;
    Class serviceClass;
    String methodName;
    Class requestClass;
    Class responseClass;
    Object request;
    AsyncHandler asyncHandler;

    public AsyncInvoker(ServiceConfigure serviceConfigure, Class serviceClass, String methodName, Class requestClass, Class responseClass, Object request, AsyncHandler asyncHandler) {
        this.serviceConfigure = serviceConfigure;
        this.serviceClass = serviceClass;
        this.methodName = methodName;
        this.requestClass = requestClass;
        this.responseClass = responseClass;
        this.request = request;
        this.asyncHandler = asyncHandler;
    }

    @Override
    public Boolean call() throws Exception {
        String txNo = null;
        String version = null;
        if (serviceClass != PublishService.class){
            InterfaceMetadata metadata = ServiceRegistry.lookupMetadata(serviceClass.getName(), methodName);
            if (metadata == null) {
                asyncHandler.fail(InterfaceRspCode.INTERFACE_NOT_DEFINED, "");
                return false;
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
        apiRequest.setSessionId(UUID.randomUUID().toString());
        apiRequest.setUic(serviceConfigure.getUic());
        apiRequest.setUid(serviceConfigure.getUid());
        apiRequest.setToken(serviceConfigure.getToken());
        apiRequest.setLat(1.0D);
        apiRequest.setLng(1.0D);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        apiRequest.setTimestamp(dateFormat.format(new Date()));
        apiRequest.setData(GSON.toJson(request));
        String password = "";
        if (serviceClass != PublishService.class) {
            apiRequest.setChannel(serviceConfigure.getChannel());
            InterfaceDefinition definition = ServiceRegistry.lookupDefinition(txNo, version);
            if (definition == null) {
                asyncHandler.fail(InterfaceRspCode.INTERFACE_NOT_DEFINED, "");
                return false;
            }
            if (definition.isUseTokenAsPassword()) {
                password = ServiceFactory.getServiceConfigure().getToken();
            }
            if (definition.isFirstSignSecondEncrypt()) {
                if (definition.getSignAlgorithm() == null || definition.getSignAlgorithm().isEmpty()) {

                } else if ("SHA512".equals(definition.getSignAlgorithm())) {
                    String sign = SHA.SHA512(apiRequest.getData() + password);
                    apiRequest.setSign(sign);
                } else {
                    asyncHandler.fail(InterfaceRspCode.NOT_SUPPORTED_ENCRYPT_DECRYPT_ALGORITHM, "");
                    return false;
                }
                if (definition.getEncryptAlgorithm() == null || definition.getEncryptAlgorithm().isEmpty()) {

                } else if ("AES".equals(definition.getEncryptAlgorithm())) {
                    String data = AES.encrypt(password, apiRequest.getData());
                    apiRequest.setData(data);
                } else {
                    asyncHandler.fail(InterfaceRspCode.NOT_SUPPORTED_ENCRYPT_DECRYPT_ALGORITHM, "");
                    return false;
                }
            } else {

                if (definition.getEncryptAlgorithm() == null || definition.getEncryptAlgorithm().isEmpty()) {

                } else if ("AES".equals(definition.getEncryptAlgorithm())) {
                    String data = AES.encrypt(password, apiRequest.getData());
                    apiRequest.setData(data);
                } else {
                    asyncHandler.fail(InterfaceRspCode.NOT_SUPPORTED_ENCRYPT_DECRYPT_ALGORITHM, "");
                    return false;
                }
                if (definition.getSignAlgorithm() == null || definition.getSignAlgorithm().isEmpty()) {

                } else if ("SHA512".equals(definition.getSignAlgorithm())) {
                    String sign = SHA.SHA512(apiRequest.getData() + password);
                    apiRequest.setSign(sign);
                } else {
                    asyncHandler.fail(InterfaceRspCode.NOT_SUPPORTED_ENCRYPT_DECRYPT_ALGORITHM, "");
                    return false;
                }
            }
        }else{
            apiRequest.setChannel("public");
        }

        ApiResponse apiResponse = ServiceClient.call(serviceConfigure, url, apiRequest);
        if (InterfaceRspCode.valueOfCode(apiResponse.getCode()) != InterfaceRspCode.SUCCESS) {
            asyncHandler.fail(apiResponse.getCode(), apiResponse.getDesc(), "");
            return false;
        }
        String data = apiResponse.getData();
        if (data == null || data.isEmpty()) {
            asyncHandler.fail(InterfaceRspCode.RESPONSE_DATA_IS_NULL, "");
            return false;
        }
        if (serviceClass != PublishService.class) {
            InterfaceDefinition definition = ServiceRegistry.lookupDefinition(txNo, version);
            if (definition.isFirstVerifySecondDecrypt()) {
                if (definition.getVerifyAlgorithm() == null || definition.getVerifyAlgorithm().isEmpty()) {

                } else if ("SHA512".equals(definition.getVerifyAlgorithm())) {
                    String sign = SHA.SHA512(apiResponse.getData() + password);
                    if (!sign.equals(apiResponse.getSign())) {
                        asyncHandler.fail(InterfaceRspCode.REQUEST_SIGN_ILLEGAL, "");
                        return false;
                    }
                } else {
                    asyncHandler.fail(InterfaceRspCode.NOT_SUPPORTED_ENCRYPT_DECRYPT_ALGORITHM, "");
                    return false;
                }

                if (definition.getDecryptAlgorithm() == null || definition.getDecryptAlgorithm().isEmpty()) {

                } else if ("AES".equals(definition.getDecryptAlgorithm())) {
                    String data0 = AES.decrypt(password, apiResponse.getData());
                    apiResponse.setData(data0);
                } else {
                    asyncHandler.fail(InterfaceRspCode.NOT_SUPPORTED_ENCRYPT_DECRYPT_ALGORITHM, "");
                    return false;
                }
            } else {
                if (definition.getDecryptAlgorithm() == null || definition.getDecryptAlgorithm().isEmpty()) {

                } else if ("AES".equals(definition.getDecryptAlgorithm())) {
                    String data0 = AES.decrypt(password, apiResponse.getData());
                    apiResponse.setData(data0);
                } else {
                    asyncHandler.fail(InterfaceRspCode.NOT_SUPPORTED_ENCRYPT_DECRYPT_ALGORITHM, "");
                    return false;
                }
                if (definition.getVerifyAlgorithm() == null || definition.getVerifyAlgorithm().isEmpty()) {

                } else if ("SHA512".equals(definition.getVerifyAlgorithm())) {
                    String sign = SHA.SHA512(apiResponse.getData() + password);
                    if (!sign.equals(apiResponse.getSign())) {
                        if (!sign.equals(apiResponse.getSign())) {
                            asyncHandler.fail(InterfaceRspCode.REQUEST_SIGN_ILLEGAL, "");
                            return false;
                        }
                    }
                } else {
                    asyncHandler.fail(InterfaceRspCode.NOT_SUPPORTED_ENCRYPT_DECRYPT_ALGORITHM, "");
                    return false;
                }
            }
        }
        Object response = null;
        try {
            response = GSON.fromJson(apiResponse.getData(), responseClass);
            if (response instanceof TokenReadable){//如果有实现Token获取接口，则设置Token值
                TokenReadable tokenReadable = (TokenReadable)response;
                serviceConfigure.setToken(tokenReadable.getToken());
            }
        } catch (JsonSyntaxException e) {
            asyncHandler.fail(InterfaceRspCode.INVALID_COMMUNICATION_MESSAGE, "");
            return false;
        }catch (Exception e){
            try {
                asyncHandler.exception(e);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return false;
        }
        try {
            asyncHandler.success(response);
            return true;
        }catch (Throwable e){
            try {
                asyncHandler.exception(e);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return false;
        }
    }
}
