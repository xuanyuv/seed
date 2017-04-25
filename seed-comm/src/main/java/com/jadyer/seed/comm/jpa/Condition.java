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
 * 参考了SpringSide实现
 * https://github.com/springside/springside4/blob/4.0/modules/core/src/main/java/org/springside/modules/persistence/SearchFilter.java
 * https://github.com/springside/springside4/blob/4.0/modules/core/src/test/java/org/springside/modules/persistence/SearchFilterTest.java
 * https://github.com/springside/springside4/blob/4.0/modules/core/src/main/java/org/springside/modules/persistence/DynamicSpecifications.java
 * https://github.com/springside/springside4/blob/4.0/examples/showcase/src/test/java/org/springside/examples/showcase/repository/jpa/DynamicSpecificationTest.java
 * ---------------------------------------------------------------------------------------------------------------
 * 具体用法
 * Condition<User> spec = Condition.<User>create().and("id", Condition.Operator.EQ, 8);
 * Condition<User> spec = Condition.<User>create().and("name", Condition.Operator.NOTIN, nameList);
 * Condition<User> spec = Condition.<User>create().and("updateTime", Condition.Operator.BETWEEN, new org.springframework.data.domain.Range<>(new Date(), new Date()));
 * 或者像下面这样构造Specification
 * Condition<User> spec = Condition.create();
 * spec.and("uid", Condition.Operator.EQ, uid);
 * spec.and("category", Condition.Operator.EQ, 2);
 * 最终传入findAll()方法中
 * userRepository.findAll(spec, new PageRequest(0, 15, new Sort(Sort.Direction.DESC, "id")));
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
    public enum Operator{
        EQ, NE, GT, LT, GE, LE, LIKE, NOTLIKE, IN, NOTIN, BETWEEN
    }

    private List<SearchFilter> filters = new ArrayList<>();

    private class SearchFilter {
        String fieldName;
        Object value;
        Operator operator;
        SearchFilter(String fieldName, Operator operator, Object value) {
            this.fieldName = fieldName;
            this.value = value;
            this.operator = operator;
        }
    }


    public static <T> Condition<T> create() {
        return new Condition<>();
    }


    public Condition<T> clear() {
        filters.clear();
        return this;
    }


    public Condition<T> and(String fieldName, Operator op, Object value) {
        filters.add(new SearchFilter(fieldName, op, value));
        return this;
    }


    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (filters.isEmpty()) {
            return cb.conjunction();
        }
        List<Predicate> predicateList = new ArrayList<>();
        for (SearchFilter filter : filters) {
            //nested path translate, 如Task的名为"user.name"的filedName, 转换为Task.user.name属性
            String[] fieldNames = StringUtils.split(filter.fieldName, ".");
            Path expression = root.get(fieldNames[0]);
            for (int i=1; i<fieldNames.length; i++) {
                expression = expression.get(fieldNames[i]);
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
                case LIKE:
                        if(filter.value instanceof List){
                            List valueList = (List)filter.value;
                            Predicate[] predicates = new Predicate[valueList.size()];
                            for(int i=0; i<valueList.size(); i++){
                                //noinspection unchecked
                                predicates[i] = cb.like(expression, "%" + valueList.get(i) + "%");
                            }
                            predicateList.add(cb.or(predicates));
                        }else{
                            //noinspection unchecked
                            predicateList.add(cb.like(expression, "%" + filter.value + "%"));
                        }
                        break;
                case NOTLIKE:
                        if(filter.value instanceof List){
                            List valueList = (List)filter.value;
                            Predicate[] predicates = new Predicate[valueList.size()];
                            for(int i=0; i<valueList.size(); i++){
                                //noinspection unchecked
                                predicates[i] = cb.notLike(expression, "%" + valueList.get(i) + "%");
                            }
                            predicateList.add(cb.and(predicates));
                        }else{
                            //noinspection unchecked
                            predicateList.add(cb.notLike(expression, "%" + filter.value + "%"));
                        }
                        break;
                case GT:
                        predicateList.add(cb.greaterThan(expression, (Comparable)filter.value));
                        break;
                case LT:
                        predicateList.add(cb.lessThan(expression, (Comparable)filter.value));
                        break;
                case GE:
                        predicateList.add(cb.greaterThanOrEqualTo(expression, (Comparable)filter.value));
                        break;
                case LE:
                        predicateList.add(cb.lessThanOrEqualTo(expression, (Comparable)filter.value));
                        break;
                case IN:
                        //noinspection unchecked
                        predicateList.add(cb.in(expression).value(filter.value));
                        break;
                case NOTIN:
                        //noinspection unchecked
                        predicateList.add(cb.in(expression).value(filter.value).not());
                        break;
                case BETWEEN:
                        Range range = (Range)filter.value;
                        predicateList.add(cb.between(expression, range.getLowerBound(), range.getUpperBound()));
                        break;
                default:
                        System.out.println("nothing to do");
            }
        }
        //将所有条件用 and 联合起来
        return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
    }


    /*
    @RequestMapping("/list")
    public String listViaPage(String pageNo, HttpServletRequest request){
        final int uid = (Integer)request.getSession().getAttribute(Constants.UID);
        //排序
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        //分页(zero-based page index)
        Pageable pageable = new PageRequest(StringUtils.isBlank(pageNo)?0:Integer.parseInt(pageNo), Constants.PAGE_SIZE, sort);
        //条件
        Specification<FansInfo> spec = new Specification<FansInfo>(){
            //@Override
            //public Predicate toPredicate(Root<FansInfo> root, CriteriaQuery<?> query, CriteriaBuilder builder){
            //    Path<Integer> _uid = root.get("uid");
            //    Path<Integer> _category = root.get("category");
            //    Predicate p1 = builder.equal(_uid, uid);
            //    Predicate p2 = builder.equal(_category, 2);
            //    query.where(builder.and(p1, p2));
            //    return query.getRestriction();
            //}
            @Override
            public Predicate toPredicate(Root<FansInfo> root, CriteriaQuery<?> query, CriteriaBuilder builder){
                List<Predicate> list = new ArrayList<>();
                Path<Integer> _uid = root.get("uid");
                list.add(builder.equal(_uid, uid));
                //list.add(builder.equal(root.get("uid").as(Integer.class), uid));
                //list.add(builder.like(root.<String>get("nickname"), "%"+nickname+"%"));
                return builder.and(list.toArray(new Predicate[list.size()]));
            }
        };
        //执行
        Page<FansInfo> fansPage = fansInfoDao.findAll(spec, pageable);
        request.setAttribute("page", fansPage);
        return "fansList";
    }
    */
}