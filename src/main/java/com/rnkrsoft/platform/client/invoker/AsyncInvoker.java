package com.rnkrsoft.platform.client.invoker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.rnkrsoft.platform.client.*;
import com.rnkrsoft.platform.client.scanner.InterfaceMetadata;
import com.rnkrsoft.platform.client.utils.DateUtil;
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
    /**
     * 服务接口类
     */
    Class serviceClass;
    /**
     * 方法名
     */
    String methodName;
    /**
     * 请求类对象
     */
    Class requestClass;
    /**
     * 应答类对象
     */
    Class responseClass;
    /**
     * 请求对象
     */
    Object request;
    /**
     * 异步处理器实例
     */
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
        if (serviceClass != PublishService.class) {
            InterfaceMetadata metadata = ServiceRegistry.lookupMetadata(serviceClass.getName(), methodName);
            if (metadata == null) {
                asyncHandler.fail(InterfaceRspCode.INTERFACE_NOT_DEFINED, "");
                return false;
            }
            txNo = metadata.getTxNo();
            version = metadata.getVersion();
        } else {
            txNo = "000";//000作为接口发布接口
            version = "1";
        }
        if (serviceConfigure.isDebug()) {
            serviceConfigure.log("{} sessionId[{}] async call txNo:'{}' version:{} ", DateUtil.getDate(), serviceConfigure.getSessionId(), txNo, version);
        }
        String url = serviceConfigure.getSchema() + "://" + serviceConfigure.getHost() + ":" + serviceConfigure.getPort() + (serviceConfigure.getContextPath().startsWith("/") ? serviceConfigure.getContextPath() : ("/" + serviceConfigure.getContextPath()));
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setTxNo(txNo);
        apiRequest.setVersion(version);
        apiRequest.setSessionId(UUID.randomUUID().toString());
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
            InterfaceDefinition definition = ServiceRegistry.lookupDefinition(txNo, version);
            if (definition == null) {
                asyncHandler.fail(InterfaceRspCode.INTERFACE_NOT_DEFINED, "交易码'" + txNo + ":" + version + "'未发现");
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
                    asyncHandler.fail(InterfaceRspCode.NOT_SUPPORTED_SIGN_VERIFY_ALGORITHM, "不支持的签字算法" + definition.getSignAlgorithm());
                    return false;
                }
                if (definition.getEncryptAlgorithm() == null || definition.getEncryptAlgorithm().isEmpty()) {

                } else if ("AES".equals(definition.getEncryptAlgorithm())) {
                    String data = AES.encrypt(password, apiRequest.getData());
                    apiRequest.setData(data);
                } else {
                    asyncHandler.fail(InterfaceRspCode.NOT_SUPPORTED_ENCRYPT_DECRYPT_ALGORITHM, "不支持的加密算法" + definition.getEncryptAlgorithm());
                    return false;
                }
            } else {

                if (definition.getEncryptAlgorithm() == null || definition.getEncryptAlgorithm().isEmpty()) {

                } else if ("AES".equals(definition.getEncryptAlgorithm())) {
                    String data = AES.encrypt(password, apiRequest.getData());
                    apiRequest.setData(data);
                } else {
                    asyncHandler.fail(InterfaceRspCode.NOT_SUPPORTED_ENCRYPT_DECRYPT_ALGORITHM, "不支持的加密算法" + definition.getEncryptAlgorithm());
                    return false;
                }
                if (definition.getSignAlgorithm() == null || definition.getSignAlgorithm().isEmpty()) {

                } else if ("SHA512".equals(definition.getSignAlgorithm())) {
                    String sign = SHA.SHA512(apiRequest.getData() + password);
                    apiRequest.setSign(sign);
                } else {
                    asyncHandler.fail(InterfaceRspCode.NOT_SUPPORTED_SIGN_VERIFY_ALGORITHM, "不支持的签字算法" + definition.getSignAlgorithm());
                    return false;
                }
            }
        } else {
            apiRequest.setChannel("public");
        }
        if (serviceConfigure.isDebug()) {
            serviceConfigure.log("{} sessionId[{}] async call ApiRequest:{} ", DateUtil.getDate(), serviceConfigure.getSessionId(), apiRequest);
        }
        ApiResponse apiResponse = ServiceClient.call(serviceConfigure, url, apiRequest);
        if (serviceConfigure.isDebug()) {
            serviceConfigure.log("{} sessionId[{}] async call ApiResponse:{} ", DateUtil.getDate(), serviceConfigure.getSessionId(), apiResponse);
        }
        if (InterfaceRspCode.valueOfCode(apiResponse.getCode()) != InterfaceRspCode.SUCCESS) {
            if (serviceConfigure.isDebug()) {
                serviceConfigure.log("{} sessionId[{}] async call result , code:'{}' desc:'{}' ", DateUtil.getDate(), serviceConfigure.getSessionId(), apiResponse.getCode(), apiResponse.getDesc());
            }
            asyncHandler.fail(apiResponse.getCode(), apiResponse.getDesc(), "远程服务调用失败");
            return false;
        }
        String data = apiResponse.getData();
        if (data == null || data.isEmpty()) {
            if (serviceConfigure.isDebug()) {
                serviceConfigure.log("{} sessionId[{}] async call response data is null ", DateUtil.getDate(), serviceConfigure.getSessionId());
            }
            asyncHandler.fail(InterfaceRspCode.RESPONSE_DATA_IS_NULL, "返回业务数据为空");
            return false;
        }
        if (serviceClass != PublishService.class) {
            InterfaceDefinition definition = ServiceRegistry.lookupDefinition(txNo, version);
            if (definition.isFirstVerifySecondDecrypt()) {
                if (definition.getVerifyAlgorithm() == null || definition.getVerifyAlgorithm().isEmpty()) {

                } else if ("SHA512".equals(definition.getVerifyAlgorithm())) {
                    String sign = SHA.SHA512(apiResponse.getData() + password);
                    if (!sign.equals(apiResponse.getSign())) {
                        asyncHandler.fail(InterfaceRspCode.SIGN_ILLEGAL, "签字信息无效");
                        return false;
                    }
                } else {
                    asyncHandler.fail(InterfaceRspCode.NOT_SUPPORTED_SIGN_VERIFY_ALGORITHM, "不支持的校验算法" + definition.getVerifyAlgorithm());
                    return false;
                }

                if (definition.getDecryptAlgorithm() == null || definition.getDecryptAlgorithm().isEmpty()) {

                } else if ("AES".equals(definition.getDecryptAlgorithm())) {
                    String data0 = AES.decrypt(password, apiResponse.getData());
                    apiResponse.setData(data0);
                } else {
                    asyncHandler.fail(InterfaceRspCode.NOT_SUPPORTED_ENCRYPT_DECRYPT_ALGORITHM, "不支持的解密算法" + definition.getDecryptAlgorithm());
                    return false;
                }
            } else {
                if (definition.getDecryptAlgorithm() == null || definition.getDecryptAlgorithm().isEmpty()) {

                } else if ("AES".equals(definition.getDecryptAlgorithm())) {
                    String data0 = AES.decrypt(password, apiResponse.getData());
                    apiResponse.setData(data0);
                } else {
                    asyncHandler.fail(InterfaceRspCode.NOT_SUPPORTED_ENCRYPT_DECRYPT_ALGORITHM, "不支持的解密算法" + definition.getDecryptAlgorithm());
                    return false;
                }
                if (definition.getVerifyAlgorithm() == null || definition.getVerifyAlgorithm().isEmpty()) {

                } else if ("SHA512".equals(definition.getVerifyAlgorithm())) {
                    String sign = SHA.SHA512(apiResponse.getData() + password);
                    if (!sign.equals(apiResponse.getSign())) {
                        if (!sign.equals(apiResponse.getSign())) {
                            asyncHandler.fail(InterfaceRspCode.SIGN_ILLEGAL, "签字信息无效");
                            return false;
                        }
                    }
                } else {
                    asyncHandler.fail(InterfaceRspCode.NOT_SUPPORTED_SIGN_VERIFY_ALGORITHM, "不支持的校验算法" + definition.getVerifyAlgorithm());
                    return false;
                }
            }
        }
        Object response = null;
        try {
            response = GSON.fromJson(apiResponse.getData(), responseClass);
            if (response instanceof TokenReadable) {//如果有实现Token获取接口，则设置Token值
                TokenReadable tokenReadable = (TokenReadable) response;
                serviceConfigure.setToken(tokenReadable.getToken());
            }
        } catch (JsonSyntaxException e) {
            if (serviceConfigure.isDebug()) {
                serviceConfigure.log("{} sessionId[{}] async call response data json syntax is illegal, json: '{}' ", DateUtil.getDate(), serviceConfigure.getSessionId(), apiResponse.getData());
            }
            asyncHandler.fail(InterfaceRspCode.INVALID_COMMUNICATION_MESSAGE, "无效的通信报文");
            return false;
        } catch (Exception e) {
            if (serviceConfigure.isDebug()) {
                serviceConfigure.log("{} sessionId[{}] async call response data happens unknown error! json: '{}' ", DateUtil.getDate(), serviceConfigure.getSessionId(), apiResponse.getData());
            }
            try {
                asyncHandler.exception(e);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return false;
        }
        try {
            if (serviceConfigure.isDebug()) {
                serviceConfigure.log("{} sessionId[{}] async call success! json: '{}' ", DateUtil.getDate(), serviceConfigure.getSessionId(), apiResponse.getData());
            }
            asyncHandler.success(response);
            return true;
        } catch (Throwable e) {
            try {
                asyncHandler.exception(e);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return false;
        }
    }
}
