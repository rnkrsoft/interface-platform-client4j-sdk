package com.rnkrsoft.platform.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.rnkrsoft.platform.client.utils.DateUtil;
import com.rnkrsoft.platform.protocol.ApiRequest;
import com.rnkrsoft.platform.protocol.ApiResponse;
import com.rnkrsoft.platform.protocol.InterfaceRspCode;

import java.net.ConnectException;
import java.net.SocketException;
import java.util.logging.Logger;


/**
 * Created by rnkrsoft.com on 2018/6/27.
 */
public class ServiceClient {
    final static Gson GSON = new GsonBuilder().serializeNulls().setDateFormat("yyyyMMddHHmmss").create();

    public static ApiResponse call(ServiceConfigure serviceConfigure, String url, ApiRequest request) {
        ApiResponse response = null;
        //进行加密，签字处理
        HttpRequest http = HttpRequest.post(url)
                .acceptGzipEncoding()
                .acceptCharset("UTF-8")
                .connectTimeout(serviceConfigure.getHttpConnectTimeoutSecond() * 1000)
                .readTimeout(serviceConfigure.getHttpReadTimeoutSecond() * 1000)
                .useCaches(false)
                .contentType("application/json;text/plain", "UTF-8");
        if (serviceConfigure.isAutoLocate()){
            serviceConfigure.refreshLocation();
        }
        request.setLng(serviceConfigure.getLng());
        request.setLat(serviceConfigure.getLat());
        String requestJson = GSON.toJson(request);
        if(serviceConfigure.isDebug()){
            serviceConfigure.log("call '{}', request '{}' ", url, requestJson);
        }
        try {
            http.send(requestJson);
        } catch (HttpRequest.HttpRequestException e) {
            Exception exception = e.getCause();
            if (exception instanceof ConnectException) {
                //TODO 访问百度，如果成功联网成功
                response = new ApiResponse();
                response.setCode(InterfaceRspCode.DEVICE_CAN_NOT_ACCESS_INTERNET);
                serviceConfigure.log("call '{}', 设备无法访问网络 ", url);
                return response;
            }
            if (exception instanceof SocketException && exception.getMessage().toLowerCase().contains("permission denied")){
                response = new ApiResponse();
                response.setCode(InterfaceRspCode.SOCKET_PERMISSION_DENIED);
                serviceConfigure.log("call '{}', 设备无网络权限 ", url);
                return response;
            }
            serviceConfigure.log("call '{}', 发生未知错误 ,cause: {}", url, e);
            response = new ApiResponse();
            response.setCode(InterfaceRspCode.FAIL);
            return response;
        }
        if (http.ok()) {
            String responseJson = http.body("UTF-8");
            if(serviceConfigure.isDebug()){
                serviceConfigure.log("call '{}' success, response '{}' ", url, responseJson);
            }
            try {
                response = GSON.fromJson(responseJson, ApiResponse.class);
                return response;
            } catch (JsonSyntaxException e) {
                if(serviceConfigure.isDebug()){
                    serviceConfigure.log("call '{}' response json syntax error!, json {}", url, responseJson);
                }
                response = new ApiResponse();
                response.setCode(InterfaceRspCode.INVALID_COMMUNICATION_MESSAGE);
                return response;
            } catch (Exception e) {
                response = new ApiResponse();
                response.setCode(InterfaceRspCode.FAIL);
                return response;
            }
        } else if (http.notFound()) {
            if(serviceConfigure.isDebug()){
                serviceConfigure.log("call '{}' not found，cause gateway not found!", url);
            }
            response = new ApiResponse();
            response.setCode(InterfaceRspCode.INTERFACE_PLATFORM_GATEWAY_NOT_FOUND);
            return response;
        } else {
            System.out.println(http.code());
            if(serviceConfigure.isDebug()){
                serviceConfigure.log("call '{}' happens unknown error!", url);
            }
            response = new ApiResponse();
            response.setCode(InterfaceRspCode.FAIL);
            return response;
        }
    }
}
