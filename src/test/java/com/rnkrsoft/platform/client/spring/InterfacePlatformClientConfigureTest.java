package com.rnkrsoft.platform.client.spring;

import com.rnkrsoft.platform.client.async.AsyncTask;
import com.rnkrsoft.platform.client.demo.domain.HelloRequest;
import com.rnkrsoft.platform.client.demo.domain.HelloResponse;
import com.rnkrsoft.platform.client.demo.service.HelloService;
import com.rnkrsoft.platform.protocol.AsyncHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Created by rnkrsoft.com on 2019/1/22.
 */
@ContextConfiguration("classpath*:testContext-InterfacePlatformClientConfigureTest.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({DirtiesContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
public class InterfacePlatformClientConfigureTest{
    @Autowired
    HelloService helloService;

    @Test
    public void test1() throws Exception {
        HelloRequest request = new HelloRequest();
        request.setName("test");
        HelloResponse response = helloService.hello(request);
        System.out.println(response);
        Thread.sleep(60 * 1000);
    }


    @Test
    public void test3() throws Exception {
//        HelloService helloService = getBean(HelloService.class);
        HelloRequest request = new HelloRequest();
        request.setName("test");
        AsyncTask asyncTask = helloService.hello(request, new AsyncHandler<HelloResponse>() {
            @Override
            public void fail(String code, String desc, String detail) {
                System.out.println("--------------->" + desc);
            }

            @Override
            public void success(HelloResponse response) {
                System.out.println("--------------->" + response);
            }
        });
        asyncTask.get();
    }

    @Test
    public void test4() throws Exception {
//        HelloService helloService = getBean(HelloService.class);
        for (int i = 0; i < 100; i++) {
            HelloRequest request = new HelloRequest();
            request.setName("test" + i);
            AsyncTask asyncTask = helloService.hello(request, new AsyncHandler<HelloResponse>() {
                @Override
                public void fail(String code, String desc, String detail) {
                    System.out.println("--------------->" + desc);
                }

                @Override
                public void success(HelloResponse response) {
                    System.out.println("--------------->" + response);
                }
            });
        }
        Thread.sleep(60000);
    }
}