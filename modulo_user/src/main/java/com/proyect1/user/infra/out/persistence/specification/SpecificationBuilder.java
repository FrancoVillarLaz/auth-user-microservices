package com.proyect1.user.infra.out.persistence.specification;

import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class SpecificationBuilder<T> {

    private final List<Specification<T>> specs = new ArrayList<>();

    public static <T> SpecificationBuilder<T> of() {
        return new SpecificationBuilder<>();
    }

    public SpecificationBuilder<T> like(String field, String value) {
        specs.add(GenericSpecification.like(field, value));
        return this;
    }

    public SpecificationBuilder<T> equal(String field, Object value) {
        specs.add(GenericSpecification.equal(field, value));
        return this;
    }

    public SpecificationBuilder<T> notEqual(String field, Object value) {
        specs.add(GenericSpecification.notEqual(field, value));
        return this;
    }

    public <Y extends Comparable<? super Y>> SpecificationBuilder<T> between(String field, Y start, Y end) {
        specs.add(GenericSpecification.between(field, start, end));
        return this;
    }

    public SpecificationBuilder<T> in(String field, Iterable<?> values) {
        specs.add(GenericSpecification.in(field, values));
        return this;
    }

    public Specification<T> build() {
        if (specs.isEmpty()) {
            return (root, query, cb) -> cb.conjunction();
        }

        return specs.stream()
                .reduce(Specification::and)
                .orElse((root, query, cb) -> cb.conjunction());
    }
}