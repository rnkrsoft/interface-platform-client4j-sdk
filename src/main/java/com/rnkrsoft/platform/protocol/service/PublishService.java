package com.rnkrsoft.platform.protocol.service;

import javax.web.doc.annotation.ApidocInterface;
import javax.web.doc.annotation.ApidocService;

/**
 * Created by woate on 2018/6/27.
 */
@ApidocService("发布服务")
public interface PublishService {
    @ApidocInterface(value = "拉去发布接口", name = "000", version = "1")
    FetchPublishResponse fetchPublish(FetchPublishRequest request);
}
