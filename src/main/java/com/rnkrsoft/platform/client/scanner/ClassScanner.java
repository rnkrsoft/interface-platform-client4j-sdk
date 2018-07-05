package com.rnkrsoft.platform.client.scanner;


import com.rnkrsoft.utils.JavaEnvironmentDetector;

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
