package com.jadyer.seed.comm.jpa;

import java.io.Serializable;
import java.util.List;

public class Pager<T> implements Serializable {
    private static final long serialVersionUID = -1791442658280162017L;
    /** 页码：从“1”开始 */
    private int pageNo;
    /** 页长：从“1”开始 */
    private int pageSize;
    /** 总页数 */
    private long totalPages;
    /** 总记录数 */
    private long total;
    /** 当前页的记录数 */
    private int countsOfPage;
    /** 当前页的记录集 */
    private List<T> data;

    public Pager(){}

    public Pager(int pageNo, int pageSize, long totalPages, long total, int countsOfPage, List<T> data) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.total = total;
        this.countsOfPage = countsOfPage;
        this.data = data;
    }

    public int getPageNo() {
        return pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public long getTotal() {
        return total;
    }

    public int getCountsOfPage() {
        return countsOfPage;
    }

    public List<T> getData() {
        return data;
    }
}