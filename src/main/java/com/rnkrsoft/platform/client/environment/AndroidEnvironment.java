package com.rnkrsoft.platform.client.environment;

import android.os.Build;

/**
 * Created by rnkrsoft.com on 2019/1/22.
 */
public class AndroidEnvironment implements Environment{
    @Override
    public String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    public String getDeviceManufacturer(){
        return Build.MANUFACTURER;
    }

    public String getDeviceModel(){
        return Build.MODEL;
    }

    public String getDeviceType(){
        return "Android";
    }
}
