package com.rnkrsoft.platform.client.connector.http;

import com.rnkrsoft.com.google.gson.Gson;
import com.rnkrsoft.com.google.gson.GsonBuilder;
import com.rnkrsoft.com.google.gson.JsonSyntaxException;
import com.rnkrsoft.platform.client.logger.Logger;
import com.rnkrsoft.platform.client.logger.LoggerFactory;
import com.rnkrsoft.platform.protocol.enums.InterfaceRspCode;
import com.rnkrsoft.platform.protocol.service.FetchConfigureRequest;
import com.rnkrsoft.platform.protocol.service.FetchConfigureResponse;

import java.net.ConnectException;
import java.net.SocketException;

/**
 * Created by rnkrsoft.com on 2018/8/7.
 */
public class ConfigureService {
    static Logger log = LoggerFactory.getLogger(ConfigureService.class);
    final static Gson GSON = new GsonBuilder().setDateFormat("yyyyMMddHHmmss").create();
    String url;

    public ConfigureService(String schema, String host, int port, String contextPath) {
        this.url = schema + "://" + host + ":" + port + "/" + contextPath;
    }

    public FetchConfigureResponse fetchConfigure(FetchConfigureRequest request) {
        FetchConfigureResponse response;
        String requestJson = GSON.toJson(request);
        log.info("remote configure '{}' request json '{}'!", url, requestJson);
        //进行加密，签字处理
        HttpRequest http = HttpRequest.post(url)
                .acceptCharset("UTF-8")
                .connectTimeout(6 * 1000)
                .readTimeout(6 * 1000)
                .useCaches(false)
                .contentType("application/json;text/plain", "UTF-8");
        try {
            http.send(requestJson);
        } catch (HttpRequest.HttpRequestException e) {
            Exception exception = e.getCause();
            if (exception instanceof ConnectException) {
                response = new FetchConfigureResponse();
                response.setCode(InterfaceRspCode.CONFIGURE_GATEWAY_NOT_FOUND);
                return response;
            }
            if (exception instanceof SocketException && exception.getMessage().toLowerCase().contains("permission denied")) {
                response = new FetchConfigureResponse();
                response.setCode(InterfaceRspCode.SOCKET_PERMISSION_DENIED);
                return response;
            }
            response = new FetchConfigureResponse();
            response.setCode(InterfaceRspCode.FAIL);
            return response;
        }

        if (http.ok()) {
            String responseJson = http.body("UTF-8");
            log.info("remote configure response json '{}'!", responseJson);
            try {
                response = GSON.fromJson(responseJson, FetchConfigureResponse.class);
                return response;
            } catch (JsonSyntaxException e) {
                response = new FetchConfigureResponse();
                response.setCode(InterfaceRspCode.INVALID_COMMUNICATION_MESSAGE);
                return response;
            } catch (Exception e) {
                response = new FetchConfigureResponse();
                response.setCode(InterfaceRspCode.FAIL);
                return response;
            }
        } else if (http.notFound()) {
            response = new FetchConfigureResponse();
            response.setCode(InterfaceRspCode.INTERFACE_PLATFORM_GATEWAY_NOT_FOUND);
            return response;
        } else if (http.serverError()) {
            response = new FetchConfigureResponse();
            response.setCode(InterfaceRspCode.INTERFACE_HAPPENS_SERVER_ERROR);
            return response;
        } else {
            response = new FetchConfigureResponse();
            response.setCode(InterfaceRspCode.FAIL);
            return response;
        }
    }
}
