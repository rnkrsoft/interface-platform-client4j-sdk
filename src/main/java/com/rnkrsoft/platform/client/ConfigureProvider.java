package com.rnkrsoft.platform.client;

import java.util.List;

/**
 * Created by rnkrsoft.com on 2018/8/6.
 * 配置信息提供者
 */
public interface ConfigureProvider {

    /**
     * 通过远程服务器加载配置
     *
     * @param schema      模式
     * @param host        主机地址
     * @param port        端口号
     * @param contextPath 上下文路径
     * @param channels    渠道数组
     * @param uic         用户识别码
     * @param deviceType  设别类型
     * @param appVersion  应用版本号
     * @param lat         经度
     * @param lng         纬度
     * @return 配置对象
     */
    Configure load(String schema, String host, int port, String contextPath, List<String> channels, String uic, String deviceType, String appVersion, double lat, double lng);
}