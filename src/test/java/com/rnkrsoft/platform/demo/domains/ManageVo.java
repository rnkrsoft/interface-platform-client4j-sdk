package com.rnkrsoft.platform.demo.domains;


import java.io.Serializable;

import javax.web.doc.annotation.ApidocElement;

/**
 * Created by lijie 2018年6月5日11:00:52
 *
 */

public class ManageVo implements Serializable{
    @ApidocElement("管理员名称")
    String manageName;
    @ApidocElement("管理员token")
    String token;
    @ApidocElement("管理员类型 1:运维专员,2:运维主管,3:车务主管")
    Integer manageType;
    @ApidocElement("任务状态：0:下班,1:上班,2:任务中")
    Integer state;
    @ApidocElement("服务城市id")
    Integer host;
    @ApidocElement("上班车场")
    String workAreas;

    public String getManageName() {
        return manageName;
    }

    public void setManageName(String manageName) {
        this.manageName = manageName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getManageType() {
        return manageType;
    }

    public void setManageType(Integer manageType) {
        this.manageType = manageType;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getHost() {
        return host;
    }

    public void setHost(Integer host) {
        this.host = host;
    }

    public String getWorkAreas() {
        return workAreas;
    }

    public void setWorkAreas(String workAreas) {
        this.workAreas = workAreas;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ManageVo{");
        sb.append("manageName='").append(manageName).append('\'');
        sb.append(", token='").append(token).append('\'');
        sb.append(", manageType=").append(manageType);
        sb.append(", state=").append(state);
        sb.append(", host=").append(host);
        sb.append(", workAreas='").append(workAreas).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
