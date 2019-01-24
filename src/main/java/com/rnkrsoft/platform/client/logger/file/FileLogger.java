package com.rnkrsoft.platform.client.logger.file;

import com.rnkrsoft.config.ConfigProvider;
import com.rnkrsoft.message.MessageFormatter;
import com.rnkrsoft.platform.client.logger.AbstractLogger;
import com.rnkrsoft.platform.client.logger.AndroidLogger;
import com.rnkrsoft.platform.client.logger.Logger;
import com.rnkrsoft.platform.client.logger.LoggerLevel;
import com.rnkrsoft.platform.client.utils.ExceptionTrackUtils;
import com.rnkrsoft.platform.protocol.utils.JavaEnvironmentDetector;
import com.rnkrsoft.utils.ClassUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rnkrsoft.com on 2019/1/18.
 */
class FileLogger extends AbstractLogger implements Logger {


    /**
     * 时间戳
     */
    private volatile String timestamp = "";
    /**
     * Instant when the log daily rotation was last checked.
     */
    private volatile long rotationLastChecked = 0L;
    /**
     * Should we rotate our log file? Default is true (like old behavior)
     */
    protected boolean rotatable = true;
    /**
     * The PrintWriter to which we are currently logging, if any.
     */
    protected PrintWriter writer = null;
    /**
     * The current log file we are writing to. Helpful when checkExists
     * is true.
     */
    protected File currentLogFile = null;

    private SimpleDateFormat format;

    ConfigProvider config;

    public FileLogger(ConfigProvider config) {
        this.config = config;
    }

    void init() {
        // Create the directory if necessary
        File dir = new File(config.getString(LoggerConstant.LOGGER_DIRECTORY));
        if (!dir.mkdirs() && !dir.isDirectory()) {
            System.err.println("dir " + dir + "is not exists!");
            return;
        }
        // Open the current log file
        File pathname;
        if (format == null) {
            this.format = new SimpleDateFormat("yyyyMMdd");
        }
        this.timestamp = this.format.format(new Date());
        // If no rotate - no need for timestamp in fileName
        if (rotatable) {
            pathname = new File(dir.getAbsoluteFile(), config.getString(LoggerConstant.LOGGER_PREFIX) + ((timestamp == null || timestamp.isEmpty()) ? "" : "." + timestamp) + ((config.getString(LoggerConstant.LOGGER_SUFFIX) == null || config.getString(LoggerConstant.LOGGER_SUFFIX).isEmpty()) ? "" : "." + config.getString(LoggerConstant.LOGGER_SUFFIX)));
        } else {
            pathname = new File(dir.getAbsoluteFile(), config.getString(LoggerConstant.LOGGER_PREFIX) + ((config.getString(LoggerConstant.LOGGER_SUFFIX) == null || config.getString(LoggerConstant.LOGGER_SUFFIX).isEmpty()) ? "" : "." + config.getString(LoggerConstant.LOGGER_SUFFIX)));
        }
        File parent = pathname.getParentFile();
        if (!parent.mkdirs() && !parent.isDirectory()) {
            System.err.println("parent " + parent + "is not exists!");
            return;
        }

        Charset charset = null;
        if (charset == null) {
            charset = Charset.forName("UTF-8");
        }
        try {
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathname, true), charset), 128000), false);
            currentLogFile = pathname;
        } catch (IOException e) {
            writer = null;
            System.err.println("open " + pathname + " happens error!");
            currentLogFile = null;
        }
    }

    /**
     * Log the specified message to the log file, switching files if the date
     * has changed since the previous log call.
     */
    void log(String log) {
        long systemTime = System.currentTimeMillis();
        if ((systemTime - rotationLastChecked) > 1000) {
            File dir = new File(config.getString(LoggerConstant.LOGGER_DIRECTORY));
            if (!dir.mkdirs() && !dir.isDirectory()) {
                System.err.println("open " + dir + " happens error!");
                return;
            }
            synchronized (this) {
                if ((systemTime - rotationLastChecked) > 1000) {
                    rotationLastChecked = systemTime;
                    String tsDate;
                    // Check for a change of date
                    tsDate = format.format(new Date(systemTime));
                    // If the date has changed, switch log files
                    if (!timestamp.equals(tsDate)) {
                        close();
                        timestamp = tsDate;
                        open();
                    }
                }
            }
        }
        /* In case something external rotated the file instead */
        synchronized (this) {
            if (currentLogFile != null && !currentLogFile.exists()) {
                try {
                    close();
                } catch (Throwable e) {
                    System.err.println("write log happens error!");
                    return;
                }
                /* Make sure date is correct */
                timestamp = format.format(new Date(System.currentTimeMillis()));
                open();
            }
        }

        // Log this message
        synchronized (this) {
            if (writer != null) {
                writer.println(log);
                writer.flush();
            }
        }
    }

    /**
     * Open the new log file for the date specified by <code>timestamp</code>.
     */
    protected synchronized void open() {
        // Create the directory if necessary
        File dir = new File(config.getString(LoggerConstant.LOGGER_DIRECTORY));
        if (!dir.mkdirs() && !dir.isDirectory()) {
            System.err.println("open " + dir + " happens error!");
            return;
        }

        this.timestamp = this.format.format(new Date());
        // Open the current log file
        File pathname;
        // If no rotate - no need for timestamp in fileName
        if (rotatable) {
            pathname = new File(dir.getAbsoluteFile(), config.getString(LoggerConstant.LOGGER_PREFIX) + ((timestamp == null || timestamp.isEmpty()) ? "" : "." + timestamp) + ((config.getString(LoggerConstant.LOGGER_SUFFIX) == null || config.getString(LoggerConstant.LOGGER_SUFFIX).isEmpty()) ? "" : "." + config.getString(LoggerConstant.LOGGER_SUFFIX)));
        } else {
            pathname = new File(dir.getAbsoluteFile(), config.getString(LoggerConstant.LOGGER_PREFIX) + ((config.getString(LoggerConstant.LOGGER_SUFFIX) == null || config.getString(LoggerConstant.LOGGER_SUFFIX).isEmpty()) ? "" : "." + config.getString(LoggerConstant.LOGGER_SUFFIX)));
        }
        File parent = pathname.getParentFile();
        if (!parent.mkdirs() && !parent.isDirectory()) {
            System.err.println("open " + parent + " happens error!");
            return;
        }

        Charset charset = null;
        if (charset == null) {
            charset = Charset.forName("UTF-8");
        }
        try {
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(pathname, true), charset), 128000),
                    false);
            currentLogFile = pathname;
        } catch (IOException e) {
            writer = null;
            System.err.println("write log happens error!");
            return;
        }
    }

    /**
     * Close the currently open log file (if any)
     */
    private synchronized void close() {
        if (writer == null) {
            return;
        }
        writer.flush();
        writer.close();
        writer = null;
        currentLogFile = null;
    }


    @Override
    public boolean isTraceEnabled() {
        return LoggerLevel.valueOf(config.getString(LoggerConstant.LOGGER_LEVEL)).ordinal() <= LoggerLevel.TRACE.ordinal();
    }

    void log(LoggerLevel level, String className, String log) {
        if (config.getBoolean(LoggerConstant.LOGGER_SOUT, false)) {
            if (JavaEnvironmentDetector.isAndroid()) {
                AndroidLogger.println(level, className, log);
            } else {
                System.out.println(log);
            }
        }
        log(log);
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (isTraceEnabled()) {
            String log = getFormat() + " TRACE " + MessageFormatter.format(format, arguments);
            log(LoggerLevel.TRACE, ClassUtils.getClassName(true, 1), log);
        }
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (isTraceEnabled()) {
            String log = getFormat() + " TRACE " + msg + "\n" + ExceptionTrackUtils.toString(t);
            log(LoggerLevel.TRACE, ClassUtils.getClassName(true, 1), log);
        }
    }

    @Override
    public boolean isDebugEnabled() {
        return LoggerLevel.valueOf(config.getString(LoggerConstant.LOGGER_LEVEL)).ordinal() <= LoggerLevel.DEBUG.ordinal();
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (isDebugEnabled()) {
            String log = getFormat() + " DEBUG " + MessageFormatter.format(format, arguments);
            log(LoggerLevel.DEBUG, ClassUtils.getClassName(true, 1), log);
        }
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (isDebugEnabled()) {
            String log = getFormat() + " DEBUG " + msg + "\n" + ExceptionTrackUtils.toString(t);
            log(LoggerLevel.DEBUG, ClassUtils.getClassName(true, 1), log);
        }
    }

    @Override
    public boolean isInfoEnabled() {
        return LoggerLevel.valueOf(config.getString(LoggerConstant.LOGGER_LEVEL)).ordinal() <= LoggerLevel.INFO.ordinal();
    }

    @Override
    public void info(String format, Object... arguments) {
        if (isInfoEnabled()) {
            String log = getFormat() + " INFO " + MessageFormatter.format(format, arguments);
            log(LoggerLevel.INFO, ClassUtils.getClassName(true, 1), log);
        }
    }

    @Override
    public void info(String msg, Throwable t) {
        if (isInfoEnabled()) {
            String log = getFormat() + " INFO " + msg + "\n" + ExceptionTrackUtils.toString(t);
            log(LoggerLevel.INFO, ClassUtils.getClassName(true, 1), log);
        }
    }

    @Override
    public boolean isWarnEnabled() {
        return LoggerLevel.valueOf(config.getString(LoggerConstant.LOGGER_LEVEL)).ordinal() <= LoggerLevel.WARN.ordinal();
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (isWarnEnabled()) {
            String log = getFormat() + " WARN " + MessageFormatter.format(format, arguments);
            log(LoggerLevel.WARN, ClassUtils.getClassName(true, 1), log);
        }
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (isWarnEnabled()) {
            String log = getFormat() + " WARN " + msg + "\n" + ExceptionTrackUtils.toString(t);
            log(LoggerLevel.WARN, ClassUtils.getClassName(true, 1), log);
        }
    }

    @Override
    public boolean isErrorEnabled() {
        return LoggerLevel.valueOf(config.getString(LoggerConstant.LOGGER_LEVEL)).ordinal() <= LoggerLevel.ERROR.ordinal();
    }

    @Override
    public void error(String format, Object... arguments) {
        if (isErrorEnabled()) {
            String log = getFormat() + " ERROR " + MessageFormatter.format(format, arguments);
            log(LoggerLevel.ERROR, ClassUtils.getClassName(true, 1), log);
        }
    }

    @Override
    public void error(String msg, Throwable t) {
        if (isErrorEnabled()) {
            String log = getFormat() + " ERROR " + msg + "\n" + ExceptionTrackUtils.toString(t);
            log(LoggerLevel.ERROR, ClassUtils.getClassName(true, 1), log);
        }
    }
}