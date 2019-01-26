package com.rnkrsoft.platform.client;

import com.rnkrsoft.platform.client.exception.InterfaceDefinitionNotFoundException;
import com.rnkrsoft.platform.client.logger.Logger;
import com.rnkrsoft.platform.client.logger.LoggerFactory;
import com.rnkrsoft.platform.protocol.AsyncHandler;
import com.rnkrsoft.platform.protocol.service.FetchPublishRequest;
import com.rnkrsoft.platform.protocol.service.PublishService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rnkrsoft.com on 2019/1/17.
 * 元信息注册中心
 */
public final class MetadataRegister {

    static Logger log = LoggerFactory.getLogger(MetadataRegister.class);
    /**
     * 接口元信息列表
     * 键值存放的 类全限定名:方法名
     */
    final Map<String, InterfaceMetadata> INTERFACE_METADATA = new ConcurrentHashMap();

    final InterfaceMetadata publicPublishMetadata;

    public MetadataRegister() {
        InterfaceMetadata interfaceMetadata = null;
        try {
            interfaceMetadata = InterfaceMetadata.builder()
                    .channel("public")
                    .txNo("000")
                    .version("1")
                    .interfaceClass(PublishService.class)
                    .interfaceMethod(PublishService.class.getMethod("fetchPublish", new Class[]{FetchPublishRequest.class, AsyncHandler.class}))
                    .build();
        } catch (NoSuchMethodException e) {
        }
        publicPublishMetadata = interfaceMetadata;
    }

    /**
     * 向元信息注册中心注册元信息
     *
     * @param metadata 元信息
     */
    public void register(InterfaceMetadata metadata) {
        String key = metadata.getInterfaceClass().getName() + ":" + metadata.getInterfaceMethod().getName();
        INTERFACE_METADATA.put(key, metadata);
    }

    public InterfaceMetadata lookup(String className, String methodName) {
        return lookup(className, methodName, false);
    }

    /**
     * 根据类名和方法名获取接口元信息
     *
     * @param className  类名
     * @param methodName 方法名
     * @param silent     是否静默模式
     * @return 接口元信息
     */
    public InterfaceMetadata lookup(String className, String methodName, boolean silent) {
        String key = className + ":" + methodName;
        if (PublishService.class.getName().equals(className) && "fetchPublish".equals(methodName)) {
            return publicPublishMetadata;
        }
        InterfaceMetadata interfaceMetadata = INTERFACE_METADATA.get(key);
        if (interfaceMetadata == null) {
            log.error("interface '{}.{}' is not definition!", className, methodName);
            if (!silent) {
                throw new InterfaceDefinitionNotFoundException("interface '" + className + "." + methodName + "' is not definition!");
            }
        }
        return interfaceMetadata;
    }
}
