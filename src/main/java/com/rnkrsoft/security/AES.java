package com.rnkrsoft.security;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;


public class AES {
    public static String KEY_GENERATION_ALG = "PBKDF2WithHmacSHA1";
    public static final String AES_CBC_PKCS7_PADDING = "AES/CBC/PKCS5PADDING";
    public static String DEFAULT_IV = "1234567890654321";
    static int HASH_ITERATIONS = 1;
    static byte[] SALT = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0xA, 0xB, 0xC, 0xD, 0xE, 0xF}; // must save this for next time we want the key
    static int KEY_LENGTH = 256;

    public static String encrypt(String key, String value) {
        return encrypt(key, DEFAULT_IV, value);
    }

    public static String encrypt(String key, String initVector, String value) {
        SecretKey sk = null;
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(KEY_GENERATION_ALG);
            PBEKeySpec keySpec = new PBEKeySpec(key.toCharArray(), SALT, HASH_ITERATIONS, KEY_LENGTH);
            sk = secretKeyFactory.generateSecret(keySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
        // This is our secret key. We could just save this to a file instead of
        // regenerating it
        // each time it is needed. But that file cannot be on the device (too
        // insecure). It could
        // be secure if we kept it on a server accessible through https.
        byte[] skAsByteArray = sk.getEncoded();
        try {
            byte[] encodeBytes = Base64.encodeBase64(value.getBytes("UTF-8"));
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec secretKeySpec = new SecretKeySpec(skAsByteArray, "AES");
            Cipher cipher = Cipher.getInstance(AES_CBC_PKCS7_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
            byte[] encrypted = cipher.doFinal(encodeBytes);
            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String key, String value) {
        return decrypt(key, DEFAULT_IV, value);
    }

    public static String decrypt(String key, String initVector, String encrypted) {
        SecretKey sk = null;
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(KEY_GENERATION_ALG);
            PBEKeySpec keySpec = new PBEKeySpec(key.toCharArray(), SALT, HASH_ITERATIONS, KEY_LENGTH);
            sk = secretKeyFactory.generateSecret(keySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
        byte[] skAsByteArray = sk.getEncoded();
        try {
            byte[] data = Base64.decodeBase64(encrypted);
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec secretKeySpec = new SecretKeySpec(skAsByteArray, "AES");
            Cipher cipher = Cipher.getInstance(AES_CBC_PKCS7_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
            byte[] original = cipher.doFinal(data);
            byte[] output = Base64.decodeBase64(original);
            return new String(output, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}