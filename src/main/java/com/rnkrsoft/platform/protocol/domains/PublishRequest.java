package com.rnkrsoft.platform.protocol.domains;

import javax.web.doc.annotation.ApidocElement;
import java.io.Serializable;

/**
 * Created by woate on 2018/6/27.
 */
public class PublishRequest implements Serializable{
    @ApidocElement("通道")
    String channel;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
