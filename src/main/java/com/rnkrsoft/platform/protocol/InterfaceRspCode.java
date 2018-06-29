package com.rnkrsoft.platform.protocol;

import com.rnkrsoft.interfaces.EnumStringCode;

/**
 * Created by rnkrsoft.com on 2018/5/7.
 */
public enum InterfaceRspCode implements EnumStringCode {
    SUCCESS("000", "成功"),
    PARAM_IS_NULL("001", "参数不能为空"),
    CALL_TIMESTAMP_ILLEGAL("002", "请校准手机系统时间"),
    ILLEGAL_CLIENT("003", "无效的客户端"),
    REQUEST_SIGN_ILLEGAL("004", "请求签字信息无效"),
    MULTI_INTERFACE_PARAM_NOT_MATCH("005", "多接口请求参数不匹配"),
    ACCOUNT_IS_DISABLED("006", "账户已禁用"),
    ACCOUNT_HAS_LOGIN_ON_OTHER_DEVICE("0007", "您的帐号已在其它设备登录"),
    PARAM_TYPE_NOT_MATCH("010", "参数类型不匹配"),
    USER_IS_NOT_LOGIN("011", "用户未登录"),
    INTERFACE_IS_ILLEGAL("008", "接口无效"),
    INTERFACE_IS_DEV("009", "接口正在开发"),
    INTERFACE_HAPPEN_UNKNOWN_ERROR("0012", "接口发生未知错误"),
    INTERFACE_EXECUTE_HAPPENS_ERROR("0012", "接口执行发生错误"),
    INTERFACE_NOT_DEFINED("013", "接口未定义"),
    INTERFACE_SERVICE_CLASS_NOT_FOUND("014", "接口对应的服务类不存在"),
    INTERFACE_SERVICE_METHOD_NOT_EXIST("015", "接口对应的服务方法不存在"),
    NOT_SUPPORT_USER_CLIENT("016", "不支持用户端调用"),
    DECRYPT_HAPPENS_FAIL("017", "解密发生错误"),
    VERIFY_HAPPENS_FAIL("017", "验签发生错误"),
    SIGN_HAPPENS_FAIL("017", "签字发生错误"),
    ENCRYPT_HAPPENS_FAIL("017", "加密发生错误"),
    TOKEN_ILLEGAL("018", "TOKEN无效"),
    UPDATE_REQUEST_HAPPENS_ERROR("019", "更新请求信息发生错误"),
    UPDATE_RESPONSE_HAPPENS_ERROR("020", "更新应答信息发生错误"),
    REQUEST_DATA_IS_NULL("021", "请求数据为空"),
    TX_NO_IS_NULL("022", "交易码为空"),
    TOKEN_SERVICE_NOT_EXISTS("023", "TOKEN服务不存在"),
    DATA_CONVERT_HAPPENS_ERROR("024", "数据转换发生错误"),
    DATA_CONVERT_SERVICE_EXISTS_ERROR("025", "数据转换服务存在错误"),
    REMOTE_SERVICE_UNREACHABLE("026", "远程服务器不可达"),
    REQUEST_DATA_INCOMPLETE("028", "请求数据不完整"),
    RESPONSE_DATA_INCOMPLETE("027", "应答数据不完整"),
    DEVICE_CAN_NOT_ACCESS_INTERNET("028", "设备不能访问互联网"),
    FAIL("999", "错误");
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
