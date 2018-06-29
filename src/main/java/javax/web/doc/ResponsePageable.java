package javax.web.doc;

import java.io.Serializable;
import java.util.List;

/**
 * Created by devops4j on 2017/12/5.
 * 分页接口时必须实现该接接口，与RequestPageable接口成对出现
 */
public interface ResponsePageable<T> extends Serializable {
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

    /**
     * 记录总条数
     *
     * @return 总条数
     */
    long getTotal();

    /**
     * 记录总条数
     *
     * @param total 总条数
     */
    void setTotal(long total);

    /**
     * 分页数
     *
     * @return 分页数
     */
    int getPageNum();

    /**
     * 分页数
     *
     * @param pageNum 分页数
     */
    void setPageNum(int pageNum);

    /**
     * 获取记录
     *
     * @return 记录
     */
    List<T> getRecords();
}
