package com.rnkrsoft.platform.protocol;


import java.io.Serializable;

/**
 * Created by liucheng on 2018/4/20.
 */
public class ApiRequest implements Serializable{
    /**
     * 全局唯一序号,或外部订单号
     */
    String sessionId;
    /**
     * 用户ID
     */
    String uid;
    /**
     * 用户识别码
     */
    String uic;
    /**
     * 交易码
     */
    String txNo;
    /**
     * 版本号
     */
    String version;
    /**
     * 客户端类型
     * 1:用户端
     * 2:车管端
     */
    int type = 1;
    /**
     * 请求数据
     */
    String data;
    /**
     * 时间戳 yyyyMMddHHmmssSSS
     */
    String timestamp;
    /**
     * 用户标识
     */
    String token;
    /**
     * 参数签名
     */
    String sign;
    /**
     * 经度
     */
    Double lat;
    /**
     * 纬度
     */
    Double lng;

    public void setClientType(ClientTypeEnum clientType){
        this.type = clientType.code;
    }

    public ClientTypeEnum getClientType(){
        return ClientTypeEnum.valueOfCode(type);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUic() {
        return uic;
    }

    public void setUic(String uic) {
        this.uic = uic;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
