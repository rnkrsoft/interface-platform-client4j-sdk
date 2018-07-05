package com.rnkrsoft.utils;

/**
 * Created by rnkrsoft.com on 2018/7/5.
 */
public class JavaEnvironmentDetector {
    /**
     * 是否为安卓环境
     * @return 为安卓环境返回真
     */
    public static boolean isAndroid(){
        try {
            Class.forName("android.content.Intent");
            return true;
        }catch (ClassNotFoundException e){
            return false;
        }
    }
}
