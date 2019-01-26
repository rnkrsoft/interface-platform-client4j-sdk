package com.rnkrsoft.platform.client;

/**
 * Created by rnkrsoft.com on 2018/7/5.
 * 位置提供者
 */
public interface LocationProvider {
    /**
     * 定位位置
     *
     * @param locationStore 位置存储对象
     */
    void locate(LocationStore locationStore);
}
