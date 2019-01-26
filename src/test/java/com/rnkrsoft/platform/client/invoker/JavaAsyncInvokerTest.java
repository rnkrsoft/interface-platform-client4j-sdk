package com.rnkrsoft.platform.client.invoker;

import com.rnkrsoft.platform.client.InterfaceMetadata;
import com.rnkrsoft.platform.client.ServiceConfigure;
import com.rnkrsoft.platform.client.ServiceFactory;
import com.rnkrsoft.platform.client.connector.MockHelloFailureInterfaceConnector;
import com.rnkrsoft.platform.client.connector.MockHelloSuccessInterfaceConnector;
import com.rnkrsoft.platform.client.demo.domain.HelloRequest;
import com.rnkrsoft.platform.client.demo.domain.HelloResponse;
import com.rnkrsoft.platform.client.demo.service.HelloService;
import com.rnkrsoft.platform.client.logger.Logger;
import com.rnkrsoft.platform.client.logger.LoggerFactory;
import com.rnkrsoft.platform.protocol.ApiResponse;
import com.rnkrsoft.platform.protocol.AsyncHandler;
import com.rnkrsoft.platform.protocol.service.InterfaceChannel;
import com.rnkrsoft.platform.protocol.service.InterfaceDefinition;
import com.rnkrsoft.platform.protocol.service.PublishService;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

/**
 * Created by rnkrsoft.com on 2019/1/21.
 */
public class JavaAsyncInvokerTest {

    @Test
    public void testDoInBackgroundFailure() throws Exception {
        final Logger logger = LoggerFactory.getLogger("");
        logger.debug("begin");
        ServiceFactory serviceFactory = new ServiceFactory();
        ServiceConfigure serviceConfigure = serviceFactory.getServiceConfigure();
        serviceConfigure.setInterfaceConnectorClass(MockHelloFailureInterfaceConnector.class);
        serviceConfigure.settingFallback("test-channel", true, "localhost", 80, "api");
        InterfaceMetadata interfaceMetadata = InterfaceMetadata.builder().channel("test-channel").txNo("010").version("1").interfaceClass(HelloService.class).interfaceMethod(HelloService.class.getMethod("hello", HelloRequest.class)).build();
        InterfaceDefinition interfaceDefinition = InterfaceDefinition.builder().channel("test-channel").txNo("010").version("1").build();
        InterfaceChannel interfaceChannel = new InterfaceChannel();
        interfaceChannel.setChannel("test-channel");
        interfaceChannel.getInterfaces().add(interfaceDefinition);
        serviceFactory.getMetadataRegister().register(interfaceMetadata);
        serviceFactory.getDefinitionRegister().register(interfaceChannel);
        HelloRequest request = new HelloRequest();
        request.setName("test");
        JavaAsyncInvoker<HelloRequest> javaAsyncInvoker = new JavaAsyncInvoker<HelloRequest>(serviceFactory, UUID.randomUUID().toString(), PublishService.class, "fetchPublish", HelloRequest.class, HelloResponse.class, new AsyncHandler<HelloResponse>() {
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
        javaAsyncInvoker.execute(request);
        ApiResponse apiResponse = javaAsyncInvoker.get();
        logger.info("结果{}", apiResponse);
    }


    @Test
    public void testDoInBackgroundSuccess() throws Exception {
        final Logger logger = LoggerFactory.getLogger("");
        logger.debug("begin");
        ServiceFactory serviceFactory = new ServiceFactory();
        ServiceConfigure serviceConfigure = serviceFactory.getServiceConfigure();
        serviceConfigure.setInterfaceConnectorClass(MockHelloSuccessInterfaceConnector.class);
        serviceConfigure.settingFallback("test-channel", true, "localhost", 80, "api");
        InterfaceMetadata interfaceMetadata = InterfaceMetadata.builder().channel("test-channel").txNo("010").version("1").interfaceClass(HelloService.class).interfaceMethod(HelloService.class.getMethod("hello", HelloRequest.class)).build();
        InterfaceDefinition interfaceDefinition = InterfaceDefinition.builder().channel("test-channel").txNo("010").version("1").build();
        InterfaceChannel interfaceChannel = new InterfaceChannel();
        interfaceChannel.setChannel("test-channel");
        interfaceChannel.getInterfaces().add(interfaceDefinition);
        serviceFactory.getMetadataRegister().register(interfaceMetadata);
        serviceFactory.getDefinitionRegister().register(interfaceChannel);
        final HelloRequest request = new HelloRequest();
        request.setName("test");
        JavaAsyncInvoker<HelloRequest> javaAsyncInvoker = new JavaAsyncInvoker<HelloRequest>(serviceFactory, UUID.randomUUID().toString(), PublishService.class, "fetchPublish", HelloRequest.class, HelloResponse.class, new AsyncHandler<HelloResponse>() {
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
        javaAsyncInvoker.execute(request);
        logger.info("等待结果");
        ApiResponse apiResponse = javaAsyncInvoker.get();
        logger.info("结果{}", apiResponse);
    }
}