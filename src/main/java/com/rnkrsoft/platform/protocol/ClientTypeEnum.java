package com.rnkrsoft.platform.protocol;

import com.rnkrsoft.interfaces.EnumIntegerCode;

/**
 * Created by liucheng on 2018/5/7.
 * 客户端类型
 */
public enum ClientTypeEnum implements EnumIntegerCode {
    USER_APP(1, "用户端"),
    MANAGER_APP(2, "车管端");
    int code;
    String desc;

    ClientTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public static ClientTypeEnum valueOfCode(int code) {
        for (ClientTypeEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return USER_APP;
    }
}
