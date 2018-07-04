package com.rnkrsoft.platform.client.scanner;

import javax.web.doc.annotation.ApidocInterface;
import javax.web.doc.annotation.ApidocService;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by rnkrsoft.com on 2018/6/27.
 */
public final class MetadataClassPathScanner {
    /**
     * 扫描指定包名下的@ApidocService
     *
     * @param basePackages 包名
     * @return
     */
    public static List<InterfaceMetadata> scan(String... basePackages) {
        List<InterfaceMetadata> metadatas = new ArrayList();
        ClassScanner classScanner = new ClassScanner(Thread.currentThread().getContextClassLoader(), true);
        for (String basePackage : basePackages) {
            classScanner.scan(basePackage, new ClassScanner.AnnotatedWithFilter(ApidocService.class));
        }
        Collection<Class> classes = classScanner.getClasses();
        for (Class clazz : classes) {
            if (!clazz.isInterface()) {
                continue;
            }
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                ApidocInterface apidocInterface = method.getAnnotation(ApidocInterface.class);
                if (apidocInterface == null) {
                    continue;
                }
                String txNo = apidocInterface.name();
                String version = apidocInterface.version();
                InterfaceMetadata metadata = new InterfaceMetadata();
                metadata.setTxNo(txNo);
                metadata.setVersion(version);
                metadata.setInterfaceClass(clazz);
                metadata.setInterfaceMethod(method);
                metadatas.add(metadata);
            }
        }
        return metadatas;
    }
}
