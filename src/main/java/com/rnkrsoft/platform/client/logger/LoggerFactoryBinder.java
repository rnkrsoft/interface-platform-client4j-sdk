package com.rnkrsoft.platform.client.logger;

import com.rnkrsoft.config.ConfigProvider;

public interface LoggerFactoryBinder {
    /**
     * 初始化
     * @param config 配置
     * @return 抽象的日志工厂类
     */
    ILoggerFactory init(ConfigProvider config);

    /**
     * 获取日志工厂
     * @return
     */
    ILoggerFactory getLoggerFactory();

    /**
     * 获取日志工厂类名
     * @return 日志工厂类名
     */
    String getLoggerFactoryClassName();
}