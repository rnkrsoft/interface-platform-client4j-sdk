package com.rnkrsoft.platform.client.invoker;

import com.rnkrsoft.com.google.gson.Gson;
import com.rnkrsoft.com.google.gson.GsonBuilder;
import com.rnkrsoft.com.google.gson.JsonSyntaxException;
import com.rnkrsoft.message.MessageFormatter;
import com.rnkrsoft.platform.client.InterfaceMetadata;
import com.rnkrsoft.platform.client.InterfaceSetting;
import com.rnkrsoft.platform.client.ServiceConfigure;
import com.rnkrsoft.platform.client.ServiceFactory;
import com.rnkrsoft.platform.client.connector.InterfaceConnector;
import com.rnkrsoft.platform.client.exception.InterfaceConnectorNotFoundException;
import com.rnkrsoft.platform.client.exception.RemoteInterfaceExecutionException;
import com.rnkrsoft.platform.client.exception.UnsupportedAlgorithmException;
import com.rnkrsoft.platform.client.logger.Logger;
import com.rnkrsoft.platform.client.logger.LoggerFactory;
import com.rnkrsoft.platform.protocol.ApiRequest;
import com.rnkrsoft.platform.protocol.ApiResponse;
import com.rnkrsoft.platform.protocol.TokenReadable;
import com.rnkrsoft.platform.protocol.enums.InterfaceRspCode;
import com.rnkrsoft.platform.protocol.service.InterfaceDefinition;
import com.rnkrsoft.platform.protocol.service.PublishService;
import com.rnkrsoft.security.SHA;
import com.rnkrsoft.utils.DateUtils;

/**
 * Created by rnkrsoft.com on 2019/1/17.
 * 同步执行器
 */
public class SyncInvoker {
    static Logger log = LoggerFactory.getLogger(SyncInvoker.class);
    private static final Gson GSON = new GsonBuilder().setDateFormat("yyyyMMddHHmmss").create();
    public static final String FETCH_PUBLISH_TX_NO = "000";
    public static final String SHA512 = "SHA512";
    public static final String AES = "AES";
    public static final String PUBLIC_CHANNEL = "public";

    /**
     * 调用接口
     *
     * @param serviceFactory 服务配置
     * @param service        服务类
     * @param methodName     方法名
     * @param requestClass   请求类
     * @param responseClass  应答类
     * @param request        请求对象
     * @return 应答对象
     */
    public Object call(ServiceFactory serviceFactory, Class service, String methodName, Class requestClass, Class responseClass, Object request) {
        ServiceConfigure serviceConfigure = serviceFactory.getServiceConfigure();
        String channel;
        String txNo;
        String version;
        //如果不是发布服务，则寻找服务类对应的交易码和版本号
        if (service == PublishService.class) {
            channel = "public";
            txNo = FETCH_PUBLISH_TX_NO;
            version = "1";
        } else {
            InterfaceMetadata interfaceMetadata = serviceFactory.getMetadataRegister().lookup(service.getName(), methodName);
            channel = interfaceMetadata.getChannel();
            txNo = interfaceMetadata.getTxNo();
            version = interfaceMetadata.getVersion();
        }
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setChannel(channel);
        apiRequest.setTxNo(txNo);
        apiRequest.setVersion(version);
        apiRequest.setSessionId(log.getSessionId());
        apiRequest.setUic(serviceConfigure.getUic());
        apiRequest.setUid(serviceConfigure.getUid());
        apiRequest.setToken(serviceConfigure.getToken());
        apiRequest.setTimestamp(DateUtils.getTimestamp());
        String plainText = GSON.toJson(request);
        apiRequest.setData(plainText);
        log.debug("async call channel:'{}' txNo:'{}' version:'{}' ", channel, txNo, version);
        String password = null;
        InterfaceSetting.InterfaceSettingBuilder settingBuilder = InterfaceSetting.builder();
        if (service != PublishService.class) {
            InterfaceDefinition interfaceDefinition = serviceFactory.getDefinitionRegister().lookup(channel, txNo, version);
            if (apiRequest.getTxNo() == null || apiRequest.getTxNo().isEmpty()) {
                throw new IllegalArgumentException("txNo is blank!");
            }
            if (apiRequest.getVersion() == null || apiRequest.getVersion().isEmpty()) {
                throw new IllegalArgumentException("version is blank!");
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
                    ;
                } else if (SHA512.equals(interfaceDefinition.getSignAlgorithm())) {
                    //将会话ID和时间戳作为待签字文本的一部分，防止报文重发
                    String sign = SHA.SHA512(apiRequest.getData() + apiRequest.getSessionId() + apiRequest.getTimestamp() + password);
                    apiRequest.setSign(sign);
                } else {
                    log.error("sign algorithm '{}' is unsupported!", interfaceDefinition.getSignAlgorithm());
                    throw new UnsupportedAlgorithmException("sign algorithm '" + interfaceDefinition.getSignAlgorithm() + "' is unsupported!");
                }
                if (interfaceDefinition.getEncryptAlgorithm() == null || interfaceDefinition.getEncryptAlgorithm().isEmpty()) {
                    ;
                } else if (AES.equals(interfaceDefinition.getEncryptAlgorithm())) {
                    if (password == null) {
                        throw new NullPointerException("password is null!");
                    }
                    String data = com.rnkrsoft.security.AES.encrypt(password, serviceConfigure.getKeyVector(), apiRequest.getData());
                    apiRequest.setData(data);
                } else {
                    log.error("encrypt algorithm '{}' is unsupported!", interfaceDefinition.getEncryptAlgorithm());
                    throw new UnsupportedAlgorithmException("encrypt algorithm '" + interfaceDefinition.getVerifyAlgorithm() + "' is unsupported!");
                }
            } else {
                log.debug("encrypt --> sign");
                if (interfaceDefinition.getEncryptAlgorithm() == null || interfaceDefinition.getEncryptAlgorithm().isEmpty()) {
                    ;
                } else if (AES.equals(interfaceDefinition.getEncryptAlgorithm())) {
                    if (password == null) {
                        throw new NullPointerException("password is null!");
                    }
                    String data = com.rnkrsoft.security.AES.encrypt(password, serviceConfigure.getKeyVector(), apiRequest.getData());
                    apiRequest.setData(data);
                } else {
                    log.error("encrypt algorithm '{}' is unsupported!", interfaceDefinition.getEncryptAlgorithm());
                    throw new UnsupportedAlgorithmException("encrypt algorithm '" + interfaceDefinition.getVerifyAlgorithm() + "' is unsupported!");
                }
                if (interfaceDefinition.getSignAlgorithm() == null || interfaceDefinition.getSignAlgorithm().isEmpty()) {
                    ;
                } else if (SHA512.equals(interfaceDefinition.getSignAlgorithm())) {
                    //将会话ID和时间戳作为待签字文本的一部分，防止报文重发
                    String sign = SHA.SHA512(apiRequest.getData() + apiRequest.getSessionId() + apiRequest.getTimestamp() + password);
                    apiRequest.setSign(sign);
                } else {
                    log.error("sign algorithm '{}' is unsupported!", interfaceDefinition.getSignAlgorithm());
                    throw new UnsupportedAlgorithmException("sign algorithm '" + interfaceDefinition.getSignAlgorithm() + "' is unsupported!");
                }
            }
        } else {
            apiRequest.setChannel(PUBLIC_CHANNEL);
            settingBuilder.httpConnectTimeoutSecond(10);
            settingBuilder.httpReadTimeoutSecond(10);
        }
        log.debug("sync call ApiRequest:{} ", apiRequest);
        InterfaceConnector interfaceConnector = serviceFactory.getInterfaceConnector();
        if (interfaceConnector == null) {
            log.error("interface connector is not config!");
            throw new InterfaceConnectorNotFoundException("interface connector is not config!");
        }
        ApiResponse apiResponse = interfaceConnector.call(apiRequest, settingBuilder.build());
        log.debug("sync call ApiResponse:{} ", apiResponse);
        if (apiResponse == null) {
            log.error("sync call result is null!");
            throw new RuntimeException("sync call result is null!");
        }
        if (InterfaceRspCode.valueOfCode(apiResponse.getCode()) != InterfaceRspCode.SUCCESS) {
            log.error("sync call is failure , code:'{}' desc:'{}' ", apiResponse.getCode(), apiResponse.getDesc());
            throw new RemoteInterfaceExecutionException(MessageFormatter.format("sync call is failure, cause {}({})!", apiResponse.getDesc(), apiResponse.getCode()));
        }
        //解密内容
        String plainTextData = apiResponse.getData();
        if (plainTextData == null || plainTextData.isEmpty()) {
            log.error("sync call response data is null ");
            throw new NullPointerException("data is null");
        }
        if (service != PublishService.class) {
            InterfaceDefinition interfaceDefinition = serviceFactory.getDefinitionRegister().lookup(channel, txNo, version);
            if (interfaceDefinition.isFirstVerifySecondDecrypt()) {
                log.debug("verify --> decrypt");
                if (interfaceDefinition.getVerifyAlgorithm() == null || interfaceDefinition.getVerifyAlgorithm().isEmpty()) {
                } else if (SHA512.equals(interfaceDefinition.getVerifyAlgorithm())) {
                    String sign = SHA.SHA512(apiResponse.getData() + apiRequest.getSessionId() + apiRequest.getTimestamp() + password);
                    if (!sign.equals(apiResponse.getSign())) {
                        log.debug("data: '{}', sign: '{}', calc sign: '{}'", apiResponse.getData(), apiResponse.getSign(), sign);
                        throw new IllegalArgumentException("illegal sign!");
                    }
                } else {
                    log.error("verify algorithm '{}' is unsupported!", interfaceDefinition.getVerifyAlgorithm());
                    throw new UnsupportedAlgorithmException("unsupported verify algorithm " + interfaceDefinition.getVerifyAlgorithm());
                }

                if (interfaceDefinition.getDecryptAlgorithm() == null || interfaceDefinition.getDecryptAlgorithm().isEmpty()) {
                    //nothing
                } else if (AES.equals(interfaceDefinition.getDecryptAlgorithm())) {
                    plainTextData = com.rnkrsoft.security.AES.decrypt(password, serviceConfigure.getKeyVector(), apiResponse.getData());
                } else {
                    log.error("decrypt algorithm '{}' is unsupported!", interfaceDefinition.getDecryptAlgorithm());
                    throw new UnsupportedAlgorithmException("unsupported decrypt algorithm " + interfaceDefinition.getDecryptAlgorithm());
                }
            } else {
                log.debug("decrypt --> verify");
                if (interfaceDefinition.getDecryptAlgorithm() == null || interfaceDefinition.getDecryptAlgorithm().isEmpty()) {
                    //nothing
                } else if (AES.equals(interfaceDefinition.getDecryptAlgorithm())) {
                    plainTextData = com.rnkrsoft.security.AES.decrypt(password, serviceConfigure.getKeyVector(), apiResponse.getData());
                } else {
                    log.error("decrypt algorithm '{}' is unsupported!", interfaceDefinition.getDecryptAlgorithm());
                    throw new UnsupportedAlgorithmException("unsupported decrypt algorithm " + interfaceDefinition.getDecryptAlgorithm());
                }
                if (interfaceDefinition.getVerifyAlgorithm() == null || interfaceDefinition.getVerifyAlgorithm().isEmpty()) {

                } else if (SHA512.equals(interfaceDefinition.getVerifyAlgorithm())) {
                    String sign = SHA.SHA512(plainTextData + apiRequest.getSessionId() + apiRequest.getTimestamp() + password);
                    if (!sign.equals(apiResponse.getSign())) {
                        log.debug("data: '{}', sign: '{}', calc sign: '{}'", plainTextData, apiResponse.getSign(), sign);
                        throw new IllegalArgumentException("illegal sign!");
                    }
                } else {
                    log.error("verify algorithm '{}' is unsupported!", interfaceDefinition.getVerifyAlgorithm());
                    throw new UnsupportedAlgorithmException("unsupported verify algorithm " + interfaceDefinition.getVerifyAlgorithm());
                }
            }
        }
        if (plainTextData == null || plainTextData.isEmpty()) {
            throw new NullPointerException("data is null");
        }
        Object response = null;
        try {
            response = GSON.fromJson(plainTextData, responseClass);
            if (response instanceof TokenReadable) {//如果有实现Token获取接口，则设置Token值
                TokenReadable tokenReadable = (TokenReadable) response;
                serviceConfigure.setToken(tokenReadable.getToken());
                log.info("resetting token '{}'!", tokenReadable.getToken());
            }
            return response;
        } catch (JsonSyntaxException e) {
            log.error("sync call response data json syntax is illegal, json: '{}' ", apiResponse.getData());
            throw new RemoteInterfaceExecutionException("sync call response data json syntax is illegal！");
        } catch (Exception e) {
            log.error(MessageFormatter.format("sync call response data happens unknown error! json: '{}' ", apiResponse.getData()), e);
            throw new RemoteInterfaceExecutionException("sync call response data happens unknown error!");
        }
    }
}
