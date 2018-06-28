package com.rnkrsoft.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SHA摘要算法
 */
public class SHA {
    /**
     * 传入文本内容，返回 SHA-512 串
     *
     * @param source 原文串
     * @return 哈希值
     */
    public static String SHA512(final String source) {
        return SHA(source, "SHA-512");
    }

    /**
     * 字符串 SHA 加密
     *
     * @param source  原文串
     * @param strType 算法类型
     * @return 哈希值
     */
    static String SHA(final String source, final String strType) {
        // 返回值
        String strResult = null;
        // 是否是有效字符串
        if (source != null && source.length() > 0) {
            try {
                // SHA 加密开始
                // 创建加密对象 并傳入加密類型
                MessageDigest messageDigest = MessageDigest.getInstance(strType);
                // 传入要加密的字符串
                try {
                    messageDigest.update(source.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                   throw new RuntimeException(e);
                }
                // 得到 byte 類型结果
                byte byteBuffer[] = messageDigest.digest();

                // 將 byte 轉換爲 string
                StringBuffer strHexString = new StringBuffer();
                // 遍歷 byte buffer
                for (int i = 0; i < byteBuffer.length; i++) {
                    String hex = Integer.toHexString(0xff & byteBuffer[i]);
                    if (hex.length() == 1) {
                        strHexString.append('0');
                    }
                    strHexString.append(hex);
                }
                // 得到返回結果
                strResult = strHexString.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        return strResult;
    }
}  