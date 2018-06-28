package com.rnkrsoft.platform.protocol.domains;

/**
 * Created by woate on 2018/6/27.
 */
public class InterfaceDefinition {
    String txNo;
    String version;
    String verifyAlgorithm;
    String signAlgorithm;
    String decryptAlgorithm;
    String encryptAlgorithm;
    Boolean firstVerifySecondDecrypt;
    Boolean firstSignSecondEncrypt;

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

    public Boolean getFirstVerifySecondDecrypt() {
        return firstVerifySecondDecrypt;
    }

    public void setFirstVerifySecondDecrypt(Boolean firstVerifySecondDecrypt) {
        this.firstVerifySecondDecrypt = firstVerifySecondDecrypt;
    }

    public Boolean getFirstSignSecondEncrypt() {
        return firstSignSecondEncrypt;
    }

    public void setFirstSignSecondEncrypt(Boolean firstSignSecondEncrypt) {
        this.firstSignSecondEncrypt = firstSignSecondEncrypt;
    }
}
