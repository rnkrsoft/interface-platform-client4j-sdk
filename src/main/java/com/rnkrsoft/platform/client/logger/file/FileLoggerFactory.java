package com.rnkrsoft.platform.client.logger.file;


import com.rnkrsoft.config.ConfigProvider;
import com.rnkrsoft.platform.client.logger.ILoggerFactory;
import com.rnkrsoft.platform.client.logger.Logger;

/**
 * Created by rnbkrsoft.com on 2019/1/18.
 */
public class FileLoggerFactory implements ILoggerFactory {
    FileLogger logger;
    public FileLoggerFactory(ConfigProvider config) {
        this.logger = new FileLogger(config);
        this.logger.init();
    }

    @Override
    public Logger getLogger(String name) {
        return this.logger;
    }
}
