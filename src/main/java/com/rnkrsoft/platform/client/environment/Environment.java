package com.rnkrsoft.platform.client.environment;

/**
 * Created by rnrkrsoft.com on 2019/1/22.
 */
public interface Environment {
    String getOsVersion();
    String getDeviceManufacturer();
    String getDeviceModel();
    String getDeviceType();
}
