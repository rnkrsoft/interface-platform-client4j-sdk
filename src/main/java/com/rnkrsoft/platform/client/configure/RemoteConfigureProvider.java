package com.rnkrsoft.platform.client.configure;

import com.rnkrsoft.platform.client.Configure;
import com.rnkrsoft.platform.client.ConfigureProvider;
import com.rnkrsoft.platform.client.async.AsyncTask;
import com.rnkrsoft.platform.client.connector.http.ConfigureService;
import com.rnkrsoft.platform.protocol.enums.InterfaceRspCode;
import com.rnkrsoft.platform.protocol.service.FetchConfigureRequest;
import com.rnkrsoft.platform.protocol.service.FetchConfigureResponse;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by rnkrsoft.com on 2019/1/17.
 */
public class RemoteConfigureProvider implements ConfigureProvider {
    @Override
    public Configure load(String schema, String host, int port, String contextPath, List<String> channels, String uic, String deviceType, String appVersion, double lat, double lng) {
        final ConfigureService configureService = new ConfigureService(schema, host, port, contextPath);
        final FetchConfigureRequest request = new FetchConfigureRequest();
        request.setUic(uic);
        request.setDeviceType(deviceType);
        request.setAppVersion(appVersion);
        request.getChannels().addAll(channels);
        request.setLng(Double.toString(lng));
        request.setLat(Double.toString(lat));
        //构建一个单线程的线程池
        FetchConfigureResponse response = null;
        AsyncTask<FetchConfigureRequest, Void, FetchConfigureResponse> asyncTask = new AsyncTask<FetchConfigureRequest, Void, FetchConfigureResponse>() {
            @Override
            protected FetchConfigureResponse doInBackground(FetchConfigureRequest... params) {
                return configureService.fetchConfigure(params[0]);
            }
        };
        asyncTask.execute(request);
        try {
            response = asyncTask.get(30, TimeUnit.SECONDS); //取得结果，同时设置超时执行时间为5秒。同样可以用future.get()，不设置执行超时时间取得结果
        } catch (InterruptedException e) {
            e.printStackTrace();
            asyncTask.cancel(true);
        } catch (ExecutionException e) {
            e.printStackTrace();
            asyncTask.cancel(true);
        } catch (TimeoutException e) {
            e.printStackTrace();
            asyncTask.cancel(true);
        }

        if (response == null || InterfaceRspCode.valueOfCode(response.getCode()) != InterfaceRspCode.SUCCESS) {
            System.out.println(response);
            return null;
        }
        Configure configure = new Configure(response);
        return configure;
    }
}
