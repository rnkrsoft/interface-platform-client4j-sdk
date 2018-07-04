package com.rnkrsoft.platform.protocol.service;

import javax.web.doc.annotation.ApidocElement;
import java.io.Serializable;

/**
 * Created by rnkrsoft.com on 2018/6/27.
 */
public class FetchPublishRequest implements Serializable{
    @ApidocElement("通道")
    String channel;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
