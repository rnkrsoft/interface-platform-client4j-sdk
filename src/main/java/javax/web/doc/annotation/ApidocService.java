package javax.web.doc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by rnkrsoft.com on 2017/12/5.
 * 用于在接口（Java接口）上定义服务（调用服务）
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ApidocService {
    /**
     * 服务版本号，如果接口未定义版本号则直接使用该版本号
     *
     * @return 版本号
     */
    String version() default "1.0.0";

    /**
     * 标记服务描述
     *
     * @return 服务描述
     */
    String value();

    /**
     * 服务名
     *
     * @return 服务名
     */
    String name() default "";

    /**
     * 用法
     *
     * @return 用法
     */
    String usage() default "";
}
