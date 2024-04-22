package com.jadyer.seed.comm.tmp.jpaSpec;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 快速构建基于spring-data-jpa中的复杂查询对象Specification的工具类。
 * -------------------------------------------------------------------------------------
 * 目的是为了快速构建简单的查询，使用方法如下：
 * Specification<Entity> specification = SpecificationUtil.<Entity>and()
 * .equal("field", "value")
 * .notEqual(ex, "field", "value")
 * .isNull(ex, "field")
 * .build();
 * -------------------------------------------------------------------------------------
 * SpecificationUtil.SpecificationBuilder<PayOrder> spec = SpecificationUtil.and();
 * spec.equal(Objects.nonNull(payOrderListReq.getPayStatus()), "payStatus", payOrderListReq.getPayStatus());
 * spec.equal(Objects.nonNull(payOrderListReq.getId()), "id", payOrderListReq.getId());
 * spec.equal(StringUtils.isNotBlank(payOrderListReq.getMerNo()), "merNo", payOrderListReq.getMerNo());
 * spec.like(StringUtils.isNotBlank(payOrderListReq.getMerName()), "merName", payOrderListReq.getMerName() + '%');
 * spec.ge(Objects.nonNull(payOrderListReq.getPayTimeBegin()), "payTime", payOrderListReq.getPayTimeBegin());
 * spec.le(Objects.nonNull(payOrderListReq.getPayTimeEnd()), "payTime", payOrderListReq.getPayTimeEnd());
 * -------------------------------------------------------------------------------------
 */
public final class SpecificationUtil {
    private SpecificationUtil() {}

    public static <T> SpecificationBuilder<T> and() {
        return new SpecificationBuilder<>(Predicate.BooleanOperator.AND);
    }

    public static <T> SpecificationBuilder<T> or() {
        return new SpecificationBuilder<>(Predicate.BooleanOperator.OR);
    }

    public static final class SpecificationBuilder<T> {
        private final Predicate.BooleanOperator operator;
        private final List<Specification<T>> specificationList;

        public SpecificationBuilder(Predicate.BooleanOperator operator) {
            this.operator = operator;
            this.specificationList = new ArrayList<>();
        }

        public SpecificationBuilder<T> equal(String field, Object value) {
            return this.addSpecification((Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.equal(root.get(field), value));
        }

        public SpecificationBuilder<T> equal(boolean ex, String field, Object value) {
            return this.addSpecification(ex, (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.equal(root.get(field), value));
        }

        public SpecificationBuilder<T> notEqual(String field, Object value) {
            return this.addSpecification((Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.notEqual(root.get(field), value));
        }

        public SpecificationBuilder<T> notEqual(boolean ex, String field, Object value) {
            return this.addSpecification(ex, (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.notEqual(root.get(field), value));
        }

        public SpecificationBuilder<T> like(String field, String value) {
            return this.addSpecification((Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.like(root.get(field), value));
        }

        public SpecificationBuilder<T> like(boolean ex, String field, String value) {
            return this.addSpecification(ex, (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.like(root.get(field), value));
        }

        public SpecificationBuilder<T> in(String field, Object... values) {
            return this.addSpecification((Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> root.get(field).in(values));
        }

        public SpecificationBuilder<T> in(boolean ex, String field, Object... values) {
            return this.addSpecification(ex, (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> root.get(field).in(values));
        }

        public SpecificationBuilder<T> notIn(String field, Object... values) {
            return this.addSpecification((Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> root.get(field).in(values).not());
        }

        public SpecificationBuilder<T> notIn(boolean ex, String field, Object... values) {
            return this.addSpecification(ex, (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> root.get(field).in(values).not());
        }

        public <Y extends Comparable<Y>> SpecificationBuilder<T> between(String field, Y start, Y end) {
            return this.addSpecification((Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.between(root.get(field), start, end));
        }

        public <Y extends Comparable<Y>> SpecificationBuilder<T> between(boolean ex, String field, Y start, Y end) {
            return this.addSpecification(ex, (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.between(root.get(field), start, end));
        }

        public <Y extends Comparable<Y>> SpecificationBuilder<T> gt(String field, Y value) {
            return this.addSpecification((Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.greaterThan(root.get(field), value));
        }

        public <Y extends Comparable<Y>> SpecificationBuilder<T> gt(boolean ex, String field, Y value) {
            return this.addSpecification(ex, (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.greaterThan(root.get(field), value));
        }

        public <Y extends Comparable<Y>> SpecificationBuilder<T> ge(String field, Y value) {
            return this.addSpecification((Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.greaterThanOrEqualTo(root.get(field), value));
        }

        public <Y extends Comparable<Y>> SpecificationBuilder<T> ge(boolean ex, String field, Y value) {
            return this.addSpecification(ex, (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.greaterThanOrEqualTo(root.get(field), value));
        }

        public <Y extends Comparable<Y>> SpecificationBuilder<T> lt(String field, Y value) {
            return this.addSpecification((Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.lessThan(root.get(field), value));
        }

        public <Y extends Comparable<Y>> SpecificationBuilder<T> lt(boolean ex, String field, Y value) {
            return this.addSpecification(ex, (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.lessThan(root.get(field), value));
        }

        public <Y extends Comparable<Y>> SpecificationBuilder<T> le(String field, Y value) {
            return this.addSpecification((Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.lessThanOrEqualTo(root.get(field), value));
        }

        public <Y extends Comparable<Y>> SpecificationBuilder<T> le(boolean ex, String field, Y value) {
            return this.addSpecification(ex, (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.lessThanOrEqualTo(root.get(field), value));
        }

        public SpecificationBuilder<T> isNull(String field) {
            return this.addSpecification((Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.isNull(root.get(field)));
        }

        public SpecificationBuilder<T> isNull(boolean ex, String field) {
            return this.addSpecification(ex, (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.isNull(root.get(field)));
        }

        public SpecificationBuilder<T> isNotNull(String field) {
            return this.addSpecification((Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.isNotNull(root.get(field)));
        }

        public SpecificationBuilder<T> isNotNull(boolean ex, String field) {
            return this.addSpecification(ex, (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.isNotNull(root.get(field)));
        }

        public SpecificationBuilder<T> newSpecification(Specification<T> specification) {
            return this.addSpecification(specification);
        }

        public SpecificationBuilder<T> newSpecification(boolean condition, Specification<T> specification) {
            return this.addSpecification(condition, specification);
        }

        private SpecificationBuilder<T> addSpecification(Specification<T> specification) {
            return this.addSpecification(true, specification);
        }

        private SpecificationBuilder<T> addSpecification(boolean condition, Specification<T> specification) {
            if (condition) {
                Assert.notNull(specification, "specification can not be null");
                this.specificationList.add(specification);
            }
            return this;
        }

        public Specification<T> build() {
            return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
                if (specificationList.isEmpty()) {
                    return cb.conjunction();
                }
                Predicate[] predicates = specificationList.stream().map(s -> s.toPredicate(root, query, cb)).collect(Collectors.toList()).toArray(new Predicate[specificationList.size()]);
                return Predicate.BooleanOperator.AND.equals(operator) ? cb.and(predicates) : cb.or(predicates);
            };
        }
    }
}