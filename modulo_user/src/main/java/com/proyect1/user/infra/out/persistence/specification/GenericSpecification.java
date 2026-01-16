package com.proyect1.user.infra.out.persistence.specification;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

public class GenericSpecification {

    public static <T> Specification<T> like(String field, String value) {
        return (root, query, cb) -> {
            if (value == null || value.isEmpty()) return cb.conjunction();

            Path<String> path = getPath(root, field);
            return cb.like(cb.lower(path), "%" + value.toLowerCase() + "%");
        };
    }

    public static <T> Specification<T> equal(String field, Object value) {
        return (root, query, cb) -> {
            if (value == null) return cb.conjunction();

            Path<Object> path = getPath(root, field);
            return cb.equal(path, value);
        };
    }

    public static <T> Specification<T> notEqual(String field, Object value) {
        return (root, query, cb) -> {
            if (value == null) return cb.conjunction();

            Path<Object> path = getPath(root, field);
            return cb.notEqual(path, value);
        };
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> between(String field, Y start, Y end) {
        return (root, query, cb) -> {
            if (start == null && end == null) return cb.conjunction();

            Path<Y> path = getPath(root, field);

            if (start != null && end != null) return cb.between(path, start, end);
            if (start != null) return cb.greaterThanOrEqualTo(path, start);
            return cb.lessThanOrEqualTo(path, end);
        };
    }

    public static <T> Specification<T> in(String field, Iterable<?> values) {
        return (root, query, cb) -> {
            if (values == null || !values.iterator().hasNext()) return cb.conjunction();

            Path<?> path = getPath(root, field);
            CriteriaBuilder.In<Object> inClause = cb.in(path);

            for (Object v : values) inClause.value(v);

            return inClause;
        };
    }

    /**
     * Método para hacer JOIN con tabla users y agregar email a los resultados
     * @param emailField Nombre del campo email en la tabla users (ej: "email", "correo")
     * @param value Valor para filtrar por email (opcional, puede ser null)
     */
    public static <T> Specification<T> withEmailJoin(String emailField, String value) {
        return (root, query, cb) -> {
            // Siempre hacer LEFT JOIN con la tabla users
            // "users" debe ser el nombre de la entidad JPA mapeada a la tabla users
            Join<T, ?> usersJoin = root.join("users", JoinType.LEFT);

            // La condición del JOIN: users.id = [entidad_actual].userId
            Predicate joinCondition = cb.equal(usersJoin.get("id"), root.get("userId"));

            // Si se proporciona un valor para filtrar, agregar condición LIKE
            if (value != null && !value.trim().isEmpty()) {
                Predicate emailFilter = cb.like(
                        cb.lower(usersJoin.get(emailField)),
                        "%" + value.toLowerCase() + "%"
                );
                return cb.and(joinCondition, emailFilter);
            }

            return joinCondition;
        };
    }

    /**
     * Método solo para hacer el JOIN sin filtrar
     */
    public static <T> Specification<T> withEmailJoinOnly(String emailField) {
        return (root, query, cb) -> {
            Join<T, ?> usersJoin = root.join("users", JoinType.LEFT);
            return cb.equal(usersJoin.get("id"), root.get("userId"));
        };
    }

    @SuppressWarnings("unchecked")
    private static <T, R> Path<R> getPath(Path<T> root, String field) {
        String[] parts = field.split("\\.");
        Path<?> path = root;

        for (String part : parts) {
            path = path.get(part);
        }

        return (Path<R>) path;
    }


}