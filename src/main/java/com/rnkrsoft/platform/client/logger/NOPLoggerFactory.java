package com.rnkrsoft.platform.client.logger;

/**
 * Created by rnkrsoft.com on 2019/1/18.
 */
public class NOPLoggerFactory implements ILoggerFactory {

    public NOPLoggerFactory() {
        // nothing to do
    }

    public Logger getLogger(String name) {
        return NOPLogger.NOP_LOGGER;
    }

}