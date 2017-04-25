package com.jadyer.seed.comm.jpa;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * ---------------------------------------------------------------------------------------------------------------
 * 具体用法如下
 * //Condition<User> spec = Condition.create();           //此时就会使用and连接各个查询条件（默认的）
 * //Condition<User> spec = Condition.create().and();     //此时就会使用and连接各个查询条件
 * Condition<User> spec = Condition.<User>create().or();  //此时就会使用or连接各个查询条件
 * spec.eq("uid", uid);
 * spec.between("updateTime", new org.springframework.data.domain.Range<>(new Date(), new Date()));
 * Condition<User> spec = Condition.<User>create().or().eq("id", 8).notin("name", nameList);
 * userRepository.findAll(spec, new PageRequest(0, 15, new Sort(Sort.Direction.DESC, "id")));
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
 * Created by 玄玉<https://jadyer.github.io/> on 2016/7/2 17:11.
 */
public class Condition<T> implements Specification<T> {
    //标记最后联合所有查询条件构造sql时，采用“and”还是“or”
    private Predicate.BooleanOperator operatorType = Predicate.BooleanOperator.AND;

    //定义目前支持的sql操作
    private enum Operator{
        EQ, NE, GT, LT, GE, LE, BETWEEN, IN, NOTIN, LIKE, NOTLIKE
    }

    //存储所有字段查询条件，用于最后联合所有查询条件构造sql
    private List<SearchFilter> filters = new ArrayList<>();

    //记录每一个字段查询条件的实体
    private class SearchFilter {
        String property;
        Operator operator;
        Object value;
        SearchFilter(String property, Operator operator, Object value) {
            this.property = property;
            this.operator = operator;
            this.value = value;
        }
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

    /**
     * 创建实例
     * 注意：or()和and()虽支持多次调用，但在构造SQL时使用或还是与，取决于最后一次调用的是or()还是and()
     */
    public static <T> Condition<T> create() {
        return new Condition<>();
    }

    /**
     * 设置本次构造sql时采用“or”
     */
    public Condition<T> or(){
        this.operatorType = Predicate.BooleanOperator.OR;
        return this;
    }

    /**
     * 设置本次构造sql时采用“and”
     */
    public Condition<T> and(){
        this.operatorType = Predicate.BooleanOperator.AND;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
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
                case GT:
                        predicateList.add(cb.gt(expression, (Number)filter.value));
                        break;
                case LT:
                        predicateList.add(cb.lt(expression, (Number)filter.value));
                        break;
                case GE:
                        predicateList.add(cb.ge(expression, (Number)filter.value));
                        break;
                case LE:
                        predicateList.add(cb.le(expression, (Number)filter.value));
                        break;
                case BETWEEN:
                        Range range = (Range)filter.value;
                        predicateList.add(cb.between(expression, range.getLowerBound(), range.getUpperBound()));
                        break;
                case IN:
                        predicateList.add(cb.in(expression).value(filter.value));
                        break;
                case NOTIN:
                        predicateList.add(cb.in(expression).value(filter.value).not());
                        break;
                case LIKE:
                        String[] values = (String[])filter.value;
                        Predicate[] predicates = new Predicate[values.length];
                        for(int i=0; i<values.length; i++){
                            predicates[i] = cb.like(expression, "%" + values[i] + "%");
                        }
                        predicateList.add(cb.or(predicates));
                        break;
                case NOTLIKE:
                        values = (String[])filter.value;
                        predicates = new Predicate[values.length];
                        for(int i=0; i<values.length; i++){
                            predicates[i] = cb.notLike(expression, "%" + values[i] + "%");
                        }
                        predicateList.add(cb.and(predicates));
                        break;
                default:
                        System.out.println("nothing to do");
            }
        }
        //联合所有条件构造sql
        Predicate[] predicates = predicateList.toArray(new Predicate[predicateList.size()]);
        return Predicate.BooleanOperator.AND.equals(operatorType) ? cb.and(predicates) : cb.or(predicates);
    }

    public Condition<T> eq(String property, Object value) {
        this.add(property, Operator.EQ, value);
        return this;
    }

    public Condition<T> ne(String property, Object value) {
        this.add(property, Operator.NE, value);
        return this;
    }

    public Condition<T> gt(String property, Number number) {
        this.add(property, Operator.GT, number);
        return this;
    }

    public Condition<T> lt(String property, Number number) {
        this.add(property, Operator.LT, number);
        return this;
    }

    public Condition<T> ge(String property, Number number) {
        this.add(property, Operator.GE, number);
        return this;
    }

    public Condition<T> le(String property, Number number) {
        this.add(property, Operator.LE, number);
        return this;
    }

    public Condition<T> between(String property, Range<? extends Comparable<?>> range) {
        this.add(property, Operator.BETWEEN, range);
        return this;
    }

    public Condition<T> in(String property, Object... values) {
        this.add(property, Operator.IN, values);
        return this;
    }

    public Condition<T> notin(String property, Object... values) {
        this.add(property, Operator.NOTIN, values);
        return this;
    }

    public Condition<T> like(String property, String... values) {
        this.add(property, Operator.LIKE, values);
        return this;
    }

    public Condition<T> notlike(String property, String... values) {
        this.add(property, Operator.NOTLIKE, values);
        return this;
    }
}