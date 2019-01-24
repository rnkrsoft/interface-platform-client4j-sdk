package com.rnkrsoft.platform.client.invoker;

import com.rnkrsoft.platform.client.InterfaceMetadata;
import com.rnkrsoft.platform.client.ServiceConfigure;
import com.rnkrsoft.platform.client.ServiceFactory;
import com.rnkrsoft.platform.client.connector.MockHelloFailureInterfaceConnector;
import com.rnkrsoft.platform.client.connector.MockHelloSuccessInterfaceConnector;
import com.rnkrsoft.platform.client.demo.domain.HelloRequest;
import com.rnkrsoft.platform.client.demo.domain.HelloResponse;
import com.rnkrsoft.platform.client.demo.service.HelloService;
import com.rnkrsoft.platform.client.exception.RemoteInterfaceExecutionException;
import com.rnkrsoft.platform.protocol.service.InterfaceChannel;
import com.rnkrsoft.platform.protocol.service.InterfaceDefinition;
import com.rnkrsoft.platform.protocol.service.PublishService;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by rnkrsoft.com on 2019/1/20.
 */
public class SyncInvokerTest {

    @Test
    public void testCallSuccess() throws Exception {
        ServiceFactory serviceFactory = new ServiceFactory();
        ServiceConfigure serviceConfigure = serviceFactory.getServiceConfigure();
        serviceConfigure.setInterfaceConnectorClass(MockHelloSuccessInterfaceConnector.class);
        serviceConfigure.settingFallback("test-channel", true, "localhost", 80, "api");
        SyncInvoker invoker = new SyncInvoker();
        InterfaceMetadata interfaceMetadata = InterfaceMetadata.builder().channel("test-channel").txNo("010").version("1").interfaceClass(HelloService.class).interfaceMethod(HelloService.class.getMethod("hello", HelloRequest.class)).build();
        InterfaceDefinition interfaceDefinition = InterfaceDefinition.builder().channel("test-channel").txNo("010").version("1").build();
        InterfaceChannel interfaceChannel = new InterfaceChannel();
        interfaceChannel.setChannel("test-channel");
        interfaceChannel.getInterfaces().add(interfaceDefinition);
        serviceFactory.getMetadataRegister().register(interfaceMetadata);
        serviceFactory.getDefinitionRegister().register(interfaceChannel);
        HelloRequest request = new HelloRequest();
        request.setName("test");
        HelloResponse response = (HelloResponse) invoker.call(serviceFactory, PublishService.class, "fetchPublish", HelloRequest.class, HelloResponse.class, request);
        System.out.println(response);
        Assert.assertEquals("0000", response.getRspCode());
        Assert.assertEquals("成功", response.getRspDesc());
        Assert.assertEquals("hello," + request.getName(), response.getText());
    }

    @Test
    public void testCallFailure() throws Exception {
        ServiceFactory serviceFactory = new ServiceFactory();
        ServiceConfigure serviceConfigure = serviceFactory.getServiceConfigure();
        serviceConfigure.setInterfaceConnectorClass(MockHelloFailureInterfaceConnector.class);
        serviceConfigure.settingFallback("test-channel", true, "localhost", 80, "api");
        SyncInvoker invoker = new SyncInvoker();
        InterfaceMetadata interfaceMetadata = InterfaceMetadata.builder().channel("test-channel").txNo("010").version("1").interfaceClass(HelloService.class).interfaceMethod(HelloService.class.getMethod("hello", HelloRequest.class)).build();
        InterfaceDefinition interfaceDefinition = InterfaceDefinition.builder().channel("test-channel").txNo("010").version("1").build();
        InterfaceChannel interfaceChannel = new InterfaceChannel();
        interfaceChannel.setChannel("test-channel");
        interfaceChannel.getInterfaces().add(interfaceDefinition);
        serviceFactory.getMetadataRegister().register(interfaceMetadata);
        serviceFactory.getDefinitionRegister().register(interfaceChannel);
        HelloRequest request = new HelloRequest();
        request.setName("test");
        try {
            HelloResponse response = (HelloResponse) invoker.call(serviceFactory, PublishService.class, "fetchPublish", HelloRequest.class, HelloResponse.class, request);
            Assert.fail("不应该到这里");
        } catch (RemoteInterfaceExecutionException e) {
            Assert.assertEquals("sync call is failure, cause 系统正在维护，请稍后重试！(998)!", e.getMessage());
        }
        Assert.fail("不应该到这里");
    }
}