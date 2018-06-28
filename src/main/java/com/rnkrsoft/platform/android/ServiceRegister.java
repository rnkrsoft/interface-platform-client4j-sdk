package com.rnkrsoft.platform.android;

import com.rnkrsoft.platform.protocol.domains.InterfaceDefinition;

import javax.web.doc.annotation.ApidocService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rnkrsoft.com on 2018/6/27.
 */
public final class ServiceRegister {
    private final static Map<Class, Object> SERVICE_CACHES = new HashMap();
    private final static Map<String, InterfaceDefinition> INTERFACE_DEFINITIONS = new HashMap();

    public static boolean isEmpty(){
        return SERVICE_CACHES.isEmpty();
    }
    /**
     * 注册服务实现类
     * @param stub 桩实现
     */
    public static void register(Class serviceClass){
        ApidocService apidocService = (ApidocService) serviceClass.getAnnotation(ApidocService.class);
        if (apidocService == null){

        }

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
        T stub = (T) SERVICE_CACHES.get(serviceClass);
        return stub;
    }

    public static void init(List<InterfaceDefinition> interfaces) {
        for (InterfaceDefinition definition : interfaces){
            INTERFACE_DEFINITIONS.put(definition.getTxNo() + ":" + definition.getVersion(), definition);
        }
    }
}
