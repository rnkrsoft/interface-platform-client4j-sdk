package com.rnkrsoft.platform.demo.service;

import com.rnkrsoft.platform.protocol.AsyncHandler;

import javax.web.doc.annotation.ApidocInterface;
import javax.web.doc.annotation.ApidocService;

/**
 * Created by rnkrsoft.com on 2018/10/6.
 */
@ApidocService(value = "Hello服务", channel = "test-channel", version = "1")
public interface HelloService {
    @ApidocInterface(value = "Hello", name = "010")
    HelloResponse hello(HelloRequest request);

    @ApidocInterface(value = "Hello", name = "010")
    void hello(HelloRequest request, AsyncHandler<HelloResponse> asyncHandler);

    @ApidocInterface(value = "Hello", name = "010")
    HelloResponse helloError(HelloRequest request);

    @ApidocInterface(value = "Hello", name = "010")
    HelloResponse helloFail(HelloRequest request);
}
