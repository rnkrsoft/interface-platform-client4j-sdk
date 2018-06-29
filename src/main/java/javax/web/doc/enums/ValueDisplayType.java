package javax.web.doc.enums;

/**
 * Created by rnkrsoft.com on 2017/12/5.
 * 字段值展示类型，用于将数据转换为可见的形式
 */
public enum ValueDisplayType {
    /**
     * 自动识别
     * java.lang.String映射到String
     * java.lang.Integer映射到Integer
     * java.lang.Long映射到Integer
     * java.lang.Boolean映射到Selection
     * java.util.Collection<String>映射到Selection
     * java.util.Collection<Integer>映射到Selection
     * java.util.Collection<Serialize>映射到Form
     */
    Auto,
    /**
     * 字符串
     */
    String,
    /**
     * 整数
     */
    Integer,
    /**
     * 浮点数
     */
    Decimal,
    /**
     * 列表
     */
    List,
    /**
     * 日期
     */
    Date,
    /**
     * 图片
     */
    Image,
    /**
     * 树
     */
    Tree,
    /**
     * 可供选择的类型，多选或者单选
     */
    Selection,
    /**
     * Markdown文本
     */
    Markdown,
    /**
     * 富文本
     */
    RichText,
    /**
     * JSON数据
     */
    Json,
    /**
     * 源代码数据
     */
    Source
}
