package com.jadyer.seed.comm.jpa;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * <code>
 *     两种用法如下
 *     Map<String, Object> params = new HashMap<>();
 *     params.put("gender", "male");
 *     params.put("age:gt", 20);
 *     Condition<User> query = Condition.<User>create().and("id", Condition.Operator.ge, 8).and(params);
 *     userRepository.findAll(query, new PageRequest(0, 15, new Sort(Sort.Direction.DESC, "id")));
 * </code>
 * <p>
 *     参照了SpringSide实现
 *     https://github.com/springside/springside4/blob/4.0/modules/core/src/main/java/org/springside/modules/persistence/SearchFilter.java
 *     https://github.com/springside/springside4/blob/4.0/modules/core/src/test/java/org/springside/modules/persistence/SearchFilterTest.java
 *     https://github.com/springside/springside4/blob/4.0/modules/core/src/main/java/org/springside/modules/persistence/DynamicSpecifications.java
 *     https://github.com/springside/springside4/blob/4.0/examples/showcase/src/test/java/org/springside/examples/showcase/repository/jpa/DynamicSpecificationTest.java
 * </p>
 * <code>
 *     补充MySQL-LIMIT分页参数offset和rows的计算方式
 *     //计算pageNo（从0开始）
 *     if(pageSize*pageNo >= totalCount){
 *          pageNo = (int)(totalCount / pageSize);
 *     }
 *     int offset = pageSize * pageNo;
 *     int rows = pageSize;
 * </code>
 * Created by 玄玉<https://jadyer.github.io/> on 2016/7/2 17:11.
 */
public class Condition<T> implements Specification<T> {
    private List<SearchFilter> filters = new ArrayList<>();

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


    /**
     * <code>
     *      //eq, ne, like, gt, lt, ge, le <br>
     *      params.put("gender", "male"); <br>
     *      params.put("age:gt", 20);
     * </code>
     */
    public Condition<T> and(Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String[] fieldNames = StringUtils.split(entry.getKey(), ":");
            Operator operator = fieldNames.length==1 ? Operator.EQ : Operator.getFromString(fieldNames[1]);
            filters.add(new SearchFilter(fieldNames[0], operator, entry.getValue()));
        }
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
                        //noinspection unchecked
                        predicateList.add(cb.like(expression, "%" + filter.value + "%"));
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
                default:
                        System.out.println("nothing to do");
            }
        }
        //将所有条件用 and 联合起来
        return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
    }


    public enum Operator{
        EQ, NE, LIKE, GT, LT, GE, LE;
        public static Operator getFromString(String value) {
            try {
                return Operator.valueOf(value.toUpperCase(Locale.US));
            } catch (Exception e) {
                String msg = "Invalid value '%s' for Operator given! Has to be in 'eq, ne, like, gt, lt, ge, le' (case insensitive).";
                throw new IllegalArgumentException(String.format(msg, value), e);
            }
        }
    }


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