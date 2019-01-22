package com.rnkrsoft.platform.client.environment;

/**
 * Created by rnkrsoft.com on 2019/1/22.
 */
public class ServerEnvironment implements Environment{
    @Override
    public String getOsVersion() {
        return System.getProperty("os.version");
    }

    @Override
    public String getDeviceManufacturer() {
        return "PC";
    }

    @Override
    public String getDeviceModel() {
        return System.getProperty("os.arch");
    }

    @Override
    public String getDeviceType() {
        return System.getProperty("os.name");
    }
}
