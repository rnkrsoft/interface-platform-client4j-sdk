package com.rnkrsoft.platform.protocol;

import com.rnkrsoft.interfaces.EnumIntegerCode;
import com.rnkrsoft.interfaces.EnumStringCode;


/**
 * Created by rnkrsoft.com on 2018/4/20.
 */
public class ApiResponse {
    /**
     * 数据
     */
    String data;
    /**
     * 签字
     */
    String sign;
    /**
     * 应答码
     */
    String code = "000";
    /**
     * 应答描述
     */
    String desc = "成功";

    public String getData() {
        return data;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public final void setCode(EnumStringCode code) {
        this.code = code.getCode();
        this.desc = code.getDesc();
    }

    public final void setCode(EnumIntegerCode code) {
        this.code = Integer.toString(code.getCode());
        this.desc = code.getDesc();
    }
}
