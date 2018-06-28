package com.rnkrsoft.platform.protocol;

/**
 * Created by liucheng on 2018/6/13.
 */
public interface UserInfoReadable {
    /**
     * 获取用户号
     * @return
     */
    int getUserId();

    /**
     * 获取用户名
     * @return
     */
    String getUserName();
}
