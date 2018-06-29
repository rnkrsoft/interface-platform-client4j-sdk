package javax.web.doc.enums;

import com.rnkrsoft.interfaces.EnumIntegerCode;

/**
 * Created by devops4j on 2017/12/5.
 * 表达式类型
 */
public enum PatternType implements EnumIntegerCode {
    AUTO(1, "自动识别,按照正则表达式，ONGL表达式，格式顺序进行尝试"),
    /**
     * 正则表达式
     */
    REGEX(2, "正则表达式"),
    /**
     * ONGL表达式
     */
    ONGL(3, "ONGL表达式"),
    /**
     * 格式
     */
    FORMAT(4, "格式");

    int code;
    String desc;

    PatternType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
