package com.rnkrsoft.platform.client.logger;
/**
 * Created by rnkrsoft.com on 2019/1/18.
 */
public interface ILoggerFactory {
    /**
     * 获取日志记录器
     * @param name 名称
     * @return 日志记录器
     */
    Logger getLogger(String name);
}
