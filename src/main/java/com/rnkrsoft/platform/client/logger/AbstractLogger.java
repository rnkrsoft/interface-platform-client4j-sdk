package com.rnkrsoft.platform.client.logger;

import com.rnkrsoft.time.DateStyle;
import com.rnkrsoft.utils.DateUtils;

import java.util.Date;
import java.util.UUID;

/**
 * Created by rnkrsoft.com on 2019/1/19.
 */
public abstract class AbstractLogger implements Logger {
    static ThreadLocal<String> SESSION_ID = new ThreadLocal<String>();

    /**
     * 获取每次请求的会话号
     *
     * @return
     */
    public String getSessionId() {
        String session = SESSION_ID.get();
        return session == null ? "" : session;
    }

    @Override
    public void setSessionId(String sessionId) {
        SESSION_ID.set(sessionId);
    }

    public String generateSessionId() {
        String session = UUID.randomUUID().toString();
        SESSION_ID.set(session);
        return session;
    }

    protected String getFormat() {
        return DateUtils.toString(new Date(), DateStyle.MILLI_FORMAT1) + " sessionId[" + getSessionId() + "] [" + Thread.currentThread().getName() + "]";
    }
}
