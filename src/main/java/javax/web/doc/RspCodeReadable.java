package javax.web.doc;

import java.io.Serializable;

/**
 * 应答码可读接口
 */
public interface RspCodeReadable extends Serializable {
    /**
     * 应答码
     *
     * @return 应答码
     */
    String getRspCode();

    /**
     * 应答描述
     *
     * @return 应答描述
     */
    String getRspDesc();
}
