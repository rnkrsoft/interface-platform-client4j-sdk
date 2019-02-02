package com.rnkrsoft.platform.client.invoker;


import com.rnkrsoft.com.google.gson.Gson;
import com.rnkrsoft.com.google.gson.GsonBuilder;
import com.rnkrsoft.com.google.gson.JsonSyntaxException;
import com.rnkrsoft.platform.client.InterfaceMetadata;
import com.rnkrsoft.platform.client.InterfaceSetting;
import com.rnkrsoft.platform.client.ServiceConfigure;
import com.rnkrsoft.platform.client.ServiceFactory;
import com.rnkrsoft.platform.client.async.AsyncTask;
import com.rnkrsoft.platform.client.connector.InterfaceConnector;
import com.rnkrsoft.platform.client.logger.Logger;
import com.rnkrsoft.platform.client.logger.LoggerFactory;
import com.rnkrsoft.platform.protocol.*;
import com.rnkrsoft.platform.protocol.enums.InterfaceRspCode;
import com.rnkrsoft.platform.protocol.service.InterfaceDefinition;
import com.rnkrsoft.platform.protocol.service.PublishService;
import com.rnkrsoft.platform.protocol.utils.JavaEnvironmentDetector;
import com.rnkrsoft.security.SHA;
import com.rnkrsoft.utils.DateUtils;

/**
 * Created by rnkrsoft.com on 2019/1/17.
 */
public class JavaAsyncInvoker<Request> extends AsyncTask<Request, Void, ApiResponse> {
    static Logger log = LoggerFactory.getLogger(JavaAsyncInvoker.class);
    final static Gson GSON = new GsonBuilder().serializeNulls().setDateFormat("yyyyMMddHHmmss").create();
    public static final String FETCH_PUBLISH_TX_NO = "000";
    public static final String SHA512 = "SHA512";
    public static final String AES = "AES";
    public static final String PUBLIC_CHANNEL = "public";
    ServiceFactory serviceFactory;
    /**
     * 会话号
     */
    String sessionId;

    Class service;
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
     * 异步处理器实例
     */
    AsyncHandler asyncHandler;

    public JavaAsyncInvoker(ServiceFactory serviceFactory, String sessionId, Class service, String methodName, Class requestClass, Class responseClass, AsyncHandler asyncHandler) {
        this.serviceFactory = serviceFactory;
        this.sessionId = sessionId;
        this.service = service;
        this.methodName = methodName;
        this.requestClass = requestClass;
        this.responseClass = responseClass;
        this.asyncHandler = asyncHandler;
    }

    @Override
    protected ApiResponse doInBackground(Request... requests) {
        log.setSessionId(sessionId);
        ServiceConfigure serviceConfigure = serviceFactory.getServiceConfigure();
        String channel;
        String txNo;
        String version;
        //如果不是发布服务，则寻找服务类对应的交易码和版本号
        ApiResponse apiResponse = new ApiResponse();
        if (service == PublishService.class) {
            channel = "public";
            txNo = FETCH_PUBLISH_TX_NO;
            version = "1";
        } else {
            InterfaceMetadata interfaceMetadata = serviceFactory.getMetadataRegister().lookup(service.getName(), methodName, true);
            if (interfaceMetadata == null) {
                apiResponse.setCode(InterfaceRspCode.INTERFACE_NOT_DEFINED);
                return apiResponse;
            }
            channel = interfaceMetadata.getChannel();
            txNo = interfaceMetadata.getTxNo();
            version = interfaceMetadata.getVersion();
        }
        log.debug("asynchronous call channel:'{}' txNo:'{}' version:{} ", channel, txNo, version);
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setChannel(channel);
        apiRequest.setTxNo(txNo);
        apiRequest.setVersion(version);
        apiRequest.setSessionId(sessionId);
        apiRequest.setUic(serviceConfigure.getUic());
        apiRequest.setUid(serviceConfigure.getUid());
        apiRequest.setToken(serviceConfigure.getToken());
        apiRequest.setTimestamp(DateUtils.getTimestamp());
        String plainText = GSON.toJson(requests[0]);
        apiRequest.setData(plainText);
        String password = null;
        InterfaceSetting.InterfaceSettingBuilder settingBuilder = InterfaceSetting.builder();
        if (service != PublishService.class) {
            InterfaceDefinition interfaceDefinition = serviceFactory.getDefinitionRegister().lookup(channel, txNo, version, true);
            if (interfaceDefinition == null) {
                apiResponse.setCode(InterfaceRspCode.INTERFACE_NOT_DEFINED);
                return apiResponse;
            }
            if (apiRequest.getTxNo() == null || apiRequest.getTxNo().isEmpty()) {
                log.error("txNo is blank!");
                apiResponse.setCode(InterfaceRspCode.TX_NO_IS_NULL);
                return apiResponse;
            }
            if (apiRequest.getVersion() == null || apiRequest.getVersion().isEmpty()) {
                log.error("version is blank!");
                apiResponse.setCode(InterfaceRspCode.VERSION_ILLEGAL);
                return apiResponse;
            }
            if (interfaceDefinition.isUseTokenAsPassword()) {
                password = serviceConfigure.getToken();
                log.debug("use token as password, '{}'", password);
            } else {
                password = serviceConfigure.getPassword();
                log.debug("use fixed string as password, '{}'", password);
            }
            if (password == null) {
                log.debug("password is null!");
            }
            if (interfaceDefinition.isFirstSignSecondEncrypt()) {
                log.debug("sign --> encrypt");
                if (interfaceDefinition.getSignAlgorithm() == null || interfaceDefinition.getSignAlgorithm().isEmpty()) {

                } else if (SHA512.equals(interfaceDefinition.getSignAlgorithm())) {
                    String sign = SHA.SHA512(apiRequest.getData() + apiRequest.getSessionId() + apiRequest.getTimestamp() + password);
                    apiRequest.setSign(sign);
                } else {
                    log.error("sign algorithm '{}' is unsupported!", interfaceDefinition.getSignAlgorithm());
                    apiResponse.setCode(InterfaceRspCode.NOT_SUPPORTED_SIGN_VERIFY_ALGORITHM);
                    return apiResponse;
                }
                if (interfaceDefinition.getEncryptAlgorithm() == null || interfaceDefinition.getEncryptAlgorithm().isEmpty()) {

                } else if (AES.equals(interfaceDefinition.getEncryptAlgorithm())) {
                    try {
                        String data = com.rnkrsoft.security.AES.encrypt(password, serviceConfigure.getKeyVector(), apiRequest.getData());
                        apiRequest.setData(data);
                    } catch (Exception e) {
                        apiResponse.setCode(InterfaceRspCode.ENCRYPT_HAPPENS_FAIL);
                        return apiResponse;
                    }
                } else {
                    log.error("encrypt algorithm '{}' is unsupported!", interfaceDefinition.getEncryptAlgorithm());
                    apiResponse.setCode(InterfaceRspCode.NOT_SUPPORTED_ENCRYPT_DECRYPT_ALGORITHM);
                    return apiResponse;
                }
            } else {
                log.debug("encrypt --> sign");
                if (interfaceDefinition.getEncryptAlgorithm() == null || interfaceDefinition.getEncryptAlgorithm().isEmpty()) {

                } else if (AES.equals(interfaceDefinition.getEncryptAlgorithm())) {
                    try {
                        String data = com.rnkrsoft.security.AES.encrypt(password, serviceConfigure.getKeyVector(), apiRequest.getData());
                        apiRequest.setData(data);
                    } catch (Exception e) {
                        apiResponse.setCode(InterfaceRspCode.ENCRYPT_HAPPENS_FAIL);
                        return apiResponse;
                    }
                } else {
                    log.error("encrypt algorithm '{}' is unsupported!", interfaceDefinition.getEncryptAlgorithm());
                    apiResponse.setCode(InterfaceRspCode.NOT_SUPPORTED_ENCRYPT_DECRYPT_ALGORITHM);
                    return apiResponse;
                }
                if (interfaceDefinition.getSignAlgorithm() == null || interfaceDefinition.getSignAlgorithm().isEmpty()) {

                } else if (SHA512.equals(interfaceDefinition.getSignAlgorithm())) {
                    String sign = SHA.SHA512(apiRequest.getData() + apiRequest.getSessionId() + apiRequest.getTimestamp() + password);
                    apiRequest.setSign(sign);
                } else {
                    log.error("sign algorithm '{}' is unsupported!", interfaceDefinition.getSignAlgorithm());
                    apiResponse.setCode(InterfaceRspCode.NOT_SUPPORTED_SIGN_VERIFY_ALGORITHM);
                    return apiResponse;
                }
            }
        } else {
            apiRequest.setChannel(PUBLIC_CHANNEL);
            settingBuilder.httpConnectTimeoutSecond(5);
            settingBuilder.httpReadTimeoutSecond(5);
        }

        log.debug("asynchronous call ApiRequest:{} ", apiRequest);
        InterfaceConnector interfaceConnector = serviceFactory.getInterfaceConnector();
        if (interfaceConnector == null) {
            log.error("interface connector is not config!");
            apiResponse.setCode(InterfaceRspCode.INTERFACE_CONNECTOR_IS_NOT_CONFIG);
            return apiResponse;
        }
        apiResponse = interfaceConnector.call(apiRequest, settingBuilder.build());
        log.debug("asynchronous call ApiResponse:{} ", apiResponse);
        String data = apiResponse.getData();
        if (data == null || data.isEmpty()) {
            if (!"000".equals(apiResponse.getCode())) {
                return apiResponse;
            } else {
                log.debug("asynchronous call response data is null ");
                return apiResponse;
            }
        }
        if (service != PublishService.class) {
            InterfaceDefinition interfaceDefinition = serviceFactory.getDefinitionRegister().lookup(channel, txNo, version);
            if (interfaceDefinition.isFirstVerifySecondDecrypt()) {
                log.debug("verify --> decrypt");
                if (interfaceDefinition.getVerifyAlgorithm() == null || interfaceDefinition.getVerifyAlgorithm().isEmpty()) {

                } else if (SHA512.equals(interfaceDefinition.getVerifyAlgorithm())) {
                    String sign = SHA.SHA512(apiResponse.getData() + apiRequest.getSessionId() + apiRequest.getTimestamp() + password);
                    if (!sign.equals(apiResponse.getSign())) {
                        log.error("illegal sign!");
                        apiResponse.setCode(InterfaceRspCode.SIGN_ILLEGAL);
                        return apiResponse;
                    }
                } else {
                    log.error("verify algorithm '{}' is unsupported!", interfaceDefinition.getVerifyAlgorithm());
                    apiResponse.setCode(InterfaceRspCode.NOT_SUPPORTED_SIGN_VERIFY_ALGORITHM);
                    return apiResponse;
                }

                if (interfaceDefinition.getDecryptAlgorithm() == null || interfaceDefinition.getDecryptAlgorithm().isEmpty()) {

                } else if (AES.equals(interfaceDefinition.getDecryptAlgorithm())) {
                    try {
                        String data0 = com.rnkrsoft.security.AES.decrypt(password, serviceConfigure.getKeyVector(), apiResponse.getData());
                        apiResponse.setData(data0);
                    } catch (Exception e) {
                        apiResponse.setCode(InterfaceRspCode.DECRYPT_HAPPENS_FAIL);
                        return apiResponse;
                    }
                } else {
                    log.error("decrypt algorithm '{}' is unsupported!", interfaceDefinition.getDecryptAlgorithm());
                    apiResponse.setCode(InterfaceRspCode.NOT_SUPPORTED_ENCRYPT_DECRYPT_ALGORITHM);
                    return apiResponse;
                }
            } else {
                log.debug("decrypt --> verify");
                if (interfaceDefinition.getDecryptAlgorithm() == null || interfaceDefinition.getDecryptAlgorithm().isEmpty()) {

                } else if (AES.equals(interfaceDefinition.getDecryptAlgorithm())) {
                    try {
                        String data0 = com.rnkrsoft.security.AES.decrypt(password, serviceConfigure.getKeyVector(), apiResponse.getData());
                        apiResponse.setData(data0);
                    } catch (Exception e) {
                        apiResponse.setCode(InterfaceRspCode.DECRYPT_HAPPENS_FAIL);
                        return apiResponse;
                    }
                } else {
                    log.error("decrypt algorithm '{}' is unsupported!", interfaceDefinition.getDecryptAlgorithm());
                    apiResponse.setCode(InterfaceRspCode.NOT_SUPPORTED_ENCRYPT_DECRYPT_ALGORITHM);
                    return apiResponse;
                }
                if (interfaceDefinition.getVerifyAlgorithm() == null || interfaceDefinition.getVerifyAlgorithm().isEmpty()) {

                } else if (SHA512.equals(interfaceDefinition.getVerifyAlgorithm())) {
                    String sign = SHA.SHA512(apiResponse.getData() + apiRequest.getSessionId() + apiRequest.getTimestamp() + password);
                    if (!sign.equals(apiResponse.getSign())) {
                        if (!sign.equals(apiResponse.getSign())) {
                            log.error("illegal sign!");
                            apiResponse.setCode(InterfaceRspCode.SIGN_ILLEGAL);
                            return apiResponse;
                        }
                    }
                } else {
                    log.error("verify algorithm '{}' is unsupported!", interfaceDefinition.getVerifyAlgorithm());
                    apiResponse.setCode(InterfaceRspCode.NOT_SUPPORTED_SIGN_VERIFY_ALGORITHM);
                    return apiResponse;
                }
            }
        }
        return apiResponse;
    }

    protected void onPostExecute(ApiResponse apiResponse) {
        log.debug("asynchronous execute remote service response '{}'", apiResponse);
        ServiceConfigure serviceConfigure = serviceFactory.getServiceConfigure();
        //通信层错误
        if (InterfaceRspCode.valueOfCode(apiResponse.getCode()) != InterfaceRspCode.SUCCESS) {
            log.debug("asynchronous call result , code:'{}' desc:'{}' ", apiResponse.getCode(), apiResponse.getDesc());
            String code = apiResponse.getCode();
            String desc = apiResponse.getDesc();
            if (JavaEnvironmentDetector.isAndroid()) {
                if (InterfaceRspCode.TIMESTAMP_ILLEGAL.getCode().equals(code)) {
                    desc = "手机" + apiResponse.getDesc();
                }
            }
            asyncHandler.fail(code, desc, "远程服务调用失败");
            return;
        }
        if (apiResponse.getData() == null || apiResponse.getData().isEmpty()) {
            log.debug("asynchronous call response data is null ");
            asyncHandler.fail(InterfaceRspCode.RESPONSE_DATA_IS_NULL.getCode(), InterfaceRspCode.RESPONSE_DATA_IS_NULL.getDesc(), "");
            return;
        }
        Object response = null;
        try {
            response = GSON.fromJson(apiResponse.getData(), responseClass);
            if (response instanceof TokenReadable || response instanceof TokenAble) {//如果有实现Token获取接口，则设置Token值
                TokenReadable tokenReadable = (TokenReadable) response;
                serviceConfigure.setToken(tokenReadable.getToken());
            }
        } catch (JsonSyntaxException e) {
            log.debug("asynchronous call response data json syntax is illegal, json: '{}' ", apiResponse.getData());
            asyncHandler.fail(InterfaceRspCode.INVALID_COMMUNICATION_MESSAGE, "无效的通信报文");
            return;
        } catch (Exception e) {
            log.debug("asynchronous call response data happens unknown error! json: '{}' ", apiResponse.getData());
            try {
                asyncHandler.exception(e);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return;
        }
        try {
            log.debug("asynchronous call success callback, json: '{}' ", apiResponse.getData());
            asyncHandler.success(response);
        } catch (Throwable e) {
            try {
                asyncHandler.exception(e);
            } catch (Throwable throwable) {
                log.error("asynchronous call success, but happens error! json: '{}', error:'{}' ", apiResponse.getData(), throwable);
            }
        }
    }
}
