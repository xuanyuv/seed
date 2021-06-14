package com.jadyer.seed.comm.jpa;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * ---------------------------------------------------------------------------------------------------------------
 * 用法如下：通过or()或者and()方法来创建实例，然后调用各个查询条件，即可
 * Condition<User> spec = Condition.or();
 * Condition<User> spec = Condition.and();
 * spec.eq("uid", uid);
 * spec.gt("createTime", new Date());
 * spec.between("updateTime", new Date(), new Date());
 * Condition<User> spec = Condition.<User>or().eq("id", 8).notIn("name", nameList);
 * userRepository.findAll(spec, PageRequest.of(0, 15, Sort.by(Sort.Direction.DESC, "id")));
 * ---------------------------------------------------------------------------------------------------------------
 * 参考了以下实现
 * https://github.com/wenhao/jpa-spec
 * https://github.com/springside/springside4/blob/4.0/modules/core/src/main/java/org/springside/modules/persistence/SearchFilter.java
 * https://github.com/springside/springside4/blob/4.0/modules/core/src/test/java/org/springside/modules/persistence/SearchFilterTest.java
 * https://github.com/springside/springside4/blob/4.0/modules/core/src/main/java/org/springside/modules/persistence/DynamicSpecifications.java
 * https://github.com/springside/springside4/blob/4.0/examples/showcase/src/test/java/org/springside/examples/showcase/repository/jpa/DynamicSpecificationTest.java
 * ---------------------------------------------------------------------------------------------------------------
 * 补充mysql-limit分页参数offset和rows的计算方式
 * //计算pageNo（从0开始）
 * if(pageSize*pageNo >= totalCount){
 *     pageNo = (int)(totalCount/pageSize);
 * }
 * int offset = pageSize * pageNo;
 * int rows = pageSize;
 * ---------------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2016/7/2 17:11.
 */
public class Condition<T> implements Specification<T> {
    private static final long serialVersionUID = 7641534766468240944L;

    //标记最后联合所有查询条件构造sql时，采用“and”还是“or”
    private Predicate.BooleanOperator operatorType;

    //定义目前支持的sql操作
    private enum Operator{
        EQ, NE, GT, LT, GE, LE, BETWEEN, IN, NOTIN, LIKE, NOTLIKE
    }

    //存储所有字段查询条件，用于最后联合所有查询条件构造sql
    private List<SearchFilter> filters = new ArrayList<>();

    //记录每一个字段查询条件的实体
    private static class SearchFilter {
        String property;
        Operator operator;
        Object value;
        SearchFilter(String property, Operator operator, Object value) {
            this.property = property;
            this.operator = operator;
            this.value = value;
        }
    }

    private Condition(Predicate.BooleanOperator operatorType) {
        this.operatorType = operatorType;
    }

    /**
     * 实例化对象，并设置本次构造sql时采用“or”连接各个查询条件
     */
    public static <T> Condition<T> or() {
        return new Condition<>(Predicate.BooleanOperator.OR);
    }

    /**
     * 实例化对象，并设置本次构造sql时采用“and”连接各个查询条件
     */
    public static <T> Condition<T> and() {
        return new Condition<>(Predicate.BooleanOperator.AND);
    }

    /**
     * 增加字段查询条件
     * @param property 实体属性名（非数据库中的字段名）
     * @param operator sql操作
     * @param value    属性值
     */
    private Condition<T> add(String property, Operator operator, Object value) {
        filters.add(new SearchFilter(property, operator, value));
        return this;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (filters.isEmpty()) {
            return cb.conjunction();
        }
        List<Predicate> predicateList = new ArrayList<>();
        for (SearchFilter filter : filters) {
            //nested path translate, 如Task的名为"user.name"的filedName, 转换为Task.user.name属性
            String[] propertys = StringUtils.split(filter.property, ".");
            Path expression = root.get(propertys[0]);
            for (int i=1; i<propertys.length; i++) {
                expression = expression.get(propertys[i]);
            }
            //logic operator
            switch (filter.operator) {
                case EQ:
                        if(null == filter.value){
                            predicateList.add(cb.isNull(expression));
                        }else{
                            predicateList.add(cb.equal(expression, filter.value));
                        }
                        break;
                case NE:
                        if(null == filter.value){
                            predicateList.add(cb.isNotNull(expression));
                        }else{
                            predicateList.add(cb.notEqual(expression, filter.value));
                        }
                        break;
                case LT:
                        predicateList.add(cb.lessThan(expression, (Comparable)filter.value));
                        break;
                case LE:
                        predicateList.add(cb.lessThanOrEqualTo(expression, (Comparable)filter.value));
                        break;
                case GT:
                        predicateList.add(cb.greaterThan(expression, (Comparable)filter.value));
                        break;
                case GE:
                        predicateList.add(cb.greaterThanOrEqualTo(expression, (Comparable)filter.value));
                        break;
                case BETWEEN:
                        Comparable[] comparables = (Comparable[])filter.value;
                        predicateList.add(cb.between(expression, comparables[0], comparables[1]));
                        break;
                case IN:
                        predicateList.add(cb.in(expression).value(filter.value));
                        break;
                case NOTIN:
                        predicateList.add(cb.in(expression).value(filter.value).not());
                        break;
                case LIKE:
                        List<String> valueList = (List<String>)filter.value;
                        Predicate[] predicates = new Predicate[valueList.size()];
                        for(int i=0,len=valueList.size(); i<len; i++){
                            if(valueList.get(i).contains("%")){
                                predicates[i] = cb.like(expression, valueList.get(i));
                            }else{
                                predicates[i] = cb.like(expression, "%" + valueList.get(i) + "%");
                            }
                        }
                        predicateList.add(cb.or(predicates));
                        break;
                case NOTLIKE:
                        valueList = (List<String>)filter.value;
                        predicates = new Predicate[valueList.size()];
                        for(int i=0,len=valueList.size(); i<len; i++){
                            if(valueList.get(i).contains("%")){
                                predicates[i] = cb.notLike(expression, valueList.get(i));
                            }else{
                                predicates[i] = cb.notLike(expression, "%" + valueList.get(i) + "%");
                            }
                        }
                        predicateList.add(cb.and(predicates));
                        break;
                default:
                        System.out.println("Nothing to do...");
            }
        }
        //联合所有条件构造sql
        Predicate[] predicates = new Predicate[predicateList.size()];
        predicateList.toArray(predicates);
        return Predicate.BooleanOperator.AND.equals(operatorType) ? cb.and(predicates) : cb.or(predicates);
    }

    public <V extends Comparable<V>> Condition<T> eq(String property, V value) {
        return this.add(property, Operator.EQ, value);
    }

    public <V extends Comparable<V>> Condition<T> ne(String property, V value) {
        return this.add(property, Operator.NE, value);
    }

    public <V extends Comparable<V>> Condition<T> gt(String property, V value) {
        return this.add(property, Operator.GT, value);
    }

    public <V extends Comparable<V>> Condition<T> lt(String property, V value) {
        return this.add(property, Operator.LT, value);
    }

    public <V extends Comparable<V>> Condition<T> ge(String property, V value) {
        return this.add(property, Operator.GE, value);
    }

    public <V extends Comparable<V>> Condition<T> le(String property, V value) {
        return this.add(property, Operator.LE, value);
    }

    public <V extends Comparable<V>> Condition<T> between(String property, V lower, V upper) {
        Comparable<?>[] comparables = new Comparable<?>[2];
        comparables[0] = lower;
        comparables[1] = upper;
        return this.add(property, Operator.BETWEEN, comparables);
    }

    public Condition<T> in(String property, Collection<?> values) {
        return this.add(property, Operator.IN, values);
    }

    public Condition<T> notIn(String property,  Collection<?> values) {
        return this.add(property, Operator.NOTIN, values);
    }

    public Condition<T> like(String property, List<String> valueList) {
        return this.add(property, Operator.LIKE, valueList);
    }

    public Condition<T> like(String property, String value) {
        return this.like(property, Collections.singletonList(value));
    }

    public Condition<T> notLike(String property, String value) {
        return this.notLike(property, Collections.singletonList(value));
    }

    public Condition<T> notLike(String property, List<String> valueList) {
        return this.add(property, Operator.NOTLIKE, valueList);
    }
}