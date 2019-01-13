package com.rnkrsoft.platform.client.configure;

import com.rnkrsoft.com.google.gson.Gson;
import com.rnkrsoft.com.google.gson.GsonBuilder;
import com.rnkrsoft.platform.client.Configure;
import com.rnkrsoft.platform.client.ConfigureProvider;
import org.junit.Test;

import java.util.Arrays;
import java.util.UUID;

/**
 * Created by rnkrsoft.com on 2018/10/6.
 */
public class MockConfigureProviderTest {

    @Test
    public void testLoad() throws Exception {
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        ConfigureProvider configureProvider = new MockConfigureProvider();
        Configure configure = configureProvider.load("http", "localhost", 80, "/", Arrays.asList("test"), UUID.randomUUID().toString(), "IOS", "1.0.0", 1.1, 2.2);
        System.out.println(gson.toJson(configure));
    }
}