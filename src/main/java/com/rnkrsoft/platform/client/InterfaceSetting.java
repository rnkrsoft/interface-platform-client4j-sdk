package com.rnkrsoft.platform.client;

/**
 * Created by rnrkrsoft.com on 2018/12/10.
 */
public class InterfaceSetting {
    /**
     * 密钥向量
     */
    String keyVector;

    /**
     * HTTP连接超时时间
     */
    Integer httpConnectTimeoutSecond;

    /**
     * HTTP读取时间
     */
    Integer httpReadTimeoutSecond;

    public String getKeyVector() {
        return keyVector;
    }

    public Integer getHttpConnectTimeoutSecond() {
        return httpConnectTimeoutSecond;
    }

    public Integer getHttpReadTimeoutSecond() {
        return httpReadTimeoutSecond;
    }

    public static InterfaceSettingBuilder builder() {
        return new InterfaceSettingBuilder();
    }

    public static class InterfaceSettingBuilder {
        /**
         * 密钥向量
         */
        String keyVector;

        /**
         * HTTP连接超时时间
         */
        Integer httpConnectTimeoutSecond;

        /**
         * HTTP读取时间
         */
        Integer httpReadTimeoutSecond;

        public InterfaceSettingBuilder keyVector(String keyVector) {
            this.keyVector = keyVector;
            return this;
        }

        public InterfaceSettingBuilder httpConnectTimeoutSecond(Integer httpConnectTimeoutSecond) {
            this.httpConnectTimeoutSecond = httpConnectTimeoutSecond;
            return this;
        }

        public InterfaceSettingBuilder httpReadTimeoutSecond(Integer httpReadTimeoutSecond) {
            this.httpReadTimeoutSecond = httpReadTimeoutSecond;
            return this;
        }

        public InterfaceSetting build() {
            InterfaceSetting setting = new InterfaceSetting();
            setting.httpConnectTimeoutSecond = this.httpConnectTimeoutSecond;
            setting.httpReadTimeoutSecond = this.httpReadTimeoutSecond;
            setting.keyVector = this.keyVector;
            return setting;
        }
    }
}
