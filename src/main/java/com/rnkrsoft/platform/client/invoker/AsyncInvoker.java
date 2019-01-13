/**
 * RNKRSOFT OPEN SOURCE SOFTWARE LICENSE TERMS ver.1
 * - 氡氪网络科技(重庆)有限公司 开源软件许可条款(版本1)
 * 氡氪网络科技(重庆)有限公司 以下简称Rnkrsoft。
 * 这些许可条款是 Rnkrsoft Corporation（或您所在地的其中一个关联公司）与您之间达成的协议。
 * 请阅读本条款。本条款适用于所有Rnkrsoft的开源软件项目，任何个人或企业禁止以下行为：
 * .禁止基于删除开源代码所附带的本协议内容、
 * .以非Rnkrsoft的名义发布Rnkrsoft开源代码或者基于Rnkrsoft开源源代码的二次开发代码到任何公共仓库,
 * 除非上述条款附带有其他条款。如果确实附带其他条款，则附加条款应适用。
 * <p/>
 * 使用该软件，即表示您接受这些条款。如果您不接受这些条款，请不要使用该软件。
 * 如下所述，安装或使用该软件也表示您同意在验证、自动下载和安装某些更新期间传输某些标准计算机信息以便获取基于 Internet 的服务。
 * <p/>
 * 如果您遵守这些许可条款，将拥有以下权利。
 * 1.阅读源代码和文档
 * 如果您是个人用户，则可以在任何个人设备上阅读、分析、研究Rnkrsoft开源源代码。
 * 如果您经营一家企业，则禁止在任何设备上阅读Rnkrsoft开源源代码,禁止分析、禁止研究Rnkrsoft开源源代码。
 * 2.编译源代码
 * 如果您是个人用户，可以对Rnkrsoft开源源代码以及修改后产生的源代码进行编译操作，编译产生的文件依然受本协议约束。
 * 如果您经营一家企业，不可以对Rnkrsoft开源源代码以及修改后产生的源代码进行编译操作。
 * 3.二次开发拓展功能
 * 如果您是个人用户，可以基于Rnkrsoft开源源代码进行二次开发，修改产生的元代码同样受本协议约束。
 * 如果您经营一家企业，不可以对Rnkrsoft开源源代码进行任何二次开发，但是可以通过联系Rnkrsoft进行商业授予权进行修改源代码。
 * 完整协议。本协议以及开源源代码附加协议，共同构成了Rnkrsoft开源软件的完整协议。
 * <p/>
 * 4.免责声明
 * 该软件按“原样”授予许可。 使用本文档的风险由您自己承担。Rnkrsoft 不提供任何明示的担保、保证或条件。
 * 5.版权声明
 * 本协议所对应的软件为 Rnkrsoft 所拥有的自主知识产权，如果基于本软件进行二次开发，在不改变本软件的任何组成部分的情况下的而二次开发源代码所属版权为贵公司所有。
 */
package com.rnkrsoft.platform.client.invoker;

import com.rnkrsoft.com.google.gson.Gson;
import com.rnkrsoft.com.google.gson.GsonBuilder;
import com.rnkrsoft.com.google.gson.JsonSyntaxException;
import com.rnkrsoft.platform.client.InterfaceSetting;
import com.rnkrsoft.platform.client.exception.InterfaceConnectorException;
import com.rnkrsoft.platform.protocol.AsyncHandler;
import com.rnkrsoft.platform.client.ServiceConfigure;
import com.rnkrsoft.platform.client.ServiceFactory;
import com.rnkrsoft.platform.client.ServiceRegistry;
import com.rnkrsoft.platform.client.connector.InterfaceConnector;
import com.rnkrsoft.platform.client.scanner.InterfaceMetadata;
import com.rnkrsoft.platform.client.utils.DateUtil;
import com.rnkrsoft.platform.client.utils.MessageFormatter;
import com.rnkrsoft.platform.protocol.ApiRequest;
import com.rnkrsoft.platform.protocol.ApiResponse;
import com.rnkrsoft.platform.protocol.enums.InterfaceRspCode;
import com.rnkrsoft.platform.protocol.TokenReadable;
import com.rnkrsoft.platform.protocol.service.InterfaceDefinition;
import com.rnkrsoft.platform.protocol.service.PublishService;
import com.rnkrsoft.security.SHA;

import java.util.concurrent.Callable;

/**
 * Created by rnkrsoft.com on 2018/7/4.
 * 异步执行器，使用线程池的方式返回执行句柄
 */
public class AsyncInvoker implements Callable<ApiResponse> {
    final static Gson GSON = new GsonBuilder().serializeNulls().setDateFormat("yyyyMMddHHmmss").create();
    public static final String FETCH_PUBLISH_TX_NO = "000";
    public static final String SHA512 = "SHA512";
    public static final String AES = "AES";
    public static final String PUBLIC_CHANNEL = "public";
    /**
     * 会话号
     */
    String sessionId;
    /**
     * 服务配置对象
     */
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

    public AsyncInvoker(String sessionId, ServiceConfigure serviceConfigure, Class serviceClass, String methodName, Class requestClass, Class responseClass, Object request, AsyncHandler asyncHandler) {
        this.sessionId = sessionId;
        this.serviceConfigure = serviceConfigure;
        this.serviceClass = serviceClass;
        this.methodName = methodName;
        this.requestClass = requestClass;
        this.responseClass = responseClass;
        this.request = request;
        this.asyncHandler = asyncHandler;
    }

    @Override
    public ApiResponse call() throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        serviceConfigure.setSessionId(sessionId);
        String channel = null;
        String txNo = null;
        String version = null;
        if (serviceClass != PublishService.class) {
            InterfaceMetadata metadata = ServiceRegistry.lookupMetadata(serviceClass.getName(), methodName);
            if (metadata == null) {
                apiResponse.setCode(InterfaceRspCode.INTERFACE_NOT_DEFINED);
                asyncHandler.fail(InterfaceRspCode.INTERFACE_NOT_DEFINED, "It is not found defined in local interface metadata ! ");
                return apiResponse;
            }
            channel = metadata.getChannel();
            txNo = metadata.getTxNo();
            version = metadata.getVersion();
        } else {
            channel = "public";
            txNo = FETCH_PUBLISH_TX_NO;//000作为接口发布接口
            version = "1";
        }
        if (serviceConfigure.isDebug()) {
            serviceConfigure.debug("async call channel:'{}' txNo:'{}' version:{} ", channel, txNo, version);
        }
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setChannel(channel);
        apiRequest.setTxNo(txNo);
        apiRequest.setVersion(version);
        apiRequest.setSessionId(serviceConfigure.getSessionId());
        apiRequest.setUic(serviceConfigure.getUic());
        apiRequest.setUid(serviceConfigure.getUid());
        apiRequest.setToken(serviceConfigure.getToken());
        apiRequest.setTimestamp(DateUtil.getTimestamp());
        String plainText = GSON.toJson(request);
        apiRequest.setData(plainText);
        String password = null;
        InterfaceSetting.InterfaceSettingBuilder settingBuilder = InterfaceSetting.builder();
        if (serviceClass != PublishService.class) {
            if (apiRequest.getTxNo() == null || apiRequest.getTxNo().isEmpty()) {
                apiResponse.setCode(InterfaceRspCode.NOT_SUPPORTED_SIGN_VERIFY_ALGORITHM);
                asyncHandler.fail(InterfaceRspCode.NOT_SUPPORTED_SIGN_VERIFY_ALGORITHM, MessageFormatter.format("交易码'{}'未配置", apiRequest.getTxNo()));
                return apiResponse;
            }
            if (apiRequest.getVersion() == null || apiRequest.getVersion().isEmpty()) {
                apiResponse.setCode(InterfaceRspCode.VERSION_ILLEGAL);
                asyncHandler.fail(InterfaceRspCode.VERSION_ILLEGAL, MessageFormatter.format("版本号'{}'未配置", apiRequest.getVersion()));
                return apiResponse;
            }
            InterfaceDefinition definition = ServiceRegistry.lookupDefinition(channel, txNo, version);
            if (definition == null) {
                apiResponse.setCode(InterfaceRspCode.INTERFACE_NOT_DEFINED);
                asyncHandler.fail(InterfaceRspCode.INTERFACE_NOT_DEFINED, MessageFormatter.format("通道'{}'下交易码'{}:{}'未发现,请检查是否填写了交易码和版本号", channel, txNo, version));
                return apiResponse;
            }
            if (definition.isUseTokenAsPassword()) {
                password = ServiceFactory.getServiceConfigure().getToken();
                if (serviceConfigure.isDebug()) {
                    serviceConfigure.debug("use token as password, '{}'", password);
                }
            } else {
                password = ServiceFactory.getServiceConfigure().getPassword();
                if (serviceConfigure.isDebug()) {
                    serviceConfigure.debug("use configure password, '{}'", password);
                }
            }
            if (definition.isFirstSignSecondEncrypt()) {
                if (definition.getSignAlgorithm() == null || definition.getSignAlgorithm().isEmpty()) {

                } else if (SHA512.equals(definition.getSignAlgorithm())) {
                    String sign = SHA.SHA512(apiRequest.getData() + apiRequest.getSessionId() + apiRequest.getTimestamp() + password);
                    apiRequest.setSign(sign);
                } else {
                    apiResponse.setCode(InterfaceRspCode.NOT_SUPPORTED_SIGN_VERIFY_ALGORITHM);
                    asyncHandler.fail(InterfaceRspCode.NOT_SUPPORTED_SIGN_VERIFY_ALGORITHM, "不支持的签字算法" + definition.getSignAlgorithm());
                    return apiResponse;
                }
                if (definition.getEncryptAlgorithm() == null || definition.getEncryptAlgorithm().isEmpty()) {

                } else if (AES.equals(definition.getEncryptAlgorithm())) {
                    try {
                        String data = com.rnkrsoft.security.AES.encrypt(password, serviceConfigure.getKeyVector(), apiRequest.getData());
                        apiRequest.setData(data);
                    } catch (Exception e) {
                        apiResponse.setCode(InterfaceRspCode.ENCRYPT_HAPPENS_FAIL);
                        asyncHandler.fail(InterfaceRspCode.ENCRYPT_HAPPENS_FAIL, "加密发生错误，使用 keyVector:" + serviceConfigure.getKeyVector() + ", password:" + password + ", encryptAlgorithm:" + definition.getEncryptAlgorithm());
                        return apiResponse;
                    }
                } else {
                    apiResponse.setCode(InterfaceRspCode.NOT_SUPPORTED_ENCRYPT_DECRYPT_ALGORITHM);
                    asyncHandler.fail(InterfaceRspCode.NOT_SUPPORTED_ENCRYPT_DECRYPT_ALGORITHM, "不支持的加密算法" + definition.getEncryptAlgorithm());
                    return apiResponse;
                }
            } else {
                if (definition.getEncryptAlgorithm() == null || definition.getEncryptAlgorithm().isEmpty()) {

                } else if (AES.equals(definition.getEncryptAlgorithm())) {
                    try {
                        String data = com.rnkrsoft.security.AES.encrypt(password, serviceConfigure.getKeyVector(), apiRequest.getData());
                        apiRequest.setData(data);
                    } catch (Exception e) {
                        apiResponse.setCode(InterfaceRspCode.ENCRYPT_HAPPENS_FAIL);
                        asyncHandler.fail(InterfaceRspCode.ENCRYPT_HAPPENS_FAIL, "加密发生错误，使用 keyVector:" + serviceConfigure.getKeyVector() + ", password:" + password + ", encryptAlgorithm:" + definition.getEncryptAlgorithm());
                        return apiResponse;
                    }
                } else {
                    apiResponse.setCode(InterfaceRspCode.NOT_SUPPORTED_ENCRYPT_DECRYPT_ALGORITHM);
                    asyncHandler.fail(InterfaceRspCode.NOT_SUPPORTED_ENCRYPT_DECRYPT_ALGORITHM, "不支持的加密算法" + definition.getEncryptAlgorithm());
                    return apiResponse;
                }
                if (definition.getSignAlgorithm() == null || definition.getSignAlgorithm().isEmpty()) {

                } else if (SHA512.equals(definition.getSignAlgorithm())) {
                    String sign = SHA.SHA512(apiRequest.getData() + apiRequest.getSessionId() + apiRequest.getTimestamp() + password);
                    apiRequest.setSign(sign);
                } else {
                    apiResponse.setCode(InterfaceRspCode.NOT_SUPPORTED_SIGN_VERIFY_ALGORITHM);
                    asyncHandler.fail(InterfaceRspCode.NOT_SUPPORTED_SIGN_VERIFY_ALGORITHM, "不支持的签字算法" + definition.getSignAlgorithm());
                    return apiResponse;
                }
            }
        } else {
            apiRequest.setChannel(PUBLIC_CHANNEL);
            settingBuilder.httpConnectTimeoutSecond(10);
            settingBuilder.httpReadTimeoutSecond(10);
        }
        if (serviceConfigure.isDebug()) {
            serviceConfigure.debug("async call ApiRequest:{} ", apiRequest);
        }
        InterfaceConnector interfaceConnector = serviceConfigure.getInterfaceConnector();
        if (interfaceConnector == null) {
            throw new InterfaceConnectorException("接口连接器无效");
        }
        apiResponse = interfaceConnector.call(apiRequest, settingBuilder.build());
        if (serviceConfigure.isDebug()) {
            serviceConfigure.debug("async call ApiResponse:{} ", apiResponse);
        }
        if (apiResponse == null) {
            return apiResponse;
        }
        if (InterfaceRspCode.valueOfCode(apiResponse.getCode()) != InterfaceRspCode.SUCCESS) {
            serviceConfigure.debug("async call result , code:'{}' desc:'{}' ", apiResponse.getCode(), apiResponse.getDesc());
            asyncHandler.fail(apiResponse.getCode(), apiResponse.getDesc(), "远程服务调用失败");
            return apiResponse;
        }
        //如果是成功的，没有data数据，则提示数据错误，正常情况至少是{}
        String data = apiResponse.getData();
        if (data == null || data.isEmpty()) {
            serviceConfigure.debug("async call response data is null ");
            apiResponse.setCode(InterfaceRspCode.RESPONSE_DATA_IS_NULL);
            asyncHandler.fail(InterfaceRspCode.RESPONSE_DATA_IS_NULL, "返回业务数据为空");
            return apiResponse;
        }
        if (serviceClass != PublishService.class) {
            InterfaceDefinition definition = ServiceRegistry.lookupDefinition(channel, txNo, version);
            if (definition.isFirstVerifySecondDecrypt()) {
                if (definition.getVerifyAlgorithm() == null || definition.getVerifyAlgorithm().isEmpty()) {

                } else if (SHA512.equals(definition.getVerifyAlgorithm())) {
                    String sign = SHA.SHA512(apiResponse.getData() + apiRequest.getSessionId() + apiRequest.getTimestamp() + password);
                    if (!sign.equals(apiResponse.getSign())) {
                        apiResponse.setCode(InterfaceRspCode.SIGN_ILLEGAL);
                        asyncHandler.fail(InterfaceRspCode.SIGN_ILLEGAL, "签字信息无效");
                        return apiResponse;
                    }
                } else {
                    apiResponse.setCode(InterfaceRspCode.NOT_SUPPORTED_SIGN_VERIFY_ALGORITHM);
                    asyncHandler.fail(InterfaceRspCode.NOT_SUPPORTED_SIGN_VERIFY_ALGORITHM, "不支持的校验算法" + definition.getVerifyAlgorithm());
                    return apiResponse;
                }

                if (definition.getDecryptAlgorithm() == null || definition.getDecryptAlgorithm().isEmpty()) {

                } else if (AES.equals(definition.getDecryptAlgorithm())) {
                    try {

                        String data0 = com.rnkrsoft.security.AES.decrypt(password, serviceConfigure.getKeyVector(), apiResponse.getData());
                        apiResponse.setData(data0);
                    } catch (Exception e) {
                        apiResponse.setCode(InterfaceRspCode.DECRYPT_HAPPENS_FAIL);
                        asyncHandler.fail(InterfaceRspCode.DECRYPT_HAPPENS_FAIL, "解密发生错误，使用 keyVector:" + serviceConfigure.getKeyVector() + ", password:" + password + ", decryptAlgorithm:" + definition.getDecryptAlgorithm());
                        return apiResponse;
                    }
                } else {
                    apiResponse.setCode(InterfaceRspCode.NOT_SUPPORTED_ENCRYPT_DECRYPT_ALGORITHM);
                    asyncHandler.fail(InterfaceRspCode.NOT_SUPPORTED_ENCRYPT_DECRYPT_ALGORITHM, "不支持的解密算法" + definition.getDecryptAlgorithm());
                    return apiResponse;
                }
            } else {
                if (definition.getDecryptAlgorithm() == null || definition.getDecryptAlgorithm().isEmpty()) {

                } else if (AES.equals(definition.getDecryptAlgorithm())) {
                    try {
                        String data0 = com.rnkrsoft.security.AES.decrypt(password, serviceConfigure.getKeyVector(), apiResponse.getData());
                        apiResponse.setData(data0);
                    } catch (Exception e) {
                        apiResponse.setCode(InterfaceRspCode.DECRYPT_HAPPENS_FAIL);
                        asyncHandler.fail(InterfaceRspCode.DECRYPT_HAPPENS_FAIL, "解密发生错误，使用 keyVector:" + serviceConfigure.getKeyVector() + ", password:" + password + ", decryptAlgorithm:" + definition.getDecryptAlgorithm());
                        return apiResponse;
                    }
                } else {
                    apiResponse.setCode(InterfaceRspCode.NOT_SUPPORTED_ENCRYPT_DECRYPT_ALGORITHM);
                    asyncHandler.fail(InterfaceRspCode.NOT_SUPPORTED_ENCRYPT_DECRYPT_ALGORITHM, "不支持的解密算法" + definition.getDecryptAlgorithm());
                    return apiResponse;
                }
                if (definition.getVerifyAlgorithm() == null || definition.getVerifyAlgorithm().isEmpty()) {

                } else if (SHA512.equals(definition.getVerifyAlgorithm())) {
                    String sign = SHA.SHA512(apiResponse.getData() + apiRequest.getSessionId() + apiRequest.getTimestamp() + password);
                    if (!sign.equals(apiResponse.getSign())) {
                        if (!sign.equals(apiResponse.getSign())) {
                            apiResponse.setCode(InterfaceRspCode.SIGN_ILLEGAL);
                            asyncHandler.fail(InterfaceRspCode.SIGN_ILLEGAL, "签字信息无效");
                            return apiResponse;
                        }
                    }
                } else {
                    apiResponse.setCode(InterfaceRspCode.NOT_SUPPORTED_SIGN_VERIFY_ALGORITHM);
                    asyncHandler.fail(InterfaceRspCode.NOT_SUPPORTED_SIGN_VERIFY_ALGORITHM, "不支持的校验算法" + definition.getVerifyAlgorithm());
                    return apiResponse;
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
                serviceConfigure.debug("async call response data json syntax is illegal, json: '{}' ", apiResponse.getData());
            }
            apiResponse.setCode(InterfaceRspCode.INVALID_COMMUNICATION_MESSAGE);
            asyncHandler.fail(InterfaceRspCode.INVALID_COMMUNICATION_MESSAGE, "无效的通信报文");
            return apiResponse;
        } catch (Exception e) {
            if (serviceConfigure.isDebug()) {
                serviceConfigure.debug("async call response data happens unknown error! json: '{}' ", apiResponse.getData());
            }
            try {
                asyncHandler.exception(e);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return apiResponse;
        }
        try {
            if (serviceConfigure.isDebug()) {
                serviceConfigure.debug("async call success! json: '{}' ", apiResponse.getData());
            }
            asyncHandler.success(response);
            return apiResponse;
        } catch (Throwable e) {
            try {
                asyncHandler.exception(e);
            } catch (Throwable throwable) {
                apiResponse.setCode(InterfaceRspCode.FAIL);
                throwable.printStackTrace();
            }
            return apiResponse;
        }
    }
}
