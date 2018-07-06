package com.rnkrsoft.platform.protocol.service;

import com.rnkrsoft.platform.client.AsyncHandler;

import javax.web.doc.annotation.ApidocInterface;
import javax.web.doc.annotation.ApidocService;
import java.util.concurrent.Future;

/**
 * Created by rnkrsoft.com on 2018/6/27.
 */
@ApidocService("发布服务")
public interface PublishService {
    @ApidocInterface(value = "拉取已发布接口", name = "000", version = "1")
    Future fetchPublish(FetchPublishRequest request, AsyncHandler<FetchPublishResponse> asyncHandler);
}
