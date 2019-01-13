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
import com.rnkrsoft.platform.client.exception.UnsupportedAlgorithmException;
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

/**
 * Created by rnkrsoft.com on 2018/7/4.
 * 同步执行器，使用同步方式执行远程接口
 */
public class SyncInvoker {
    private static final Gson GSON = new GsonBuilder().serializeNulls().disableHtmlEscaping().setDateFormat("yyyyMMddHHmmss").create();
    public static final String FETCH_PUBLISH_TX_NO = "000";
    public static final String SHA512 = "SHA512";
    public static final String AES = "AES";
    public static final String PUBLIC_CHANNEL = "public";

    /**
     * 同步调用
     *
     * @param serviceConfigure
     * @param serviceClass
     * @param methodName
     * @param requestClass
     * @param responseClass
     * @param request
     * @return
     */
    public static Object call(ServiceConfigure serviceConfigure, Class serviceClass, String methodName, Class requestClass, Class responseClass, Object request) {
        String channel = null;
        String txNo = null;
        String version = null;
        //如果不是发布服务，则寻找服务类对应的交易码和版本号
        if (serviceClass != PublishService.class) {
            InterfaceMetadata metadata = ServiceRegistry.lookupMetadata(serviceClass.getName(), methodName);
            if (metadata == null) {
                throw new NullPointerException("not found " + serviceClass.getName() + "." + methodName);
            }
            channel = metadata.getChannel();
            txNo = metadata.getTxNo();
            version = metadata.getVersion();
        } else {
            channel = "public";
            txNo = FETCH_PUBLISH_TX_NO;
            version = "1";
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
        if (serviceConfigure.isDebug()) {
            serviceConfigure.debug("async call channel:'{}' txNo:'{}' version:{} ", channel, txNo, version);
        }
        String password = null;
        InterfaceSetting.InterfaceSettingBuilder settingBuilder = InterfaceSetting.builder();
        if (serviceClass != PublishService.class) {
            if (apiRequest.getTxNo() == null || apiRequest.getTxNo().isEmpty()) {
                throw new IllegalArgumentException(MessageFormatter.format("交易码'{}'未配置", apiRequest.getTxNo()));
            }
            if (apiRequest.getVersion() == null || apiRequest.getVersion().isEmpty()) {
                throw new IllegalArgumentException(MessageFormatter.format("版本号'{}'未配置", apiRequest.getVersion()));
            }
            InterfaceDefinition definition = ServiceRegistry.lookupDefinition(channel, txNo, version);
            if (definition == null) {
                throw new NullPointerException(MessageFormatter.format("通道'{}'下交易码'{}:{}'未发现,请检查是否填写了交易码和版本号", channel, txNo, version));
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
                    //将会话ID和时间戳作为待签字文本的一部分，防止报文重发
                    String sign = SHA.SHA512(apiRequest.getData() + apiRequest.getSessionId() + apiRequest.getTimestamp() + password);
                    apiRequest.setSign(sign);
                } else {
                    throw new UnsupportedAlgorithmException("不支持的算法" + definition.getVerifyAlgorithm());
                }
                if (definition.getEncryptAlgorithm() == null || definition.getEncryptAlgorithm().isEmpty()) {

                } else if (AES.equals(definition.getEncryptAlgorithm())) {
                    if (password == null) {
                        throw new NullPointerException("密码为空!");
                    }
                    String data = com.rnkrsoft.security.AES.encrypt(password, serviceConfigure.getKeyVector(), apiRequest.getData());
                    apiRequest.setData(data);
                } else {
                    throw new UnsupportedAlgorithmException("不支持的算法" + definition.getEncryptAlgorithm());
                }
            } else {
                if (definition.getEncryptAlgorithm() == null || definition.getEncryptAlgorithm().isEmpty()) {

                } else if (AES.equals(definition.getEncryptAlgorithm())) {
                    if (password == null) {
                        throw new NullPointerException("密码为空!");
                    }
                    String data = com.rnkrsoft.security.AES.encrypt(password, serviceConfigure.getKeyVector(), apiRequest.getData());
                    apiRequest.setData(data);
                } else {
                    throw new UnsupportedAlgorithmException("不支持的算法" + definition.getEncryptAlgorithm());
                }
                if (definition.getSignAlgorithm() == null || definition.getSignAlgorithm().isEmpty()) {

                } else if (SHA512.equals(definition.getSignAlgorithm())) {
                    //将会话ID和时间戳作为待签字文本的一部分，防止报文重发
                    String sign = SHA.SHA512(apiRequest.getData() + apiRequest.getSessionId() + apiRequest.getTimestamp() + password);
                    apiRequest.setSign(sign);
                } else {
                    throw new UnsupportedAlgorithmException("不支持的算法" + definition.getVerifyAlgorithm());
                }
            }
        } else {
            apiRequest.setChannel(PUBLIC_CHANNEL);
            settingBuilder.httpConnectTimeoutSecond(10);
            settingBuilder.httpReadTimeoutSecond(10);
        }
        if (serviceConfigure.isDebug()) {
            serviceConfigure.debug("sync call ApiRequest:{} ", apiRequest);
        }
        InterfaceConnector interfaceConnector = serviceConfigure.getInterfaceConnector();
        if (interfaceConnector == null) {
            throw new InterfaceConnectorException("interface connector is not config!");
        }
        ApiResponse apiResponse = interfaceConnector.call(apiRequest, settingBuilder.build());
        if (serviceConfigure.isDebug()) {
            serviceConfigure.debug("sync call ApiResponse:{} ", apiResponse);
        }
        if (apiResponse == null) {
            return null;
        }
        if (InterfaceRspCode.valueOfCode(apiResponse.getCode()) != InterfaceRspCode.SUCCESS) {
            serviceConfigure.debug("sync call result , code:'{}' desc:'{}' ", apiResponse.getCode(), apiResponse.getDesc());
            throw new RuntimeException(MessageFormatter.format("sync call result , code:'{}' desc:'{}' ", apiResponse.getCode(), apiResponse.getDesc()));
        }
        //解密内容
        String plainTextData = apiResponse.getData();
        if (plainTextData == null || plainTextData.isEmpty()) {
            serviceConfigure.debug("sync call response data is null ");
            throw new NullPointerException("data is null");
        }
        if (serviceClass != PublishService.class) {
            InterfaceDefinition definition = ServiceRegistry.lookupDefinition(channel, txNo, version);
            if (definition.isFirstVerifySecondDecrypt()) {
                if (definition.getVerifyAlgorithm() == null || definition.getVerifyAlgorithm().isEmpty()) {
                } else if (SHA512.equals(definition.getVerifyAlgorithm())) {
                    String sign = SHA.SHA512(apiResponse.getData() + apiRequest.getSessionId() + apiRequest.getTimestamp() + password);
                    if (!sign.equals(apiResponse.getSign())) {
                        serviceConfigure.debug("data: '{}', sign: '{}', calc sign: '{}'", apiResponse.getData(), apiResponse.getSign(), sign);
                        throw new IllegalArgumentException("无效签字");
                    }
                } else {
                    throw new UnsupportedAlgorithmException("unsupported verify algorithm " + definition.getVerifyAlgorithm());
                }

                if (definition.getDecryptAlgorithm() == null || definition.getDecryptAlgorithm().isEmpty()) {
                    //nothing
                } else if (AES.equals(definition.getDecryptAlgorithm())) {
                    plainTextData = com.rnkrsoft.security.AES.decrypt(password, serviceConfigure.getKeyVector(), apiResponse.getData());
                } else {
                    throw new UnsupportedAlgorithmException("unsupported decrypt algorithm " + definition.getDecryptAlgorithm());
                }
            } else {
                if (definition.getDecryptAlgorithm() == null || definition.getDecryptAlgorithm().isEmpty()) {
                    //nothing
                } else if (AES.equals(definition.getDecryptAlgorithm())) {
                    plainTextData = com.rnkrsoft.security.AES.decrypt(password, serviceConfigure.getKeyVector(), apiResponse.getData());
                } else {
                    throw new UnsupportedAlgorithmException("unsupported decrypt algorithm " + definition.getDecryptAlgorithm());
                }
                if (definition.getVerifyAlgorithm() == null || definition.getVerifyAlgorithm().isEmpty()) {

                } else if (SHA512.equals(definition.getVerifyAlgorithm())) {
                    String sign = SHA.SHA512(plainTextData + apiRequest.getSessionId() + apiRequest.getTimestamp() + password);
                    if (!sign.equals(apiResponse.getSign())) {
                        serviceConfigure.debug("data: '{}', sign: '{}', calc sign: '{}'", plainTextData, apiResponse.getSign(), sign);
                        throw new IllegalArgumentException("无效签字");
                    }
                } else {
                    throw new UnsupportedAlgorithmException("unsupported verify algorithm " + definition.getVerifyAlgorithm());
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
            }
        } catch (JsonSyntaxException e) {
            if (serviceConfigure.isDebug()) {
                serviceConfigure.debug("sync call response data json syntax is illegal, json: '{}' ", apiResponse.getData());
            }
            return false;
        } catch (Exception e) {
            if (serviceConfigure.isDebug()) {
                serviceConfigure.debug("sync call response data happens unknown error! json: '{}' ", apiResponse.getData());
            }
            return false;
        }
        return response;
    }
}
