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
package com.rnkrsoft.platform.client.scanner;


import com.rnkrsoft.platform.protocol.utils.JavaEnvironmentDetector;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by rnkrsoft.com on 2018/6/27.
 * 类扫描器
 * 不能扫描Java未配置权限的包，例如java.lang不能访问
 */
public class ClassScanner {
    ClassLoader classLoader;
    /**
     * 是否扫描子包
     */
    boolean scanSubPackage;

    final Collection<Class> classes = new LinkedHashSet();

    public ClassScanner(ClassLoader classLoader, boolean scanSubPackage) {
        this.classLoader = classLoader;
        this.scanSubPackage = scanSubPackage;
    }

    public ClassScanner(boolean scanSubPackage) {
        this(Thread.currentThread().getContextClassLoader(), scanSubPackage);
    }


    public ClassScanner() {
        this(Thread.currentThread().getContextClassLoader(), false);
    }

    public interface Filter {
        boolean accept(Class clazz);
    }

    /**
     * 什么也不做过滤器
     */
    static class NoThingFilter implements Filter {
        public boolean accept(Class clazz) {
            return true;
        }
    }

    /**
     * 不包含方法名过滤器
     */
    public static class ExcludeMethodNameFilter implements Filter {
        Set<String> methodNames;

        public ExcludeMethodNameFilter(String... methodNames) {
            this.methodNames = new HashSet(Arrays.asList(methodNames));
        }

        public boolean accept(Class clazz) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (this.methodNames.contains(method.getName())) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * 包含方法名过滤器
     */
    public static class IncludeMethodNameFilter implements Filter {
        Set<String> methodNames;

        public IncludeMethodNameFilter(String... methodNames) {
            this.methodNames = new HashSet(Arrays.asList(methodNames));
        }

        public boolean accept(Class clazz) {
            Set<String> names = new HashSet();
            for (Method method : clazz.getDeclaredMethods()) {
                if (this.methodNames.contains(method.getName())) {
                    names.add(method.getName());
                }
            }
            return names.size() == methodNames.size();
        }
    }

    /**
     * 只有是某一个父类的子类或者自身
     */
    public static class IsAFilter implements Filter {
        Class parent;

        public IsAFilter(Class parent) {
            this.parent = parent;
        }

        public boolean accept(Class clazz) {
            return clazz != null && parent.isAssignableFrom(clazz);
        }
    }

    /**
     * 只有是某一个父类的类
     */
    public static class AnnotatedWithFilter implements Filter {
        Class<? extends Annotation> annotation;

        public AnnotatedWithFilter(Class annotation) {
            this.annotation = annotation;
        }

        public boolean accept(Class clazz) {
            return clazz != null && clazz.isAnnotationPresent(annotation);
        }
    }

    public Collection<Class> getClasses() {
        return this.classes;
    }

    public ClassScanner clear() {
        this.classes.clear();
        return this;
    }

    /**
     * 扫描当前类加载器的包路径类
     *
     * @param _package 包路径
     * @return 集合
     */
    public ClassScanner scan(String _package) {
        return scan(_package, new NoThingFilter());
    }

    /**
     * 扫描当前类加载器的包路径类
     *
     * @param _package 包路径
     * @param filter   过滤器
     * @return
     */
    public ClassScanner scan(String _package, Filter filter) {
        return scan(_package, Thread.currentThread().getContextClassLoader(), filter);
    }

    /**
     * 扫描包路径
     *
     * @param _package    包路径
     * @param classLoader 类加载器
     * @param filter      过滤器
     * @return 集合
     */
    public ClassScanner scan(String _package, ClassLoader classLoader, Filter filter) {
        if (_package == null || _package.isEmpty()) {
            throw new NullPointerException("package is null");
        }
        if (_package.indexOf("/") != -1) {
            throw new IllegalArgumentException("package is not contain '/'");
        }

        String _dir = _package.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = classLoader.getResources(_dir);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    scanFile(_package, _dir, url, filter);
                } else if ("jar".equals(protocol)) {
                    scanJar(_package, _dir, url, filter);
                }
            }
        } catch (IOException e) {
            //nothing
        }
        return this;
    }

    void scanJar(String _package, String _dir, URL url, Filter filter) {
        JarFile jar;
        try {
            jar = ((JarURLConnection) url.openConnection()).getJarFile();
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                // 如果是以/开头的
                if (name.charAt(0) == '/') {
                    name = name.substring(1);
                }
                if (entry.isDirectory()) {
                    continue;
                } else {
                    int _path0_idx = name.lastIndexOf('/');
                    String _path0 = _path0_idx > -1 ? name.substring(0, _path0_idx) : "";
                    String _fileName0 = _path0_idx > -1 ? name.substring(_path0_idx + 1) : name;
                    int _fileName0_idx = name.lastIndexOf('.');
                    String _className0 = _fileName0_idx > -1 ? name.substring(_path0_idx + 1, _fileName0_idx) : _fileName0;
                    String _fileSuffix0 = _fileName0_idx > -1 ? name.substring(_fileName0_idx + 1) : "";
                    if (_fileSuffix0.equals("class") || (JavaEnvironmentDetector.isAndroid() ? _fileSuffix0.equals("dex") : false)) {
                        if (scanSubPackage ? _path0.startsWith(_dir) : _path0.equals(_dir)) {
                            String _package0 = _path0.replace('/', '.');
                            try {
                                Class clazz = Class.forName(_package0 + '.' + _className0, true, this.classLoader);
                                if (filter.accept(clazz)) {
                                    classes.add(clazz);
                                }
                            } catch (ClassNotFoundException e) {
                                //nothing
                            }
                        }
                    }
                }


            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void scanFile(String _package, String _dir, URL url, Filter filter) throws UnsupportedEncodingException, MalformedURLException {
        String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] files = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                //针对安卓平台优化识别dex文件
                return file.isDirectory() || file.getName().endsWith(".class") || (JavaEnvironmentDetector.isAndroid() ? file.getName().endsWith(".dex") : false);
            }
        });
        for (File file : files) {
            if (file.isDirectory()) {
                if (scanSubPackage) {
                    scanFile(_package + "." + file.getName(), _dir + "/" + file.getName(), file.toURI().toURL(), filter);
                }
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    String className0 = _package + '.' + className;
                    Class clazz = this.classLoader.loadClass(className0);
                    if (filter.accept(clazz)) {
                        classes.add(clazz);
                    }
                } catch (NoClassDefFoundError e) {
                    //nothing
                } catch (ClassNotFoundException e) {
                    //nothing
                }
            }
        }
    }
}
