package javax.web.skeleton4j.annotation;

/**
 * Created by devops4j on 2017/12/7.
 * 用于定义级联接口
 */
public @interface WebCascadeInterface {
    /**
     * 级联接口
     * 格式别名=包名.类名.接口名:版本号，例如find=com.rnkrsoft.skeleton.UserService.findUser:1.0.0
     *
     * @return 接口引用格式
     */
    String value();

    /**
     * 是否在进行事件触发前进行确认操作
     *
     * @return 确认操作
     */
    boolean confirm() default false;

    /**
     * 展示名称，如果是按钮情况，则使用该字段的值展示，否则使用接口本身的名字
     *
     * @return 展示名称
     */
    String displayName() default "";

    /**
     * 当前级联接口调用时需要的级联入参数组，填入的时当前接口的Java字段名
     *
     * @return 级联字段数组
     */
    String[] cascadeColumn() default {};
}
