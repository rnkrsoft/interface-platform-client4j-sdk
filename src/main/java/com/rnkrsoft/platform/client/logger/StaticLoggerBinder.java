package com.rnkrsoft.platform.client.logger;

import com.rnkrsoft.config.ConfigProvider;
import com.rnkrsoft.config.ConfigProviderFactory;
import com.rnkrsoft.platform.client.logger.file.FileLoggerFactory;

public final class StaticLoggerBinder implements LoggerFactoryBinder {
    private static final String LOGGER_FACTORY_CLASS_NAME = FileLoggerFactory.class.getName();

    private static StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

    private ILoggerFactory loggerFactory = new NOPLoggerFactory();

    private StaticLoggerBinder() {
        try {
            loggerFactory = new FileLoggerFactory(ConfigProviderFactory.getPropertiesInstance("log.properties"));
        } catch (Exception e) {

        }
    }

    public static StaticLoggerBinder getSingleton() {
        return SINGLETON;
    }

    @Override
    public ILoggerFactory init(ConfigProvider config) {
        this.loggerFactory = new FileLoggerFactory(config);
        return loggerFactory;
    }

    @Override
    public ILoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    @Override
    public String getLoggerFactoryClassName() {
        return LOGGER_FACTORY_CLASS_NAME;
    }
}