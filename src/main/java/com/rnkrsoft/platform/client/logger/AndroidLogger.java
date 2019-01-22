package com.rnkrsoft.platform.client.logger;

import android.util.Log;
import com.rnkrsoft.platform.client.logger.LoggerLevel;

/**
 * Created by rnkrsoft.com on 2019/1/22.
 */
public class AndroidLogger {
    /**
     * Priority constant for the println method; use Log.v.
     */
    public static final int VERBOSE = 2;

    /**
     * Priority constant for the println method; use Log.d.
     */
    public static final int DEBUG = 3;

    /**
     * Priority constant for the println method; use Log.i.
     */
    public static final int INFO = 4;

    /**
     * Priority constant for the println method; use Log.w.
     */
    public static final int WARN = 5;

    /**
     * Priority constant for the println method; use Log.e.
     */
    public static final int ERROR = 6;

    /**
     * Priority constant for the println method.
     */
    public static final int ASSERT = 7;
    public static void println(LoggerLevel level, String className, String log){
        int priority = DEBUG;
        if (LoggerLevel.TRACE == level){
            priority = VERBOSE;
        }else if (LoggerLevel.DEBUG == level){
            priority = DEBUG;
        }else if (LoggerLevel.INFO == level){
            priority = INFO;
        }else if (LoggerLevel.WARN == level){
            priority = WARN;
        }else if (LoggerLevel.ERROR == level){
            priority = ERROR;
        }else if (LoggerLevel.FATAL == level){
            priority = ASSERT;
        }
        Log.println(priority, className, log);
    }
}
