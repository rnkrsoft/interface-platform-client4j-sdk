package com.rnkrsoft.platform.protocol.service;

import com.rnkrsoft.platform.protocol.domains.PublishRequest;
import com.rnkrsoft.platform.protocol.domains.PublishResponse;

import javax.web.doc.annotation.ApidocInterface;
import javax.web.doc.annotation.ApidocService;

/**
 * Created by woate on 2018/6/27.
 */
@ApidocService("发布服务")
public interface PublishService {
    @ApidocInterface(value = "推送", name = "000", version = "1")
    PublishResponse publish(PublishRequest request);
}
