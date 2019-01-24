package com.rnkrsoft.platform.client.demo.domain;

import lombok.ToString;

import javax.web.doc.AbstractResponse;
import javax.web.doc.annotation.ApidocElement;

/**
 * Created by rnkrsoft.com on 2018/10/6.
 */
@ToString
public class HelloResponse extends AbstractResponse {
    @ApidocElement("结果")
    String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "HelloResponse{" +
                "text='" + text + '\'' +
                '}';
    }
}
