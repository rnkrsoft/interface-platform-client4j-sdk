package javax.web.doc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by rnkrsoft.com on 2017/12/5.
 * 用于在接口（Java接口）方法上定义接口（调用服务接口）
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ApidocInterface {
    /**
     * 填写接口描述信息
     *
     * @return 接口描述
     */
    String value();

    /**
     * 接口名,接口的唯一标识，如果未填使用方法名作为接口名
     *
     * @return 接口名
     */
    String name() default "";

    /**
     * 接口版本号，交易代码和版本号共同构成了在同一个服务类里的唯一关系
     * 接口版本为填写时，使用服务中的版本号
     *
     * @return 接口版本号
     */
    String version() default "";

    /**
     * 用法
     *
     * @return 用法
     */
    String usage() default "";

}
