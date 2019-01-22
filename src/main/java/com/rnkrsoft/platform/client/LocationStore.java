package com.rnkrsoft.platform.client;

/**
 * Created by rnkrsoft.com on 2018/7/5.
 * 定位存储
 */
public interface LocationStore {
    /**
     * 刷新经纬度
     *
     * @param location 位置对象
     */
    void refreshLocation(Location location);
}