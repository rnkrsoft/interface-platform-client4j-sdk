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
 * 接口平台客户端Spring配置对象
 */
public class InterfacePlatformClientConfigure implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware, BeanNameAware {
    /**
     * 配置远程网关配置
     */
    @Setter
    ConfigureAddress configure;
    /**
     * 通道回退网关地址
     */
    @Setter
    List<GatewayAddress> fallbackGateways;
    /**
     * 密钥向量
     */
    @Setter
    String keyVector = "1234567890654321";
    /**
     * 服务器通信的固定密码
     */
    @Setter
    String password = "1234567890";
    /**
     * 扫描的包路径
     */
    @Setter
    String[] basePackages;
    /**
     * 一个小时拉取一次远程配置
     */
    @Setter
    int fetchConfigureIntervalSecond = 60 * 60 * 1000;
    /**
     * 一个小时拉取一次接口元信息
     */
    @Setter
    int fetchMetadataIntervalSecond = 60 * 60 * 1000;

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
        serviceFactory.setFetchConfigureIntervalSecond(fetchConfigureIntervalSecond);
        serviceFactory.setFetchMetadataIntervalSecond(fetchMetadataIntervalSecond);
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
