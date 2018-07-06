package com.rnkrsoft.platform.demo.domains;


import javax.web.doc.AbstractResponse;
import javax.web.doc.annotation.ApidocElement;

/**
 * Created by rnkrsoft.com on 2018/6/19.
 */
public class DemoResponse extends AbstractResponse{
    @ApidocElement("登录用户相关信息")
    ManageVo dict;

    public ManageVo getDict() {
        return dict;
    }

    public void setDict(ManageVo dict) {
        this.dict = dict;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DemoResponse{");
        sb.append("dict=").append(dict);
        sb.append('}');
        return sb.toString();
    }
}
