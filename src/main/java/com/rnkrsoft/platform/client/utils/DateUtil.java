package com.rnkrsoft.platform.client.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by woate on 2018/7/4.
 */
public class DateUtil {
    public static String getTimestamp(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return dateFormat.format(new Date());
    }
    public static String getDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        return dateFormat.format(new Date());
    }
}
