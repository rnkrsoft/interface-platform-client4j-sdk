package com.rnkrsoft.platform.client.environment;

import com.rnkrsoft.platform.protocol.utils.JavaEnvironmentDetector;

/**
 * Created by rnkrsoft.com on 2019/1/22.
 */
public class EnvironmentDetector implements Environment{
    final static Environment ANDROID = new AndroidEnvironment();
    final static Environment SERVER = new ServerEnvironment();
    @Override
    public String getOsVersion() {
        return JavaEnvironmentDetector.isAndroid() ? ANDROID.getOsVersion() : SERVER.getOsVersion();
    }

    @Override
    public String getDeviceManufacturer() {
        return JavaEnvironmentDetector.isAndroid() ? ANDROID.getDeviceManufacturer() : SERVER.getDeviceManufacturer();
    }

    @Override
    public String getDeviceModel() {
        return JavaEnvironmentDetector.isAndroid() ? ANDROID.getDeviceModel() : SERVER.getDeviceModel();
    }

    @Override
    public String getDeviceType() {
        return JavaEnvironmentDetector.isAndroid() ? ANDROID.getDeviceType() : SERVER.getDeviceType();
    }
}
