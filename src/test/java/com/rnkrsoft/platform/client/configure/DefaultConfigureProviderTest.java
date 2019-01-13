package com.rnkrsoft.platform.client.configure;

import com.rnkrsoft.com.google.gson.GsonBuilder;
import com.rnkrsoft.platform.client.Configure;
import com.rnkrsoft.platform.client.ConfigureProvider;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by woate on 2018/10/7.
 */
public class DefaultConfigureProviderTest {

    @Test
    public void testLoad() throws Exception {
        ConfigureProvider configureProvider = new DefaultConfigureProvider();
        Configure configure = configureProvider.load("http", "localhost", 80, "/configure", Arrays.asList("test-channel"), "222", "ANDROID", "2.1.1", 1.1, 2.2);
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(configure));
    }
}