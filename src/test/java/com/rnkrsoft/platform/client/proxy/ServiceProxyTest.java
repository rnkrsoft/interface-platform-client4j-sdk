package com.rnkrsoft.platform.client.proxy;

import com.rnkrsoft.platform.client.InterfaceMetadata;
import com.rnkrsoft.platform.client.ServiceConfigure;
import com.rnkrsoft.platform.client.ServiceFactory;
import com.rnkrsoft.platform.client.async.AsyncTask;
import com.rnkrsoft.platform.client.connector.MockHelloFailureInterfaceConnector;
import com.rnkrsoft.platform.client.connector.MockHelloSuccessInterfaceConnector;
import com.rnkrsoft.platform.client.demo.domain.HelloRequest;
import com.rnkrsoft.platform.client.demo.domain.HelloResponse;
import com.rnkrsoft.platform.client.demo.service.HelloService;
import com.rnkrsoft.platform.client.exception.RemoteInterfaceExecutionException;
import com.rnkrsoft.platform.client.logger.Logger;
import com.rnkrsoft.platform.client.logger.LoggerFactory;
import com.rnkrsoft.platform.protocol.AsyncHandler;
import com.rnkrsoft.platform.protocol.service.InterfaceChannel;
import com.rnkrsoft.platform.protocol.service.InterfaceDefinition;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by rnkrsoft.com on 2019/1/17.
 */
public class ServiceProxyTest {

    @Test
    public void testHellSuccess() throws Exception {
        final Logger logger = LoggerFactory.getLogger("");
        logger.generateSessionId();
        ServiceFactory serviceFactory =ServiceFactory.newInstance();
        ServiceConfigure serviceConfigure = serviceFactory.getServiceConfigure();
        InterfaceMetadata interfaceMetadata = InterfaceMetadata.builder().channel("test-channel").txNo("010").version("1").interfaceClass(HelloService.class).interfaceMethod(HelloService.class.getMethod("hello", HelloRequest.class)).build();
        serviceFactory.getMetadataRegister().register(interfaceMetadata);
        InterfaceDefinition interfaceDefinition = InterfaceDefinition.builder().channel("test-channel").txNo("010").version("1").build();
        InterfaceChannel interfaceChannel = new InterfaceChannel();
        interfaceChannel.setChannel("test-channel");
        interfaceChannel.getInterfaces().add(interfaceDefinition);
        serviceFactory.getDefinitionRegister().register(interfaceChannel);
        serviceConfigure.setInterfaceConnectorClass(MockHelloSuccessInterfaceConnector.class);
        HelloService helloService = ServiceProxyFactory.newInstance(serviceFactory, HelloService.class);
        HelloRequest request = new HelloRequest();
        request.setName("test");
        HelloResponse response = helloService.hello(request);
        System.out.println(response);
        Assert.assertEquals("0000", response.getRspCode());
        Assert.assertEquals("成功", response.getRspDesc());
        Assert.assertEquals("hello," + request.getName(), response.getText());
    }

    @Test
    public void testHelloFailure() throws Exception {
        final Logger logger = LoggerFactory.getLogger("");
        logger.generateSessionId();
        ServiceFactory serviceFactory =ServiceFactory.newInstance();
        ServiceConfigure serviceConfigure = serviceFactory.getServiceConfigure();
        InterfaceMetadata interfaceMetadata = InterfaceMetadata.builder().channel("test-channel").txNo("010").version("1").interfaceClass(HelloService.class).interfaceMethod(HelloService.class.getMethod("hello", HelloRequest.class)).build();
        serviceFactory.getMetadataRegister().register(interfaceMetadata);
        InterfaceDefinition interfaceDefinition = InterfaceDefinition.builder().channel("test-channel").txNo("010").version("1").build();
        InterfaceChannel interfaceChannel = new InterfaceChannel();
        interfaceChannel.setChannel("test-channel");
        interfaceChannel.getInterfaces().add(interfaceDefinition);
        serviceFactory.getDefinitionRegister().register(interfaceChannel);
        serviceConfigure.setInterfaceConnectorClass(MockHelloFailureInterfaceConnector.class);
        HelloService helloService = ServiceProxyFactory.newInstance(serviceFactory, HelloService.class);
        HelloRequest request = new HelloRequest();
        request.setName("test");
        try {
            HelloResponse response = helloService.hello(request);
            Assert.fail("不应该到这里");
        } catch (RemoteInterfaceExecutionException e) {
            Assert.assertEquals("sync call is failure, cause 系统正在维护，请稍后重试！(998)!", e.getMessage());
        }
        Assert.fail("不应该到这里");
    }

    @Test
    public void testHelloAsyncSuccess() throws Exception {
        final Logger logger = LoggerFactory.getLogger("");
        logger.generateSessionId();
        ServiceFactory serviceFactory =ServiceFactory.newInstance();
        ServiceConfigure serviceConfigure = serviceFactory.getServiceConfigure();
        InterfaceMetadata interfaceMetadata = InterfaceMetadata.builder().channel("test-channel").txNo("010").version("1").interfaceClass(HelloService.class).interfaceMethod(HelloService.class.getMethod("hello", HelloRequest.class)).build();
        serviceFactory.getMetadataRegister().register(interfaceMetadata);
        InterfaceDefinition interfaceDefinition = InterfaceDefinition.builder().channel("test-channel").txNo("010").version("1").build();
        InterfaceChannel interfaceChannel = new InterfaceChannel();
        interfaceChannel.setChannel("test-channel");
        interfaceChannel.getInterfaces().add(interfaceDefinition);
        serviceFactory.getDefinitionRegister().register(interfaceChannel);
        serviceConfigure.setInterfaceConnectorClass(MockHelloSuccessInterfaceConnector.class);
        HelloService helloService = ServiceProxyFactory.newInstance(serviceFactory, HelloService.class);
        final HelloRequest request = new HelloRequest();
        request.setName("test");
        AsyncTask asyncTask = helloService.hello(request, new AsyncHandler<HelloResponse>() {
            @Override
            public void fail(String code, String desc, String detail) {
                logger.debug("fail");
                Assert.fail("不应该到这里");
            }

            @Override
            public void success(HelloResponse response) {
                logger.debug("success");
                Assert.assertEquals("hello," + request.getName(), response.getText());
            }
        });
        asyncTask.get();
    }

    @Test
    public void testHelloAsyncFailure() throws Exception {
        final Logger logger = LoggerFactory.getLogger("");
        logger.generateSessionId();
        ServiceFactory serviceFactory =ServiceFactory.newInstance();
        ServiceConfigure serviceConfigure = serviceFactory.getServiceConfigure();
        InterfaceMetadata interfaceMetadata = InterfaceMetadata.builder().channel("test-channel").txNo("010").version("1").interfaceClass(HelloService.class).interfaceMethod(HelloService.class.getMethod("hello", HelloRequest.class)).build();
        serviceFactory.getMetadataRegister().register(interfaceMetadata);
        InterfaceDefinition interfaceDefinition = InterfaceDefinition.builder().channel("test-channel").txNo("010").version("1").build();
        InterfaceChannel interfaceChannel = new InterfaceChannel();
        interfaceChannel.setChannel("test-channel");
        interfaceChannel.getInterfaces().add(interfaceDefinition);
        serviceFactory.getDefinitionRegister().register(interfaceChannel);
        serviceConfigure.setInterfaceConnectorClass(MockHelloFailureInterfaceConnector.class);
        HelloService helloService = ServiceProxyFactory.newInstance(serviceFactory, HelloService.class);
        final HelloRequest request = new HelloRequest();
        request.setName("test");
        AsyncTask asyncTask = helloService.hello(request, new AsyncHandler<HelloResponse>() {
            @Override
            public void fail(String code, String desc, String detail) {
                logger.debug("fail");
                Assert.assertEquals("998", code);
                Assert.assertEquals("系统正在维护，请稍后重试！", desc);
            }

            @Override
            public void success(HelloResponse response) {
                logger.debug("success");
                Assert.fail("不应该到这里");
            }
        });
        asyncTask.get();
    }
}