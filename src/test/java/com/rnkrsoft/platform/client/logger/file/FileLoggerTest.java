package com.rnkrsoft.platform.client.logger.file;

import com.rnkrsoft.config.ConfigProvider;
import com.rnkrsoft.config.ConfigProviderFactory;
import com.rnkrsoft.platform.client.logger.LoggerLevel;
import org.junit.Test;

/**
 * Created by rnkrsoft.com on 2019/1/19.
 */
public class FileLoggerTest {

    @Test
    public void testTrace() throws Exception {
        ConfigProvider config = ConfigProviderFactory.getPropertiesInstance("log");
        config.param(LoggerConstant.LOGGER_DIRECTORY, "./target/logs");
        config.param(LoggerConstant.LOGGER_PREFIX, "interface-platform");
        config.param(LoggerConstant.LOGGER_SUFFIX, "log");
        config.param(LoggerConstant.LOGGER_LEVEL, LoggerLevel.TRACE.name());
        config.init("./target", 60 * 24);
        FileLogger logger = new FileLogger(ConfigProviderFactory.getPropertiesInstance("log"));
        logger.init();
        for (int i = 0; i < 100; i++) {
            logger.generateSessionId();
            logger.debug("this is a test {}", "zzzz");
            logger.info("this is a test {}", "zzzz");
            logger.error("this is a test {}", "zzzz");
            logger.trace("this is a test {}", "zzzz");
            logger.warn("this is a test {}", "zzzz");
        }
    }

    @Test
    public void testDebug() throws Exception {

    }

    @Test
    public void testInfo() throws Exception {

    }

    @Test
    public void testWarn() throws Exception {

    }

    @Test
    public void testError() throws Exception {

    }
}