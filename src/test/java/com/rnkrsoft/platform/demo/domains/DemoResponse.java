package com.rnkrsoft.platform.demo.domains;


import javax.web.doc.AbstractResponse;

/**
 * Created by rnkrsoft.com on 2018/6/19.
 */
public class DemoResponse extends AbstractResponse{
    Integer age;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
