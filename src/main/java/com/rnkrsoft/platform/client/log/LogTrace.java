/**
 * RNKRSOFT OPEN SOURCE SOFTWARE LICENSE TERMS ver.1
 * - 氡氪网络科技(重庆)有限公司 开源软件许可条款(版本1)
 * 氡氪网络科技(重庆)有限公司 以下简称Rnkrsoft。
 * 这些许可条款是 Rnkrsoft Corporation（或您所在地的其中一个关联公司）与您之间达成的协议。
 * 请阅读本条款。本条款适用于所有Rnkrsoft的开源软件项目，任何个人或企业禁止以下行为：
 * .禁止基于删除开源代码所附带的本协议内容、
 * .以非Rnkrsoft的名义发布Rnkrsoft开源代码或者基于Rnkrsoft开源源代码的二次开发代码到任何公共仓库,
 * 除非上述条款附带有其他条款。如果确实附带其他条款，则附加条款应适用。
 * <p/>
 * 使用该软件，即表示您接受这些条款。如果您不接受这些条款，请不要使用该软件。
 * 如下所述，安装或使用该软件也表示您同意在验证、自动下载和安装某些更新期间传输某些标准计算机信息以便获取基于 Internet 的服务。
 * <p/>
 * 如果您遵守这些许可条款，将拥有以下权利。
 * 1.阅读源代码和文档
 * 如果您是个人用户，则可以在任何个人设备上阅读、分析、研究Rnkrsoft开源源代码。
 * 如果您经营一家企业，则禁止在任何设备上阅读Rnkrsoft开源源代码,禁止分析、禁止研究Rnkrsoft开源源代码。
 * 2.编译源代码
 * 如果您是个人用户，可以对Rnkrsoft开源源代码以及修改后产生的源代码进行编译操作，编译产生的文件依然受本协议约束。
 * 如果您经营一家企业，不可以对Rnkrsoft开源源代码以及修改后产生的源代码进行编译操作。
 * 3.二次开发拓展功能
 * 如果您是个人用户，可以基于Rnkrsoft开源源代码进行二次开发，修改产生的元代码同样受本协议约束。
 * 如果您经营一家企业，不可以对Rnkrsoft开源源代码进行任何二次开发，但是可以通过联系Rnkrsoft进行商业授予权进行修改源代码。
 * 完整协议。本协议以及开源源代码附加协议，共同构成了Rnkrsoft开源软件的完整协议。
 * <p/>
 * 4.免责声明
 * 该软件按“原样”授予许可。 使用本文档的风险由您自己承担。Rnkrsoft 不提供任何明示的担保、保证或条件。
 * 5.版权声明
 * 本协议所对应的软件为 Rnkrsoft 所拥有的自主知识产权，如果基于本软件进行二次开发，在不改变本软件的任何组成部分的情况下的而二次开发源代码所属版权为贵公司所有。
 */
package com.rnkrsoft.platform.client.log;

import com.rnkrsoft.platform.client.utils.DateUtil;
import com.rnkrsoft.platform.client.utils.MessageFormatter;

/**
 * Created by rnkrsoft.com on 2018/8/7.
 * 日志跟踪类
 */
public abstract class LogTrace {
    /**
     * 日志提供者
     */
    protected LogProvider logProvider;
    /**
     * 调试模式
     */
    boolean debug = false;
    /**
     * 啰嗦日志
     */
    boolean verboseLog = false;

    /**
     * 获取每次请求的会话号
     *
     * @return
     */
    public abstract String getSessionId();

    /**
     * 记录日志
     *
     * @param format 日志格式
     * @param args   参数
     */
    public synchronized void debug(String format, Object... args) {
        String format0 = "{} sessionId[{}] [" + Thread.currentThread().getName() + "] DEBUG " + format;
        Object[] args0 = new Object[args.length + 2];
        args0[0] = DateUtil.getDate();
        args0[1] = getSessionId();
        System.arraycopy(args, 0, args0, 2, args.length);
        String log = MessageFormatter.format(format0, args0);
        if (isVerboseLog()) {
            System.out.println(log);
        }
        if (this.logProvider != null && isDebug()) {
            this.logProvider.append(LogLevel.DEBUG, log);
        }
    }

    public synchronized void info(String format, Object... args) {
        String format0 = "{} sessionId[{}] [" + Thread.currentThread().getName() + "] INFO " + format;
        Object[] args0 = new Object[args.length + 2];
        args0[0] = DateUtil.getDate();
        args0[1] = getSessionId();
        System.arraycopy(args, 0, args0, 2, args.length);
        String log = MessageFormatter.format(format0, args0);
        if (isVerboseLog()) {
            System.out.println(log);
        }
        if (this.logProvider != null) {
            this.logProvider.append(LogLevel.INFO, log);
        }
    }

    /**
     * 记录日志
     *
     * @param format 日志格式
     * @param args   参数
     */
    public synchronized void error(String format, Object... args) {
        String format0 = "{} sessionId[{}] [" + Thread.currentThread().getName() + "] ERROR " + format;
        Object[] args0 = new Object[args.length + 2];
        args0[0] = DateUtil.getDate();
        args0[1] = getSessionId();
        System.arraycopy(args, 0, args0, 2, args.length);
        String log = MessageFormatter.format(format0, args0);
        if (isVerboseLog()) {
            System.err.println(log);
        }
        if (this.logProvider != null) {
            this.logProvider.append(LogLevel.ERROR, log);
        }
    }

    public void setLogProvider(LogProvider logProvider) {
        this.logProvider = logProvider;
    }

    /**
     * 是否为调试模式
     *
     * @return 调试模式
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * 开启调试模式
     */
    public synchronized void enableDebug() {
        this.debug = true;
    }

    /**
     * 关闭调试模式
     */
    public synchronized void disableDebug() {
        this.debug = false;
    }

    /**
     * 判断是否为啰嗦模式
     *
     * @return 是否为啰嗦模式
     */
    public boolean isVerboseLog() {
        return verboseLog;
    }

    /**
     * 开启日志啰嗦模式
     */
    public synchronized void enableVerboseLog() {
        this.verboseLog = true;
    }

    /**
     * 关闭日志啰嗦模式
     */
    public synchronized void disableVerboseLog() {
        this.verboseLog = false;
    }
}
