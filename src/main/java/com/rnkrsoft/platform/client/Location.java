package com.rnkrsoft.platform.client;

/**
 * Created by rnkrsoft.com on 2018/7/5.
 * 位置信息对象
 */
public final class Location {
    /**
     * 经度
     */
    double lng = 0;
    /**
     * 纬度
     */
    double lat = 0;

    public Location(double lng, double lat) {
        this.lng = lng;
        this.lat = lat;
    }
}