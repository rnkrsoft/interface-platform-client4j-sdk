package com.rnkrsoft.platform.demo.service;

import javax.web.doc.AbstractResponse;
import javax.web.doc.annotation.ApidocElement;

/**
 * Created by rnkrsoft.com on 2018/10/6.
 */
public class HelloResponse extends AbstractResponse{
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
