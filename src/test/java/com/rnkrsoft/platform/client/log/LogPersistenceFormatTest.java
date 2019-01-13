package com.rnkrsoft.platform.client.log;

import org.junit.Test;

import java.io.File;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by rnbkrsoft.com on 2018/11/13.
 */
public class LogPersistenceFormatTest {

    @Test
    public void testGenerateZip() throws Exception {
        LogPersistenceFormat logPersistenceFormat = new LogPersistenceFormat();
        for (int i = 0; i < 10000; i++) {
            logPersistenceFormat.addLog(new Log(LogLevel.INFO, UUID.randomUUID().toString() + UUID.randomUUID().toString() + UUID.randomUUID().toString() + UUID.randomUUID().toString() + UUID.randomUUID().toString() + UUID.randomUUID().toString() + UUID.randomUUID().toString() + UUID.randomUUID().toString() + UUID.randomUUID().toString() + UUID.randomUUID().toString() + UUID.randomUUID().toString() + UUID.randomUUID().toString() + UUID.randomUUID().toString()));
        }
        logPersistenceFormat.generateZip(new File("D:/2.zip"));
    }
}