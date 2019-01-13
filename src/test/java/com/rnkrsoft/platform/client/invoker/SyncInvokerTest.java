package com.rnkrsoft.platform.client.invoker;

import com.rnkrsoft.platform.client.ServiceConfigure;
import com.rnkrsoft.platform.client.ServiceFactory;
import com.rnkrsoft.platform.client.ServiceRegistry;
import com.rnkrsoft.platform.client.connector.http.HttpInterfaceConnector;
import com.rnkrsoft.platform.client.connector.mock.MockHelloInterfaceConnector;
import com.rnkrsoft.platform.client.connector.mock.MockHelloInterfaceConnector2;
import com.rnkrsoft.platform.client.connector.mock.MockInterfaceConnector;
import com.rnkrsoft.platform.demo.service.HelloRequest;
import com.rnkrsoft.platform.demo.service.HelloResponse;
import com.rnkrsoft.platform.demo.service.HelloService;
import com.rnkrsoft.platform.client.scanner.InterfaceMetadata;
import com.rnkrsoft.platform.protocol.service.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by rnkrsoft.com on 2018/10/6.
 */
public class SyncInvokerTest {

    @Test
    public void testSync() throws Exception {
        FetchPublishRequest request = new FetchPublishRequest();
        request.getChannels().add("car_manage");
        ServiceConfigure serviceConfigure = new ServiceConfigure();
        serviceConfigure.settingFallback("car_manage", "http", "47.96.169.97", 8001, "api");
        serviceConfigure.setInterfaceConnectorClass(HttpInterfaceConnector.class);
        Object response = SyncInvoker.call(serviceConfigure, PublishService.class, "fetchPublish", FetchPublishRequest.class, FetchPublishResponse.class, request);
        System.out.println(response);
    }

    @Test
    public void testSync2() throws Exception {
        InterfaceMetadata metadata = new InterfaceMetadata();
        metadata.setChannel("test-channel");
        metadata.setTxNo("010");
        metadata.setVersion("1");
        metadata.setInterfaceClass(HelloService.class);
        metadata.setInterfaceMethod(HelloService.class.getMethod("hello", HelloRequest.class));
        Map metadatas = new HashMap();
        metadatas.put("test-channel", new HashSet(Arrays.asList(metadata)));
        ServiceRegistry.initMetadataSet(metadatas);
        InterfaceChannel channel = new InterfaceChannel();
        channel.setChannel("test-channel");
        InterfaceDefinition interfaceDefinition = new InterfaceDefinition();
        interfaceDefinition.setChannel("test-channel");
        interfaceDefinition.setTxNo("010");
        interfaceDefinition.setVersion("1");
        interfaceDefinition.setEncryptAlgorithm("AES");
        interfaceDefinition.setDecryptAlgorithm("");
        interfaceDefinition.setSignAlgorithm("SHA512");
        interfaceDefinition.setVerifyAlgorithm("");
        interfaceDefinition.setUseTokenAsPassword(false);
        channel.getInterfaces().add(interfaceDefinition);
        ServiceRegistry.initChannels(Arrays.asList(channel));
        HelloRequest request = new HelloRequest();
        request.setName("张三");
        ServiceConfigure serviceConfigure = ServiceFactory.getServiceConfigure();
        serviceConfigure.setPassword("");
        serviceConfigure.setInterfaceConnectorClass(MockHelloInterfaceConnector.class);
        Object response = SyncInvoker.call(serviceConfigure, HelloService.class, "hello", HelloRequest.class, HelloResponse.class, request);
        System.out.println(response);
    }

    @Test
    public void testSync3() throws Exception {
        InterfaceMetadata metadata = new InterfaceMetadata();
        metadata.setChannel("test-channel");
        metadata.setTxNo("010");
        metadata.setVersion("1");
        metadata.setInterfaceClass(HelloService.class);
        metadata.setInterfaceMethod(HelloService.class.getMethod("hello", HelloRequest.class));
        Map metadatas = new HashMap();
        metadatas.put("test-channel", new HashSet(Arrays.asList(metadata)));
        ServiceRegistry.initMetadataSet(metadatas);
        InterfaceChannel channel = new InterfaceChannel();
        channel.setChannel("test-channel");
        InterfaceDefinition interfaceDefinition = new InterfaceDefinition();
        interfaceDefinition.setChannel("test-channel");
        interfaceDefinition.setTxNo("010");
        interfaceDefinition.setVersion("1");
        interfaceDefinition.setEncryptAlgorithm("AES");
        interfaceDefinition.setDecryptAlgorithm("AES");
        interfaceDefinition.setSignAlgorithm("SHA512");
        interfaceDefinition.setVerifyAlgorithm("");
        interfaceDefinition.setUseTokenAsPassword(false);
        channel.getInterfaces().add(interfaceDefinition);
        ServiceRegistry.initChannels(Arrays.asList(channel));
        HelloRequest request = new HelloRequest();
        request.setName("张三");
        ServiceConfigure serviceConfigure = ServiceFactory.getServiceConfigure();
        serviceConfigure.setPassword("");
        serviceConfigure.setInterfaceConnectorClass(MockHelloInterfaceConnector2.class);
        Object response = SyncInvoker.call(serviceConfigure, HelloService.class, "hello", HelloRequest.class, HelloResponse.class, request);
        System.out.println(response);
    }
}