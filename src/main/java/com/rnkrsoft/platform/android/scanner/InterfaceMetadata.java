package com.rnkrsoft.platform.android.scanner;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Created by woate on 2018/6/27.
 */
public class InterfaceMetadata implements Serializable{
    /**
     * 交易码
     */
    String txNo;
    /**
     * 版本号
     */
    String version;
    /**
     * 接口服务类
     */
    Class interfaceClass;
    /**
     * 接口方法
     */
    Method interfaceMethod;

    public String getTxNo() {
        return txNo;
    }

    public void setTxNo(String txNo) {
        this.txNo = txNo;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Class getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public Method getInterfaceMethod() {
        return interfaceMethod;
    }

    public void setInterfaceMethod(Method interfaceMethod) {
        this.interfaceMethod = interfaceMethod;
    }
}
