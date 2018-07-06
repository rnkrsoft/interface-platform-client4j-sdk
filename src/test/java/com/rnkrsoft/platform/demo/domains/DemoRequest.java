package com.rnkrsoft.platform.demo.domains;


import javax.web.doc.annotation.ApidocElement;

/**
 * Created by rnkrsoft.com on 2018/6/19.
 */
public class DemoRequest {
    @ApidocElement(value = "手机号码",required = true)
    String mobilePhone;
    @ApidocElement(value = "登录密码",required = true)
    String password;

    @ApidocElement(value = "用户号",required = false)
    String userId;
    @ApidocElement(value = "用户名",required = false)
    String userName;

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
