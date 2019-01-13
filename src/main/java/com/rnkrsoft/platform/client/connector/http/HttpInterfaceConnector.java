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
package com.rnkrsoft.platform.client.connector.http;

import com.rnkrsoft.com.google.gson.Gson;
import com.rnkrsoft.com.google.gson.GsonBuilder;
import com.rnkrsoft.com.google.gson.JsonSyntaxException;
import com.rnkrsoft.platform.client.InterfaceSetting;
import com.rnkrsoft.platform.client.ServiceConfigure;
import com.rnkrsoft.platform.client.connector.InterfaceConnector;
import com.rnkrsoft.platform.protocol.ApiRequest;
import com.rnkrsoft.platform.protocol.ApiResponse;
import com.rnkrsoft.platform.protocol.enums.InterfaceRspCode;
import com.rnkrsoft.platform.protocol.service.GatewayAddress;

import java.net.ConnectException;
import java.net.SocketException;
import java.util.List;

/**
 * Created by rnkrsoft.com on 2018/10/6.
 */
public class HttpInterfaceConnector implements InterfaceConnector {
    final static Gson GSON = new GsonBuilder().serializeNulls().setDateFormat("yyyyMMddHHmmss").create();
    ServiceConfigure serviceConfigure;

    public HttpInterfaceConnector(ServiceConfigure serviceConfigure) {
        this.serviceConfigure = serviceConfigure;
    }

    @Override
    public ServiceConfigure getServiceConfigure() {
        return serviceConfigure;
    }

    ApiResponse call0(String url, ApiRequest request, InterfaceSetting setting) {
        ApiResponse response = null;
        int connectTimeout = (setting.getHttpConnectTimeoutSecond() == null ? serviceConfigure.getHttpConnectTimeoutSecond() : setting.getHttpConnectTimeoutSecond()) * 1000;
        int readTimeout = (setting.getHttpReadTimeoutSecond() == null ? serviceConfigure.getHttpReadTimeoutSecond() : setting.getHttpReadTimeoutSecond()) * 1000;
        //进行加密，签字处理
        HttpRequest http = HttpRequest.post(url)
                .acceptCharset("UTF-8")
                .connectTimeout(connectTimeout)
                .readTimeout(readTimeout)
                .useCaches(false)
                .contentType("application/json;text/plain", "UTF-8");
        if (serviceConfigure.isAutoLocate()) {
            serviceConfigure.refreshLocation();
        }
        request.setLng(serviceConfigure.getLng());
        request.setLat(serviceConfigure.getLat());
        request.setUic(serviceConfigure.getUic());
        request.setUid(serviceConfigure.getUid());
        request.setDeviceManufacturer(serviceConfigure.getDeviceManufacturer());
        request.setDeviceModel(serviceConfigure.getDeviceModel());
        request.setDeviceType(serviceConfigure.getDeviceType());
        request.setOsVersion(serviceConfigure.getOsVersion());
        request.setAppVersion(serviceConfigure.getAppVersion());
        request.setMacAddress(serviceConfigure.getMacAddress());
        String requestJson = GSON.toJson(request);
        if (serviceConfigure.isDebug()) {
            serviceConfigure.debug("call '{}', request '{}' ", url, requestJson);
        }
        try {
            http.header("IF-USER-SESSIONID", request.getSessionId());
            http.header("IF-USER-UID", request.getUid() == null || request.getUid().isEmpty() ? " " : request.getUid());
            http.header("IF-USER-UIC", request.getUic() == null || request.getUic().isEmpty() ? " " : request.getUic());
            http.header("IF-USER-LAT", request.getLat());
            http.header("IF-USER-LNG", request.getLng());
            http.send(requestJson);
        } catch (HttpRequest.HttpRequestException e) {
            Exception exception = e.getCause();
            if (exception instanceof ConnectException) {
                //TODO 访问百度，如果成功联网成功
                response = new ApiResponse();
                response.setCode(InterfaceRspCode.INTERFACE_PLATFORM_GATEWAY_NOT_FOUND);
                serviceConfigure.debug("call '{}', 接口平台网关未发现 ", url);
                return response;
            }
            if (exception instanceof SocketException && exception.getMessage().toLowerCase().contains("permission denied")) {
                response = new ApiResponse();
                response.setCode(InterfaceRspCode.SOCKET_PERMISSION_DENIED);
                serviceConfigure.debug("call '{}', 设备无网络权限 ", url);
                return response;
            }
            serviceConfigure.debug("call '{}', 发生未知错误 ,cause: {}", url, e);
            response = new ApiResponse();
            response.setCode(InterfaceRspCode.INTERFACE_PLATFORM_GATEWAY_NOT_FOUND);
            return response;
        }
        if (http.ok()) {
            String responseJson = http.body("UTF-8");
            if (serviceConfigure.isDebug()) {
                serviceConfigure.debug("call '{}' success, response '{}' ", url, responseJson);
            }
            try {
                response = GSON.fromJson(responseJson, ApiResponse.class);
                return response;
            } catch (JsonSyntaxException e) {
                if (serviceConfigure.isDebug()) {
                    serviceConfigure.debug("call '{}' response happens json syntax error!, json {}", url, responseJson);
                }
                response = new ApiResponse();
                response.setCode(InterfaceRspCode.INVALID_COMMUNICATION_MESSAGE);
                return response;
            } catch (Exception e) {
                if (serviceConfigure.isDebug()) {
                    serviceConfigure.debug("call '{}' response happens unknown error!, json {}, cause:'{}'", url, responseJson, e.getMessage());
                }
                response = new ApiResponse();
                response.setCode(InterfaceRspCode.FAIL);
                return response;
            }
        } else if (http.notFound()) {
            if (serviceConfigure.isDebug()) {
                serviceConfigure.debug("call '{}' not found, cause gateway not found!", url);
            }
            response = new ApiResponse();
            response.setCode(InterfaceRspCode.INTERFACE_PLATFORM_GATEWAY_NOT_FOUND);
            return response;
        } else {
            System.out.println(http.code());
            if (serviceConfigure.isDebug()) {
                serviceConfigure.debug("call '{}' happens unknown error! http code:{}", url, http.code());
            }
            response = new ApiResponse();
            response.setCode(InterfaceRspCode.FAIL);
            return response;
        }
    }

    @Override
    public ApiResponse call(ApiRequest request, InterfaceSetting setting) {
        List<GatewayAddress> gatewayAddresses = serviceConfigure.getGatewayAddresses(request.getChannel());
        ApiResponse response = null;
        if (gatewayAddresses != null && !gatewayAddresses.isEmpty()) {
            for (GatewayAddress gatewayAddress : gatewayAddresses) {
                String url = gatewayAddress.getSchema() + "://" + gatewayAddress.getHost() + ":" + gatewayAddress.getPort() + "/" + (gatewayAddress.getContextPath().startsWith("/") ? gatewayAddress.getContextPath().substring(1) : gatewayAddress.getContextPath());
                try {
                    response = call0(url, request, setting);
                    //如果是网关未发现，则直接尝试下一个网关
                    if (response.getCode().equals(InterfaceRspCode.INTERFACE_PLATFORM_GATEWAY_NOT_FOUND.getCode())) {
                        continue;
                    }
                    return response;
                } catch (Exception e) {
                    serviceConfigure.error("call gateway '{}' happens error! cause: '{}'", url, e);
                    response = new ApiResponse();
                    response.setCode(InterfaceRspCode.INTERFACE_EXECUTE_HAPPENS_ERROR);
                    return response;
                }
            }
            serviceConfigure.error("call all gateway '{}' happens error!", gatewayAddresses);
            response = new ApiResponse();
            response.setCode(InterfaceRspCode.INTERFACE_EXECUTE_HAPPENS_ERROR);
            return response;
        } else {
            GatewayAddress gatewayAddress = serviceConfigure.getFallbackGatewayAddresses(request.getChannel());
            if (gatewayAddress == null) {
                serviceConfigure.error("get channel '{}' ,fallback gateway configure error!", request.getChannel());
                response = new ApiResponse();
                response.setCode(InterfaceRspCode.INTERFACE_EXECUTE_HAPPENS_ERROR);
                return response;
            }
            String url = gatewayAddress.getSchema() + "://" + gatewayAddress.getHost() + ":" + gatewayAddress.getPort() + "/" + (gatewayAddress.getContextPath().startsWith("/") ? gatewayAddress.getContextPath().substring(1) : gatewayAddress.getContextPath());
            try {
                response = call0(url, request, setting);
                return response;
            } catch (RuntimeException e) {
                serviceConfigure.error("call gateway '{}' happens error! cause: '{}'", url, e);
                response = new ApiResponse();
                response.setCode(InterfaceRspCode.INTERFACE_EXECUTE_HAPPENS_ERROR);
                return response;
            }
        }
    }
}
