package com.rnkrsoft.platform.client.logger;

import com.rnkrsoft.config.ConfigProvider;
import com.rnkrsoft.config.ConfigProviderFactory;
import com.rnkrsoft.platform.client.logger.file.LoggerConstant;
import org.junit.Test;

/**
 * Created by rnkrsoft.com on 2019/1/19.
 */
public class LoggerFactoryTest {

    @Test
    public void testGetLogger() throws Exception {
        ConfigProvider config = ConfigProviderFactory.getPropertiesInstance("logger");
        config.param(LoggerConstant.LOGGER_DIRECTORY, "./target/logs");
        config.param(LoggerConstant.LOGGER_PREFIX, "interface-platform");
        config.param(LoggerConstant.LOGGER_SUFFIX, "log");
        config.param(LoggerConstant.LOGGER_LEVEL, LoggerLevel.TRACE.name());
        config.init("./target", 60 * 24);
        LoggerFactory.CONFIG = config;
        Logger logger = LoggerFactory.getLogger(LoggerFactoryTest.class);
        logger.debug("this is a test {}", "zzzz");
        logger.info("this is a test {}", "zzzz");
        logger.error("this is a test {}", "zzzz");
        logger.trace("this is a test {}", "zzzz");
        logger.warn("this is a test {}", "zzzz");
    }

    @Test
    public void testGetLogger1() throws Exception {
        Logger logger = LoggerFactory.getLogger(LoggerFactoryTest.class);
        logger.debug("this is a test {}", "zzzz");
        logger.info("this is a test {}", "zzzz");
        logger.error("this is a test {}", "zzzz");
        logger.trace("this is a test {}", "zzzz");
        logger.warn("this is a test {}", "zzzz");
    }
}