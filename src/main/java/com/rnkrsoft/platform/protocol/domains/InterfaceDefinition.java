package com.rnkrsoft.platform.protocol.domains;

/**
 * Created by woate on 2018/6/27.
 */
public class InterfaceDefinition{
    /**
     * 渠道
     */
    String channel;
    /**
     * 交易码
     */
    String txNo;
    /**
     * 版本号
     */
    String version;
    /**
     * 验签算法
     */
    String verifyAlgorithm;
    /**
     * 签字算法
     */
    String signAlgorithm;
    /**
     * 解密算法
     */
    String decryptAlgorithm;
    /**
     * 加密算法
     */
    String encryptAlgorithm;
    /**
     * 验签先于解密
     */
    boolean firstVerifySecondDecrypt = true;
    /**
     * 签字先于加密
     */
    boolean firstSignSecondEncrypt = false;
    /**
     * 是否将TOKEN用作加密和签字的密钥
     */
    boolean useTokenAsPassword = true;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTxNo() {
        return txNo;
    }

    public void setTxNo(String txNo) {
        this.txNo = txNo;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVerifyAlgorithm() {
        return verifyAlgorithm;
    }

    public void setVerifyAlgorithm(String verifyAlgorithm) {
        this.verifyAlgorithm = verifyAlgorithm;
    }

    public String getSignAlgorithm() {
        return signAlgorithm;
    }

    public void setSignAlgorithm(String signAlgorithm) {
        this.signAlgorithm = signAlgorithm;
    }

    public String getDecryptAlgorithm() {
        return decryptAlgorithm;
    }

    public void setDecryptAlgorithm(String decryptAlgorithm) {
        this.decryptAlgorithm = decryptAlgorithm;
    }

    public String getEncryptAlgorithm() {
        return encryptAlgorithm;
    }

    public void setEncryptAlgorithm(String encryptAlgorithm) {
        this.encryptAlgorithm = encryptAlgorithm;
    }

    public boolean isFirstVerifySecondDecrypt() {
        return firstVerifySecondDecrypt;
    }

    public void setFirstVerifySecondDecrypt(boolean firstVerifySecondDecrypt) {
        this.firstVerifySecondDecrypt = firstVerifySecondDecrypt;
    }

    public boolean isFirstSignSecondEncrypt() {
        return firstSignSecondEncrypt;
    }

    public void setFirstSignSecondEncrypt(boolean firstSignSecondEncrypt) {
        this.firstSignSecondEncrypt = firstSignSecondEncrypt;
    }

    public boolean isUseTokenAsPassword() {
        return useTokenAsPassword;
    }

    public void setUseTokenAsPassword(boolean useTokenAsPassword) {
        this.useTokenAsPassword = useTokenAsPassword;
    }
}
