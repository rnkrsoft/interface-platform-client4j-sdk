package com.rnkrsoft.platform.client.spring;

import com.rnkrsoft.platform.client.ServiceFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;

/**
 * Created by rnkrsoft.com on 2019/1/22.
 */
public class InterfacePlatformClientConfigure implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware, BeanNameAware {
    @Setter
    ConfigureAddress configure;
    @Setter
    List<GatewayAddress> fallbackGateways;
    @Setter
    String keyVector = "1234567890654321";
    @Setter
    String password = "1234567890";
    @Setter
    String[] basePackages;

    @Setter
    @Getter
    String beanName;

    ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        //初始化服务工厂
        ServiceFactory serviceFactory = initServiceFactory();
        //进行服务扫描
        InterfacePlatformServiceScanner scanner = new InterfacePlatformServiceScanner(beanDefinitionRegistry, serviceFactory);
        scanner.registerFilters();
        scanner.doScan(this.basePackages);
    }

    /**
     * 初始化服务工厂
     *
     * @return 服务工厂
     */
    ServiceFactory initServiceFactory() {
        if (fallbackGateways == null) {
            throw new NullPointerException("未配置回退配置！");
        }
        ServiceFactory serviceFactory = new ServiceFactory();
        if (configure != null) {
            serviceFactory.settingConfigure(configure.ssl, configure.host, configure.port, configure.contextPath);
        }
        for (GatewayAddress gatewayAddress : fallbackGateways) {
            serviceFactory.settingFallback(gatewayAddress.channel, gatewayAddress.ssl, gatewayAddress.host, gatewayAddress.port, gatewayAddress.contextPath);
        }
        serviceFactory.scan(basePackages);
        serviceFactory.setKeyVector(keyVector);
        serviceFactory.setPassword(password);
        serviceFactory.init();
        return serviceFactory;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
