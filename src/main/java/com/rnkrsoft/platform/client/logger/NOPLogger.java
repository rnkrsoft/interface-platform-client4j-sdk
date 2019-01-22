package com.rnkrsoft.platform.client.logger;

/**
 * Created by rnkrsoft.com on 2019/1/18.
 */
class NOPLogger extends AbstractLogger implements Logger{
    static NOPLogger NOP_LOGGER = new NOPLogger();

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void trace(String format, Object... arguments) {

    }

    @Override
    public void trace(String msg, Throwable t) {

    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(String format, Object... arguments) {

    }

    @Override
    public void debug(String msg, Throwable t) {

    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public void info(String format, Object... arguments) {

    }

    @Override
    public void info(String msg, Throwable t) {

    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public void warn(String format, Object... arguments) {

    }

    @Override
    public void warn(String msg, Throwable t) {

    }

    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public void error(String format, Object... arguments) {

    }

    @Override
    public void error(String msg, Throwable t) {

    }
}
