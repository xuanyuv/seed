package com.jadyer.seed.comm.util;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.jpa.Pager;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * spring-data-jpa-page工具类
 * -----------------------------------------------------------------------------------------------------
 * org.springframework.data.domain.Page属性如下
 * {
 *     "content":[
 *         {
 *             "id":"0",
 *             "name":"jadyer01"
 *         }
 *     ],
 *     "empty":false,
 *     "first":false,
 *     "last":true,
 *     "number":3,
 *     "numberOfElements":1,
 *     "pageable":{
 *         "offset":6,
 *         "pageNumber":3,
 *         "pageSize":2,
 *         "paged":true,
 *         "sort":{
 *             "empty":false,
 *             "sorted":true,
 *             "unsorted":false
 *         },
 *         "unpaged":false
 *     },
 *     "size":2,
 *     "sort":{
 *         "empty":false,
 *         "sorted":true,
 *         "unsorted":false
 *     },
 *     "totalElements":7,
 *     "totalPages":4
 * }
 * -----------------------------------------------------------------------------------------------------
 * @version v1.1
 * @history v1.1-->对象拷贝方法由BeanUtil改为JSON的方式（解决id无法拷贝的问题）
 * @history v1.0-->初建
 * Created by 玄玉<https://jadyer.cn/> on 2021/7/16 11:45.
 */
public final class PageUtil {
    private PageUtil(){}

    /**
     * 拷贝org.springframework.data.domain.Page为自定义的Pager对象
     * Comment by 玄玉<https://jadyer.cn/> on 2021/7/16 9:49.
     */
    public static <E, T> Pager<T> copy(Page<E> page, Class<T> targetClass){
        List<T> targetList = new ArrayList<>();
        for(E e : page.getContent()){
            targetList.add(JSON.parseObject(JSON.toJSONString(e), targetClass));
        }
        return new Pager<>(page.getNumber()+1, page.getSize(), page.getTotalPages(), page.getTotalElements(), page.getNumberOfElements(), targetList);
    }
}