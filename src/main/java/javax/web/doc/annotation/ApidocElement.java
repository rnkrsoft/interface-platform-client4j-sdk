package javax.web.doc.annotation;

import javax.web.doc.enums.PatternType;
import javax.web.doc.enums.ValueDisplayType;
import javax.web.skeleton4j.annotation.WebCascadeInterface;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by rnkrsoft.com on 2017/12/5.
 * 用于在接口上标注字段
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface ApidocElement {
    /**
     * 是否为唯一主键，标记为唯一组件，则可用于管理系统中表格中按钮事件的传值
     *
     * @return 是否为唯一主键
     */
    boolean unique() default false;

    /**
     * 字段描述
     *
     * @return 字段描述
     */
    String value() default "";

    /**
     * 是否为只读
     *
     * @return 是否为只读
     */
    boolean readonly() default false;

    /**
     * 最大长度
     *
     * @return 最大长度
     */
    int maxLen() default 255;

    /**
     * 最小长度
     *
     * @return 最小长度
     */
    int minLen() default 0;

    /**
     * 是否必输
     *
     * @return 是否必输
     */
    boolean required() default true;

    /**
     * 字段类型
     * ValueDisplayType.String将数据展示为输入框
     * ValueDisplayType.Integer将数据展示为输入框
     * ValueDisplayType.Decimal将数据展示为输入框
     * ValueDisplayType.Boolean将数据展示为状态切换框
     * ValueDisplayType.List将数据展示为表格
     * ValueDisplayType.Date将数据展示为日期选择框，字段必须为String
     * ValueDisplayType.Image将数据展示为图片，字段必须为String
     * ValueDisplayType.Tree将数据展示为Tree，单选时字段为String,复选时字段为Set或者List
     * ValueDisplayType.Markdown将数据展示为Markdown编辑器，字段必须为String
     * ValueDisplayType.RichText将数据展示为富文本编辑器，字段必须为String
     * ValueDisplayType.Json将数据展示为Json编辑器，字段必须为String
     * ValueDisplayType.Selection将数据展示为可以选择的单选或则复选，单选时字段为String,复选时字段为Set或者List
     *
     * @return 字段类型
     */
    ValueDisplayType valueDisplayType() default ValueDisplayType.Auto;

    /**
     * 枚举字典类
     *
     * @return 枚举类，如果未定义则为Object
     */
    Class enumClass() default Object.class;

    /**
     * 默认值,可以定义组初始值
     *
     * @return 默认值列表
     */
    String[] defaults() default {};

    /**
     * 占位符，如果为空，则默认使用desc
     *
     * @return 占位符
     */
    String placeholder() default "";

    /**
     * 表达式类型，默认正则表达式
     *
     * @return 表达式类型
     */
    PatternType patternType() default PatternType.AUTO;

    /**
     * 表达式
     *
     * @return 表达式
     */
    String[] pattern() default {};

    /**
     * 级联接口，用于定义元素的级联接口事件
     *
     * @return 级联接口列表
     */
    WebCascadeInterface[] interfaces() default {};

    /**
     * 是否为隐藏字段
     *
     * @return 否为隐藏
     */
    boolean hidden() default false;
}
