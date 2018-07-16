package javax.web.doc;

import java.io.Serializable;

/**
 * Created by rnkrsoft.com on 2017/12/5.
 * 分页接口时必须实现该接接口，与ResponsePageable接口成对出现
 */
public interface RequestPageable extends Serializable {
    /**
     * 获取分页大小
     *
     * @return 分页大小
     */
    int getPageSize();

    /**
     * 设置分页大小
     *
     * @param pageSize 分页大小
     */
    void setPageSize(int pageSize);

    /**
     * 获取当前页号
     *
     * @return 当前页号
     */
    int getPageNo();

    /**
     * 设置当前页号
     *
     * @param pageNo 当前页号
     */
    void setPageNo(int pageNo);
}
