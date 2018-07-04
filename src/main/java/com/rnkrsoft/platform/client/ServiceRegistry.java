package com.rnkrsoft.platform.client;

import com.rnkrsoft.platform.client.scanner.InterfaceMetadata;
import com.rnkrsoft.platform.protocol.service.InterfaceDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by rnkrsoft.com on 2018/6/27.
 */
public final class ServiceRegistry {
    private final static Map<Class, Object> SERVICE_CACHES = new HashMap();
    private final static Map<String, InterfaceMetadata> INTERFACE_METADATA = new HashMap();
    private final static Map<String, InterfaceDefinition> INTERFACE_DEFINITION = new HashMap();
    static AtomicBoolean INIT_METADATA = new AtomicBoolean(false);
    static AtomicBoolean INIT_DEFINITION = new AtomicBoolean(false);

    public static boolean isInit(){
        return INIT_METADATA.get() && INIT_DEFINITION.get() && INTERFACE_DEFINITION.isEmpty();
    }
    /**
     * 注册服务实现类
     * @param stub 桩实现
     */
    public static void register(Object stub){
        SERVICE_CACHES.put(stub.getClass(), stub);
    }

    /**
     * 查找父类
     * @param serviceClass 服务类
     * @param <T>
     * @return 桩实现
     */
    public static <T> T lookup(Class<T> serviceClass){
        if (!INIT_METADATA.get() || !INIT_DEFINITION.get()){
            throw new IllegalArgumentException("未初始化成功");
        }
        T stub = (T) SERVICE_CACHES.get(serviceClass);
        return stub;
    }

    public static InterfaceMetadata lookupMetadata(String className, String methodName){
        if (!INIT_METADATA.get() || !INIT_DEFINITION.get()){
            throw new IllegalArgumentException("未初始化成功");
        }
        return INTERFACE_METADATA.get(className + ":" + methodName);
    }

    public static InterfaceDefinition lookupDefinition(String txNo, String version){
        if (!INIT_METADATA.get() || !INIT_DEFINITION.get()){
            throw new IllegalArgumentException("未初始化成功");
        }
        return INTERFACE_DEFINITION.get(txNo + ":" + version);
    }

    public static void initMetadatas(List<InterfaceMetadata> metadatas) {
        if (INIT_METADATA.get()){
            throw new IllegalArgumentException("已初始化，重复初始化");
        }
        for (InterfaceMetadata metadata : metadatas){
            INTERFACE_METADATA.put(metadata.getInterfaceClass().getName() + ":" + metadata.getInterfaceMethod().getName(), metadata);
        }
        INIT_METADATA.set(true);
    }

    public static void initDefinitions(List<InterfaceDefinition> definitions) {
        if (INIT_DEFINITION.get()){
            INTERFACE_DEFINITION.clear();
        }
        for (InterfaceDefinition definition : definitions){
            INTERFACE_DEFINITION.put(definition.getTxNo() + ":" + definition.getVersion(), definition);
        }
        INIT_DEFINITION.set(true);
    }
}
