package com.rnkrsoft.platform.android.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rnkrsoft.platform.android.HttpRequest;
import com.rnkrsoft.platform.protocol.ApiRequest;
import com.rnkrsoft.platform.protocol.ApiResponse;
import com.rnkrsoft.platform.protocol.InterfaceRspCode;

import java.util.logging.Logger;


/**
 * Created by rnkrsoft.com on 2018/6/27.
 */
public class ServiceClient {
    final static Logger log = Logger.getLogger(ServiceClient.class.toString());
    final static Gson GSON = new GsonBuilder().serializeNulls().create();

    public static ApiResponse call(String url, ApiRequest request) {
        ApiResponse response = null;
        //进行加密，签字处理
        HttpRequest http = HttpRequest.post(url)
                .acceptGzipEncoding()
                .acceptCharset("UTF-8")
                .connectTimeout(20 * 1000)
                .readTimeout(20 * 1000)
                .useCaches(false)
                .contentType("application/json;text/plain", "UTF-8");
        String requestJson = GSON.toJson(request);
        log.info(url);
        log.info(requestJson);
        try {
            http.send(requestJson);
        } catch (HttpRequest.HttpRequestException e) {
            Exception exception = e.getCause();
            if (exception instanceof java.net.ConnectException) {
                //TODO 访问百度，如果成功联网成功
                response = new ApiResponse();
                response.setCode(InterfaceRspCode.DEVICE_CAN_NOT_ACCESS_INTERNET);
                return response;
            }
            response = new ApiResponse();
            response.setCode(InterfaceRspCode.FAIL);
            return response;
        }
        if (http.ok()) {
            String responseJson = http.body("UTF-8");
            try {
                response = GSON.fromJson(responseJson, ApiResponse.class);
                return response;
            } catch (Exception e) {
                response = new ApiResponse();
                response.setCode(InterfaceRspCode.FAIL);
                return response;
            }
        } else if (http.notFound()) {
            response = new ApiResponse();
            response.setCode(InterfaceRspCode.FAIL);
            return response;
        } else {
            response = new ApiResponse();
            response.setCode(InterfaceRspCode.FAIL);
            return response;
        }
    }
}
