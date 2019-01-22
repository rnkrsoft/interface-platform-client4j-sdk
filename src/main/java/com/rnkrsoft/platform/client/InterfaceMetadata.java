package com.rnkrsoft.platform.client;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Created by rnkrsoft.com on 2019/1/17.
 */
@Data
@ToString
@Builder
public class InterfaceMetadata implements Serializable {
    /**
     * 通道号
     */
    String channel;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InterfaceMetadata metadata = (InterfaceMetadata) o;

        if (!channel.equals(metadata.channel)) return false;
        if (!txNo.equals(metadata.txNo)) return false;
        return version.equals(metadata.version);

    }

    @Override
    public int hashCode() {
        int result = channel.hashCode();
        result = 31 * result + txNo.hashCode();
        result = 31 * result + version.hashCode();
        return result;
    }
}
