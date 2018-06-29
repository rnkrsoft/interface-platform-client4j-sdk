package com.rnkrsoft.platform.protocol.service;

import javax.web.doc.AbstractResponse;
import javax.web.doc.annotation.ApidocElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by woate on 2018/6/27.
 */
public class FetchPublishResponse extends AbstractResponse{
    @ApidocElement("渠道")
    String channel;
    @ApidocElement("接口列表")
    final List<InterfaceDefinition> interfaces = new ArrayList();

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public List<InterfaceDefinition> getInterfaces() {
        return interfaces;
    }
}
