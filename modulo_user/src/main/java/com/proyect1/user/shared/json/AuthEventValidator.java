package com.proyect1.user.shared.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.proyect1.user.shared.exception.auth.InvalidAuthFieldException;
import com.proyect1.user.shared.exception.auth.MissingAuthFieldException;

public final class AuthEventValidator {

    private AuthEventValidator() {}

    public static String requiredText(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            throw new MissingAuthFieldException(field);
        }
        if (value.asText().isBlank()) {
            throw new InvalidAuthFieldException(field);
        }
        return value.asText();
    }

    public static Long requiredLong(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            throw new MissingAuthFieldException(field);
        }
        return value.asLong();
    }

    public static String optionalText(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return (value == null || value.isNull()) ? null : value.asText();
    }

    public static boolean optionalBoolean(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return (value == null || value.isNull()) ? false : value.asBoolean();
    }
}
