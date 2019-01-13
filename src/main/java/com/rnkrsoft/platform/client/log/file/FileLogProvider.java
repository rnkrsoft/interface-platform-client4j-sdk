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
package com.rnkrsoft.platform.client.log.file;

import com.rnkrsoft.platform.client.log.LogProvider;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rnkrsoft.com on 2018/12/13.
 */
public class FileLogProvider implements LogProvider {
    /**
     * 时间戳
     */
    private volatile String dateStamp = "";
    /**
     * Instant when the log daily rotation was last checked.
     */
    private volatile long rotationLastChecked = 0L;
    /**
     * 日志前缀
     */
    protected String prefix = "interface-platform";
    /**
     * The suffix that is added to log file filenames.
     */
    protected String suffix = "log";
    /**
     * 日志存放路径
     */
    private String directory = "logs";
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

    public FileLogProvider() {
    }

    public FileLogProvider(String directory, String prefix, String suffix) {
        this.directory = directory;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public void init() {
        // Create the directory if necessary
        File dir = new File(directory);
        if (!dir.mkdirs() && !dir.isDirectory()) {
            System.err.println("dir " + dir + "is not exists!");
            return;
        }
        // Open the current log file
        File pathname;
        if (format == null) {
            this.format = new SimpleDateFormat("yyyyMMdd");
        }
        this.dateStamp = this.format.format(new Date());
        // If no rotate - no need for dateStamp in fileName
        if (rotatable) {
            pathname = new File(dir.getAbsoluteFile(), prefix + ((dateStamp == null || dateStamp.isEmpty()) ? "" : "." + dateStamp) + ((suffix == null || suffix.isEmpty()) ? "" : "." + suffix));
        } else {
            pathname = new File(dir.getAbsoluteFile(), prefix + ((suffix == null || suffix.isEmpty()) ? "" : "." + suffix));
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
    public void log(String log) {
        long systime = System.currentTimeMillis();
        if ((systime - rotationLastChecked) > 1000) {
            File dir = new File(directory);
            if (!dir.mkdirs() && !dir.isDirectory()) {
                System.err.println("open " + dir + " happens error!");
                return;
            }
            synchronized (this) {
                if ((systime - rotationLastChecked) > 1000) {
                    rotationLastChecked = systime;
                    String tsDate;
                    // Check for a change of date
                    tsDate = format.format(new Date(systime));
                    // If the date has changed, switch log files
                    if (!dateStamp.equals(tsDate)) {
                        close();
                        dateStamp = tsDate;
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
                dateStamp = format.format(new Date(System.currentTimeMillis()));
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
     * Open the new log file for the date specified by <code>dateStamp</code>.
     */
    protected synchronized void open() {
        // Create the directory if necessary
        File dir = new File(directory);
        if (!dir.mkdirs() && !dir.isDirectory()) {
            System.err.println("open " + dir + " happens error!");
            return;
        }

        this.dateStamp = this.format.format(new Date());
        // Open the current log file
        File pathname;
        // If no rotate - no need for dateStamp in fileName
        if (rotatable) {
            pathname = new File(dir.getAbsoluteFile(), prefix  + ((dateStamp == null || dateStamp.isEmpty()) ? "" : "." + dateStamp) + ((suffix == null || suffix.isEmpty()) ? "" : "." + suffix));
        } else {
            pathname = new File(dir.getAbsoluteFile(), prefix + ((suffix == null || suffix.isEmpty()) ? "" : "." + suffix));
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
    public void destroy() {
        close();
    }

    @Override
    public void append(int level, String log) {
        log(log);
    }

    public FileLogProvider setDirectory(String directory) {
        this.directory = directory;
        return this;
    }

    public FileLogProvider setSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    public FileLogProvider setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public FileLogProvider setRotationLastChecked(long rotationLastChecked) {
        this.rotationLastChecked = rotationLastChecked;
        return this;
    }

    public FileLogProvider setRotatable(boolean rotatable) {
        this.rotatable = rotatable;
        return this;
    }

    public String getDirectory() {
        return directory;
    }

    public File getCurrentLogFile() {
        return currentLogFile;
    }
}
