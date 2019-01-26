package com.rnkrsoft.platform.client;

import com.rnkrsoft.platform.client.exception.InterfaceDefinitionNotFoundException;
import com.rnkrsoft.platform.client.logger.Logger;
import com.rnkrsoft.platform.client.logger.LoggerFactory;
import com.rnkrsoft.platform.protocol.service.InterfaceChannel;
import com.rnkrsoft.platform.protocol.service.InterfaceDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rnkrsoft.com on 2019/1/17.
 * 定义注册中心
 */
public final class DefinitionRegister {
    static Logger log = LoggerFactory.getLogger(DefinitionRegister.class);
    /**
     * 接口定义信息列表
     * 键值存放的 通道名:交易码:版本号
     */
    final Map<String, InterfaceDefinition> INTERFACE_CHANNEL = new ConcurrentHashMap();

    final InterfaceDefinition publicPublishDefinition;

    public DefinitionRegister() {
        publicPublishDefinition = InterfaceDefinition.builder()
                .channel("public")
                .txNo("000")
                .version("1")
                .build();
    }

    /**
     * 注册接口定义信息
     *
     * @param interfaceChannel 接口定义信息
     */
    public void register(InterfaceChannel interfaceChannel) {
        for (InterfaceDefinition definition : interfaceChannel.getInterfaces()) {
            String key = definition.getChannel() + ":" + definition.getTxNo() + ":" + definition.getVersion();
            INTERFACE_CHANNEL.put(key, definition);
        }
    }

    public void clear() {
        INTERFACE_CHANNEL.clear();
    }

    public InterfaceDefinition lookup(String channel, String txNo, String version) {
        return lookup(channel, txNo, version, false);
    }

    /**
     * 根据通道号，交易码和版本号获取接口定义信息
     *
     * @param channel 通道号
     * @param txNo    交易码
     * @param version 版本号
     * @param silent  是否静默模式
     * @return 接口定义信息
     */
    public InterfaceDefinition lookup(String channel, String txNo, String version, boolean silent) {
        if (channel == null || channel.isEmpty()) {
            throw new NullPointerException("通道号为空");
        }
        if (txNo == null || txNo.isEmpty()) {
            throw new NullPointerException("交易码为空");
        }
        if (version == null || version.isEmpty()) {
            version = "1";
        }
        if ("public".equals(channel) && "000".equals(txNo) && "1".equals(version)) {
            return publicPublishDefinition;
        }
        String key = channel + ":" + txNo + ":" + version;
        InterfaceDefinition interfaceDefinition = INTERFACE_CHANNEL.get(key);
        if (interfaceDefinition == null) {
            log.error("interface '{}' is not definition!", key);
            if (!silent) {
                throw new InterfaceDefinitionNotFoundException("interface '" + key + "'is not definition!");
            }
        }
        return interfaceDefinition;
    }
}
