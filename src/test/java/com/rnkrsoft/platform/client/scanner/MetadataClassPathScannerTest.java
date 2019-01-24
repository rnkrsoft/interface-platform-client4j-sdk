package com.rnkrsoft.platform.client.scanner;

import com.rnkrsoft.platform.client.demo.service.HelloService;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by rnkrsoft.com on 2019/1/19.
 */
public class MetadataClassPathScannerTest {

    @Test
    public void testScan() throws Exception {
        Collection<Class> classes = new ArrayList<Class>();
        classes.add(HelloService.class);
        Map map = MetadataClassPathScanner.scan(classes);
        System.out.println(map);
    }
}