package com.rnkrsoft.platform.demo.service;

import com.rnkrsoft.platform.android.AsyncHandler;
import com.rnkrsoft.platform.demo.domains.DemoRequest;
import com.rnkrsoft.platform.demo.domains.DemoResponse;

import javax.web.doc.annotation.ApidocInterface;
import javax.web.doc.annotation.ApidocService;

/**
 * Created by rnkrsoft.com on 2018/6/19.
 */
@ApidocService("演示服务")
public interface DemoService {
    @ApidocInterface(value = "演示", name = "101", version = "1")
    void demo(DemoRequest request, AsyncHandler<DemoResponse> callback);
    @ApidocInterface(value = "演示", name = "101", version = "1")
    DemoResponse demo(DemoRequest request);
}
