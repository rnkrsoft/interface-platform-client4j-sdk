package com.rnkrsoft.platform.client;

import com.rnkrsoft.platform.protocol.service.FetchConfigureResponse;
import com.rnkrsoft.platform.protocol.service.GatewayChannel;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class Configure implements Serializable {
    public Configure(){

    }
    public Configure(FetchConfigureResponse fetchConfigureResponse) {
        this.channels.addAll(fetchConfigureResponse.getChannels());
        this.keyVector = fetchConfigureResponse.getKeyVector();
        this.httpConnectTimeoutSecond = fetchConfigureResponse.getHttpConnectTimeoutSecond();
        this.httpReadTimeoutSecond = fetchConfigureResponse.getHttpReadTimeoutSecond();
        this.asyncExecuteThreadPoolSize = fetchConfigureResponse.getAsyncExecuteThreadPoolSize();
        this.debug = fetchConfigureResponse.isDebug();
        this.autoLocate = fetchConfigureResponse.isAutoLocate();
        this.verboseLog = fetchConfigureResponse.isVerboseLog();
        this.env = fetchConfigureResponse.getEnv();
        this.envDesc = fetchConfigureResponse.getEnvDesc();
    }
    final List<GatewayChannel> channels = new ArrayList();

    String keyVector;

    int httpConnectTimeoutSecond;

    int httpReadTimeoutSecond;

    int asyncExecuteThreadPoolSize;

    boolean debug;

    boolean autoLocate;

    boolean verboseLog;

    int env;

    String envDesc;
}