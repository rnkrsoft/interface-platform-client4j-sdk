package com.rnkrsoft.platform.client.demo.domain;

import lombok.ToString;

import javax.web.doc.annotation.ApidocElement;
import java.io.Serializable;

/**
 * Created by rnkrsoft.com on 2018/10/6.
 */
@ToString
public class HelloRequest implements Serializable{
    @ApidocElement("姓名")
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
