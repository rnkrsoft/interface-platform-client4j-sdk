package com.rnkrsoft.platform.client.scanner;

import com.rnkrsoft.platform.client.InterfaceMetadata;

import javax.web.doc.annotation.ApidocInterface;
import javax.web.doc.annotation.ApidocService;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by rnkrsoft.com on 2018/6/27.
 * 元信息扫描器
 */
public final class MetadataClassPathScanner {
    /**
     * 扫描指定包名下的@ApidocService
     *
     * @param classes 服务类集合
     * @return 元信息列表
     */
    public static Map<String, Set<InterfaceMetadata>> scan(Collection<Class> classes) {
        Map<String, Set<InterfaceMetadata>> metadataMap = new HashMap();
        for (Class clazz : classes) {
            if (!clazz.isInterface()) {
                continue;
            }
            ApidocService apidocService = (ApidocService) clazz.getAnnotation(ApidocService.class);
            if (apidocService == null) {
                continue;
            }
            String version = apidocService.version();
            String channel = apidocService.channel();
            Set<InterfaceMetadata> metadataSet = metadataMap.get(channel);
            if (metadataSet == null) {
                metadataSet = new HashSet();
                metadataMap.put(channel, metadataSet);
            }
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                ApidocInterface apidocInterface = method.getAnnotation(ApidocInterface.class);
                if (apidocInterface == null) {
                    continue;
                }
                String txNo = apidocInterface.name().isEmpty() ? method.getName() : apidocInterface.name();
                String version0 = apidocInterface.version().isEmpty() ? version : apidocInterface.version();
                InterfaceMetadata metadata = InterfaceMetadata.builder()
                        .channel(channel)
                        .txNo(txNo)
                        .version(version0)
                        .interfaceClass(clazz)
                        .interfaceMethod(method)
                        .build();
                metadataSet.add(metadata);
            }
        }
        return metadataMap;
    }
}
