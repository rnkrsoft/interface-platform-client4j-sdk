package com.rnkrsoft.platform.client.invoker;

import com.rnkrsoft.platform.protocol.AsyncHandler;
import com.rnkrsoft.platform.client.ServiceConfigure;
import com.rnkrsoft.platform.client.ServiceFactory;
import com.rnkrsoft.platform.client.ServiceRegistry;
import com.rnkrsoft.platform.client.connector.mock.MockHelloInterfaceConnector;
import com.rnkrsoft.platform.client.connector.mock.MockHelloInterfaceConnector2;
import com.rnkrsoft.platform.client.connector.mock.MockInterfaceConnector;
import com.rnkrsoft.platform.demo.service.HelloRequest;
import com.rnkrsoft.platform.demo.service.HelloResponse;
import com.rnkrsoft.platform.demo.service.HelloService;
import com.rnkrsoft.platform.client.scanner.InterfaceMetadata;
import com.rnkrsoft.platform.protocol.service.*;
import org.junit.Test;

import java.util.*;

/**
 * Created by woate on 2018/10/6.
 */
public class AsyncInvokerTest {

    @Test
    public void testCall() throws Exception {
        FetchPublishRequest request = new FetchPublishRequest();
        request.getChannels().add("test-channel");
        ServiceConfigure serviceConfigure = new ServiceConfigure();
        serviceConfigure.setInterfaceConnectorClass(MockInterfaceConnector.class);
        AsyncInvoker asyncInvoker = new AsyncInvoker(UUID.randomUUID().toString(), serviceConfigure, PublishService.class, "fetchPublish", FetchPublishRequest.class, FetchPublishResponse.class, request, new AsyncHandler() {
            @Override
            public void fail(String code, String desc, String detail) {
                System.out.println(code);
            }

            @Override
            public void success(Object response) {
                System.out.println(response);
            }
        });
        asyncInvoker.call();
    }

    @Test
    public void testCall2() throws Exception {
        ServiceConfigure serviceConfigure = ServiceFactory.getServiceConfigure();
        serviceConfigure.setPassword("");
        serviceConfigure.setInterfaceConnectorClass(MockHelloInterfaceConnector.class);

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
        AsyncInvoker asyncInvoker = new AsyncInvoker(UUID.randomUUID().toString(), serviceConfigure, HelloService.class, "hello", HelloRequest.class, HelloResponse.class, request, new AsyncHandler() {
            @Override
            public void fail(String code, String desc, String detail) {
                System.out.println(code);
            }

            @Override
            public void success(Object response) {
                System.out.println(response);
            }
        });
        asyncInvoker.call();
    }

    @Test
    public void testCall3() throws Exception {
        ServiceConfigure serviceConfigure = ServiceFactory.getServiceConfigure();
        serviceConfigure.setPassword("");
        serviceConfigure.setInterfaceConnectorClass(MockHelloInterfaceConnector2.class);

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
        AsyncInvoker asyncInvoker = new AsyncInvoker(UUID.randomUUID().toString(), serviceConfigure, HelloService.class, "hello", HelloRequest.class, HelloResponse.class, request, new AsyncHandler() {
            @Override
            public void fail(String code, String desc, String detail) {
                System.out.println(code);
            }

            @Override
            public void success(Object response) {
                System.out.println(response);
            }
        });
        asyncInvoker.call();
    }
}