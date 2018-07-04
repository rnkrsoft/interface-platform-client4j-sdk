package com.rnkrsoft.platform.demo.service;

import com.rnkrsoft.platform.client.AsyncHandler;
import com.rnkrsoft.platform.demo.domains.DemoRequest;
import com.rnkrsoft.platform.demo.domains.DemoResponse;

import javax.web.doc.annotation.ApidocInterface;
import javax.web.doc.annotation.ApidocService;
import java.util.concurrent.Future;

/**
 * Created by rnkrsoft.com on 2018/6/19.
 */
@ApidocService("演示服务")
public interface DemoService {
    @ApidocInterface(value = "演示", name = "001", version = "1")
    DemoResponse login(DemoRequest request);

    @ApidocInterface(value = "演示", name = "001", version = "1")
    Future<Boolean> login(DemoRequest request, AsyncHandler<DemoResponse> callback);
}
