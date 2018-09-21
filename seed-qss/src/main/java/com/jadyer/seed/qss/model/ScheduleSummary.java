package com.jadyer.seed.qss.model;

/**
 * 投影查询（Spring Data JPA 中的多表查询）
 * ---------------------------------------------------------------------
 * 主要有两种实现方式
 * 一种是利用 Hibernate 的级联查询来实现
 * 一种是创建一个结果集的接口来接收连表查询后的结果，运行时Spring为该接口自动生成一个代理类来接收返回的结果，使用时直接getter便获取到了
 * 第二种实现方式，需要注意以下细节
 * 1. 定义结果集的接口，接口中只需要提供getter方法即可，不需要定义属性
 * 2. Repository中的@Query里面写的JPQL注意查询的字段，都需要显式使用别名
 * ---------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2017/4/27 14:15.
 */
public interface ScheduleSummary {
    String getName();
    String getUrl();
}