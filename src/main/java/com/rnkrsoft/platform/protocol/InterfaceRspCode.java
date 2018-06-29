package com.rnkrsoft.platform.protocol;

import com.rnkrsoft.interfaces.EnumStringCode;

/**
 * Created by rnkrsoft.com on 2018/5/7.
 */
public enum InterfaceRspCode implements EnumStringCode {
    SUCCESS("000","成功"),
    PARAM_IS_NULL("001","参数不能为空"),
    TIMESTAMP_ILLEGAL("002","请校准手机系统时间"),
    REQUEST_SIGN_ILLEGAL("003","请求签字信息无效"),
    ACCOUNT_HAS_LOGIN_ON_OTHER_DEVICE("004","您的帐号已在其它设备登录"),
    PARAM_TYPE_NOT_MATCH("005","参数类型不匹配"),
    USER_IS_NOT_LOGIN("006","用户未登录"),
    INTERFACE_IS_ILLEGAL("007","接口无效"),
    INTERFACE_IS_DEV("008","接口正在开发"),
    INTERFACE_HAPPEN_UNKNOWN_ERROR("009","接口发生未知错误"),
    INTERFACE_EXECUTE_HAPPENS_ERROR("010","接口执行发生错误"),
    INTERFACE_NOT_DEFINED("011","接口未定义"),
    INTERFACE_SERVICE_CLASS_NOT_FOUND("012","接口对应的服务类不存在"),
    INTERFACE_SERVICE_METHOD_NOT_EXIST("013","接口对应的服务方法不存在"),
    INTERFACE_EXISTS_OTHER_VERSION("014","存在其他版本的接口，但不存在当前版本号的接口"),
    CHANNEL_NOT_EXISTS("015","接口通道不存在"),
    DECRYPT_HAPPENS_FAIL("016","解密发生错误"),
    VERIFY_HAPPENS_FAIL("017","验签发生错误"),
    SIGN_HAPPENS_FAIL("018","签字发生错误"),
    ENCRYPT_HAPPENS_FAIL("019","加密发生错误"),
    TOKEN_ILLEGAL("020","TOKEN无效"),
    UPDATE_REQUEST_HAPPENS_ERROR("021","更新请求信息发生错误"),
    UPDATE_RESPONSE_HAPPENS_ERROR("022","更新应答信息发生错误"),
    REQUEST_DATA_IS_NULL("023","请求数据为空"),
    TX_NO_IS_NULL("024","交易码为空"),
    TOKEN_SERVICE_NOT_EXISTS("025","TOKEN服务不存在"),
    DATA_CONVERT_HAPPENS_ERROR("026","数据转换发生错误"),
    DATA_CONVERT_SERVICE_EXISTS_ERROR("027","数据转换服务存在错误"),
    DEVICE_CAN_NOT_ACCESS_INTERNET("028", "设备不能访问互联网"),
    FAIL("999","错误");
    String code;
    String desc;

    InterfaceRspCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public static InterfaceRspCode valueOfCode(String code) {
        for (InterfaceRspCode value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return FAIL;
    }
}
