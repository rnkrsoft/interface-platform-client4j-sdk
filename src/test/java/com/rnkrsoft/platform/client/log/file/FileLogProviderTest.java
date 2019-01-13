package com.rnkrsoft.platform.client.log.file;

import com.rnkrsoft.platform.client.log.Log;
import com.rnkrsoft.platform.client.log.LogLevel;
import com.rnkrsoft.platform.client.log.LogProvider;
import org.junit.Test;

import java.util.logging.Level;

import static org.junit.Assert.*;

/**
 * Created by woate on 2018/12/13.
 */
public class FileLogProviderTest {

    @Test
    public void testLog() throws Exception {
        LogProvider log = new FileLogProvider("./target", "test", "log");
        log.init();
        for (int i = 0; i < 100; i++) {
            log.append(LogLevel.INFO, "sssssssssssssss");
        }
    }
}