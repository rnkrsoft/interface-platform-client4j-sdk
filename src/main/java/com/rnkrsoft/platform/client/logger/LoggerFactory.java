package com.rnkrsoft.platform.client.logger;

import com.rnkrsoft.config.ConfigProvider;
import com.rnkrsoft.config.ConfigProviderFactory;
import com.rnkrsoft.platform.client.logger.file.LoggerConstant;

/**
 * Created by rnkrsoft.com on 2019/1/18.
 */
public final class LoggerFactory {
    /**
     * 未初始化
     */
    static final int UNINITIALIZED = 0;
    /**
     * 初始中
     */
    static final int ONGOING_INITIALIZATION = 1;
    /**
     * 初始化失败
     */
    static final int FAILED_INITIALIZATION = 2;
    /**
     * 初始化成功
     */
    static final int SUCCESSFUL_INITIALIZATION = 3;
    /**
     * 初始化无任何实现
     */
    static final int NOP_FALLBACK_INITIALIZATION = 4;

    static final String UNSUCCESSFUL_INIT_MSG = "init failure";

    static ConfigProvider CONFIG = null;

    static NOPLoggerFactory NOP_FALLBACK_FACTORY = new NOPLoggerFactory();

    static volatile int INITIALIZATION_STATE = UNINITIALIZED;


    private LoggerFactory() {
    }

    public static void level(LoggerLevel level) {
        if (CONFIG == null) {
            return;
        }
        CONFIG.param(LoggerConstant.LOGGER_LEVEL, level.name());
    }

    /**
     * 完成初始化
     */
    final static void performInitialization() {
        if (INITIALIZATION_STATE == ONGOING_INITIALIZATION) {
            if (CONFIG == null) {
                synchronized (LoggerFactory.class) {
                    if (CONFIG == null) {
                        try {
                            ConfigProvider config = ConfigProviderFactory.getPropertiesInstance("logger");
                            config.param(LoggerConstant.LOGGER_DIRECTORY, "logs");
                            config.param(LoggerConstant.LOGGER_PREFIX, "interface-platform");
                            config.param(LoggerConstant.LOGGER_SUFFIX, "log");
                            config.param(LoggerConstant.LOGGER_LEVEL, LoggerLevel.TRACE.name());
                            config.param(LoggerConstant.LOGGER_SOUT, "true");
                            config.init("./target", 60 * 24);
                            CONFIG = config;
                        } catch (Exception e) {
                            ;
                        }
                    }
                }
            }
            StaticLoggerBinder.getSingleton().init(CONFIG);
        }
    }

    public static ILoggerFactory getILoggerFactory() {
        if (INITIALIZATION_STATE == UNINITIALIZED) {
            synchronized (LoggerFactory.class) {
                if (INITIALIZATION_STATE == UNINITIALIZED) {
                    INITIALIZATION_STATE = ONGOING_INITIALIZATION;
                    performInitialization();
                    INITIALIZATION_STATE = SUCCESSFUL_INITIALIZATION;
                }
            }
        }
        switch (INITIALIZATION_STATE) {
            case SUCCESSFUL_INITIALIZATION:
                return StaticLoggerBinder.getSingleton().getLoggerFactory();
            case NOP_FALLBACK_INITIALIZATION:
                return NOP_FALLBACK_FACTORY;
            case FAILED_INITIALIZATION:
                throw new IllegalStateException(UNSUCCESSFUL_INIT_MSG);
            case ONGOING_INITIALIZATION:
                return NOP_FALLBACK_FACTORY;
        }
        throw new IllegalStateException("Unreachable code");
    }

    public static Logger getLogger(String name) {
        ILoggerFactory iLoggerFactory = getILoggerFactory();
        return iLoggerFactory.getLogger(name);
    }

    public static Logger getLogger(Class<?> clazz) {
        Logger logger = getLogger(clazz.getName());
        return logger;
    }

}
