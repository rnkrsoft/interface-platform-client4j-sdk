package com.rnkrsoft.platform.client.connector.http;

import com.rnkrsoft.com.google.gson.Gson;
import com.rnkrsoft.com.google.gson.GsonBuilder;
import com.rnkrsoft.com.google.gson.JsonSyntaxException;
import com.rnkrsoft.message.MessageFormatter;
import com.rnkrsoft.platform.client.InterfaceSetting;
import com.rnkrsoft.platform.client.ServiceConfigure;
import com.rnkrsoft.platform.client.ServiceFactory;
import com.rnkrsoft.platform.client.connector.InterfaceConnector;
import com.rnkrsoft.platform.client.exception.LocationProviderNotFoundException;
import com.rnkrsoft.platform.client.logger.Logger;
import com.rnkrsoft.platform.client.logger.LoggerFactory;
import com.rnkrsoft.platform.protocol.ApiRequest;
import com.rnkrsoft.platform.protocol.ApiResponse;
import com.rnkrsoft.platform.protocol.enums.InterfaceRspCode;
import com.rnkrsoft.platform.protocol.service.GatewayAddress;

import java.net.ConnectException;
import java.net.SocketException;
import java.util.List;

/**
 * Created by rnkrsoft.com on 2019/1/17.
 */
public class HttpInterfaceConnector implements InterfaceConnector {

    static Logger log = LoggerFactory.getLogger(HttpInterfaceConnector.class);

    final static Gson GSON = new GsonBuilder().setDateFormat("yyyyMMddHHmmss").create();
    ServiceFactory serviceFactory;
    ServiceConfigure serviceConfigure;

    public HttpInterfaceConnector(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
        this.serviceConfigure = serviceFactory.getServiceConfigure();
    }

    @Override
    public ServiceFactory getServiceFactory() {
        return serviceFactory;
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
            log.debug("enabled auto locate!");
            serviceFactory.refreshLocation();
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
        log.debug("call '{}', request '{}' ", url, requestJson);
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
                log.error(MessageFormatter.format("call '{}', 接口平台网关未发现 ", url), exception);
                //TODO 访问百度，如果成功联网成功
                response = new ApiResponse();
                response.setCode(InterfaceRspCode.INTERFACE_PLATFORM_GATEWAY_NOT_FOUND);
                return response;
            }else if (exception instanceof SocketException && exception.getMessage().toLowerCase().contains("permission denied")) {
                log.error(MessageFormatter.format("call '{}', 设备无网络权限 ", url), exception);
                response = new ApiResponse();
                response.setCode(InterfaceRspCode.SOCKET_PERMISSION_DENIED);
                return response;
            }else{
                log.error(MessageFormatter.format("call '{}', gateway not found ,cause: {}", url), e);
                response = new ApiResponse();
                response.setCode(InterfaceRspCode.INTERFACE_PLATFORM_GATEWAY_NOT_FOUND);
                return response;
            }
        }
        if (http.ok()) {
            String responseJson = http.body("UTF-8");
            log.debug("call '{}' success, response '{}' ", url, responseJson);
            try {
                response = GSON.fromJson(responseJson, ApiResponse.class);
                return response;
            } catch (JsonSyntaxException e) {
                log.debug("call '{}' response happens json syntax error!, json {}", url, responseJson);
                response = new ApiResponse();
                response.setCode(InterfaceRspCode.INVALID_COMMUNICATION_MESSAGE);
                return response;
            } catch (Exception e) {
                log.debug("call '{}' response happens unknown error!, json {}, cause:'{}'", url, responseJson, e.getMessage());
                response = new ApiResponse();
                response.setCode(InterfaceRspCode.INVALID_COMMUNICATION_MESSAGE);
                return response;
            }
        } else if (http.notFound()) {
            log.debug("call '{}' not found, cause gateway not found!", url);
            response = new ApiResponse();
            response.setCode(InterfaceRspCode.INTERFACE_PLATFORM_GATEWAY_NOT_FOUND);
            return response;
        } else if (http.serverError()){
            log.debug("call '{}' happens server error!", url);
            response = new ApiResponse();
            response.setCode(InterfaceRspCode.INTERFACE_HAPPENS_SERVER_ERROR);
            return response;
        } else {
            log.debug("call '{}' happens unknown error! http code:{}", url, http.code());
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
                    log.error("call gateway '{}' happens error! cause: '{}'", url, e);
                    response = new ApiResponse();
                    response.setCode(InterfaceRspCode.INTERFACE_EXECUTE_HAPPENS_ERROR);
                    return response;
                }
            }
            log.error("call all gateway '{}' happens error!", gatewayAddresses);
            response = new ApiResponse();
            response.setCode(InterfaceRspCode.INTERFACE_EXECUTE_HAPPENS_ERROR);
            return response;
        } else {
            GatewayAddress gatewayAddress = serviceConfigure.getFallbackGatewayAddresses(request.getChannel());
            if (gatewayAddress == null) {
                log.error("get channel '{}' ,fallback gateway is not found!", request.getChannel());
                response = new ApiResponse();
                response.setCode(InterfaceRspCode.INTERFACE_EXECUTE_HAPPENS_ERROR);
                return response;
            }
            String url = gatewayAddress.getSchema() + "://" + gatewayAddress.getHost() + ":" + gatewayAddress.getPort() + "/" + (gatewayAddress.getContextPath().startsWith("/") ? gatewayAddress.getContextPath().substring(1) : gatewayAddress.getContextPath());
            try {
                response = call0(url, request, setting);
                return response;
            } catch (LocationProviderNotFoundException e) {
                log.error("call gateway '{}'happens location provider is not found ! cause: '{}'", url, e.getMessage());
                response = new ApiResponse();
                response.setCode(InterfaceRspCode.LOCATION_PROVIDER_IS_NOT_CONFIG);
                return response;
            } catch (RuntimeException e) {
                log.error("call gateway '{}' happens error! cause: '{}'", url, e);
                response = new ApiResponse();
                response.setCode(InterfaceRspCode.INTERFACE_EXECUTE_HAPPENS_ERROR);
                return response;
            }
        }
    }
}
