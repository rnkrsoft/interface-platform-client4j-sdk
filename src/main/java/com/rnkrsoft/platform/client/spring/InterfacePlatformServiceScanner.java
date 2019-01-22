package com.rnkrsoft.platform.client.spring;

import com.rnkrsoft.platform.client.ServiceFactory;
import com.rnkrsoft.platform.client.logger.Logger;
import com.rnkrsoft.platform.client.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import javax.web.doc.annotation.ApidocService;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

/**
 * Created by rnkrsoft.com on 2019/1/22.
 */
public class InterfacePlatformServiceScanner extends ClassPathBeanDefinitionScanner {
    static Logger log = LoggerFactory.getLogger(InterfacePlatformServiceScanner.class);
    ServiceFactory serviceFactory;
    public InterfacePlatformServiceScanner(BeanDefinitionRegistry registry, ServiceFactory serviceFactory) {
        super(registry, false);
        this.serviceFactory = serviceFactory;
    }

    public void registerFilters() {
        // override AssignableTypeFilter to ignore matches on the actual marker interface
        addIncludeFilter(new AnnotationTypeFilter(ApidocService.class));
        // exclude package-info.java
        addExcludeFilter(new TypeFilter() {
            @Override
            public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                ClassMetadata classMetadata =  metadataReader.getClassMetadata();
                String className = classMetadata.getClassName();
                return className.endsWith("package-info") || !classMetadata.isInterface();
            }
        });
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        if (beanDefinitions.isEmpty()) {
            log.warn("No Service was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
        } else {
            processBeanDefinitions(beanDefinitions);
        }
        return beanDefinitions;
    }

    void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        for (BeanDefinitionHolder holder : beanDefinitions) {
            GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
            if (log.isDebugEnabled()) {
                log.debug("Creating InterfacePlatformClientFactoryBean with name '" + holder.getBeanName() + "' and '" + definition.getBeanClassName() + "' mapperInterface");
            }
            String serviceClassName = definition.getBeanClassName();
            definition.setBeanClass(InterfacePlatformClientFactoryBean.class);
            definition.getPropertyValues().add("serviceClass", serviceClassName);
            definition.getPropertyValues().add("serviceFactory", serviceFactory);
            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
            registerBeanDefinition(holder, getRegistry());
        }
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

    @Override
    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
        if (super.checkCandidate(beanName, beanDefinition)) {
            return true;
        } else {
            log.warn("Skipping InterfacePlatformClientFactoryBean with name '" + beanName + "' and '" + beanDefinition.getBeanClassName() + "' service class" + ". Bean already defined with the same name!");
            return false;
        }
    }

}
