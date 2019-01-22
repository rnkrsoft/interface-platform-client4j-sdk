package com.rnkrsoft.platform.client.environment;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by rnkrsoft.com on 2019/1/22.
 */
public class EnvironmentDetectorTest {

    @Test
    public void testGetOsVersion() throws Exception {
        EnvironmentDetector environmentDetector = new EnvironmentDetector();
        System.out.println(environmentDetector.getDeviceManufacturer());
        System.out.println(environmentDetector.getDeviceModel());
        System.out.println(environmentDetector.getDeviceType());
        System.out.println(environmentDetector.getOsVersion());
    }

    @Test
    public void testGetDeviceManufacturer() throws Exception {

    }

    @Test
    public void testGetDeviceModel() throws Exception {

    }

    @Test
    public void testGetDeviceType() throws Exception {

    }
}