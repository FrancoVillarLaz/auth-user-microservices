#!/bin/bash

# ==========================================================
# Script para Testear Escenarios de Errores y Validaciones
# ==========================================================

# Variables de Configuración
API_HOST="http://localhost:8083" # Cambia el puerto si es diferente
API_BASE_PATH="/api/users"
USER_ID_EXISTENTE=9999 # ID que asumimos existe (para pruebas DELETE/PUT)
USER_ID_INEXISTENTE=99999 # ID que asumimos no existe (para pruebas GET/PUT/DELETE)

# ==================================
# Funciones de Logging y Ejecución
# ==================================

# Función para imprimir cabeceras
print_header() {
    echo ""
    echo "=========================================================="
    echo "▶️  $1"
    echo "=========================================================="
}

# 0. Verificar si el comando 'jq' está disponible
if command -v jq &> /dev/null; then
    json_pp() {
        jq '.'
    }
else
    json_pp() {
        cat
    }
    echo "⚠️ ADVERTENCIA: 'jq' no encontrado. La salida JSON no será formateada."
fi

# 1. ERROR: Crear Usuario - Campos Obligatorios Faltantes (400 Bad Request)
test_missing_fields() {
    print_header "1. ❌ CREAR (POST): Campos obligatorios faltantes (400)"

    MISSING_PAYLOAD='{
        "apellido": "García"
        # Falta 'nombre', que es @NotBlank
    }'

    RESPONSE=$(curl -s -X POST "${API_HOST}${API_BASE_PATH}" \
        -H "Content-Type: application/json" \
        -d "$MISSING_PAYLOAD" -w "\nHTTP_STATUS:%{http_code}")

    HTTP_STATUS=$(echo "$RESPONSE" | grep "HTTP_STATUS" | cut -d ':' -f 2)
    BODY=$(echo "$RESPONSE" | grep -v "HTTP_STATUS")

    echo "Estado HTTP: $HTTP_STATUS"
    echo "Cuerpo de la Respuesta:"
    echo "$BODY" | json_pp
}

# 2. ERROR: Crear Usuario - Violación de Tamaño Máximo (400 Bad Request)
test_size_violation() {
    print_header "2. ❌ CREAR (POST): Violación de límite de @Size (>100 caracteres)"

    LONG_STRING=$(printf 'x%.0s' {1..101}) # Crea una cadena de 101 'x'

    SIZE_PAYLOAD='{
        "nombre": "'"$LONG_STRING"'",
        "apellido": "Válido"
    }'

    RESPONSE=$(curl -s -X POST "${API_HOST}${API_BASE_PATH}" \
        -H "Content-Type: application/json" \
        -d "$SIZE_PAYLOAD" -w "\nHTTP_STATUS:%{http_code}")

    HTTP_STATUS=$(echo "$RESPONSE" | grep "HTTP_STATUS" | cut -d ':' -f 2)
    BODY=$(echo "$RESPONSE" | grep -v "HTTP_STATUS")

    echo "Estado HTTP: $HTTP_STATUS"
    echo "Cuerpo de la Respuesta:"
    echo "$BODY" | json_pp
}

# 3. ERROR: Info Suscripciones - Lista Vacía (@NotEmpty)
test_empty_subscription_list() {
    print_header "3. ❌ INFO (POST): Lista de suscripciones vacía (400)"

    EMPTY_INFO_PAYLOAD='{
        "idSuscripciones": []
    }'

    RESPONSE=$(curl -s -X POST "${API_HOST}${API_BASE_PATH}/info-by-subscriptions" \
        -H "Content-Type: application/json" \
        -H "X-User-Id: 100" \
        -d "$EMPTY_INFO_PAYLOAD" -w "\nHTTP_STATUS:%{http_code}")

    HTTP_STATUS=$(echo "$RESPONSE" | grep "HTTP_STATUS" | cut -d ':' -f 2)
    BODY=$(echo "$RESPONSE" | grep -v "HTTP_STATUS")

    echo "Estado HTTP: $HTTP_STATUS"
    echo "Cuerpo de la Respuesta:"
    echo "$BODY" | json_pp
}

# 4. ERROR: Casos de Uso del Dominio (Ej. Suscripción no soportada)
test_domain_error() {
    print_header "4. ⚠️ INFO (POST): Error de Dominio (SubscriptionNotSupportedException)"

    # Asumimos que la suscripción "999" provoca la excepción SubscriptionNotSupportedException
    DOMAIN_ERROR_PAYLOAD='{
        "idSuscripciones": ["1", "999"]
    }'

    RESPONSE=$(curl -s -X POST "${API_HOST}${API_BASE_PATH}/info-by-subscriptions" \
        -H "Content-Type: application/json" \
        -H "X-User-Id: 100" \
        -d "$DOMAIN_ERROR_PAYLOAD" -w "\nHTTP_STATUS:%{http_code}")

    HTTP_STATUS=$(echo "$RESPONSE" | grep "HTTP_STATUS" | cut -d ':' -f 2)
    BODY=$(echo "$RESPONSE" | grep -v "HTTP_STATUS")

    echo "Estado HTTP: $HTTP_STATUS"
    echo "Cuerpo de la Respuesta:"
    echo "$BODY" | json_pp
}

# 5. ERROR: Operación sobre ID Inexistente (400/404)
# NOTA: Tu código maneja esto con 'IllegalArgumentException' que mapea a 400.
test_update_non_existent() {
    print_header "5. ❓ UPDATE (PUT): Usuario ID $USER_ID_INEXISTENTE no encontrado (400/404)"

    UPDATE_PAYLOAD='{"nombre": "Test Update"}'

    RESPONSE=$(curl -s -X PUT "${API_HOST}${API_BASE_PATH}/${USER_ID_INEXISTENTE}" \
        -H "Content-Type: application/json" \
        -d "$UPDATE_PAYLOAD" -w "\nHTTP_STATUS:%{http_code}")

    HTTP_STATUS=$(echo "$RESPONSE" | grep "HTTP_STATUS" | cut -d ':' -f 2)
    BODY=$(echo "$RESPONSE" | grep -v "HTTP_STATUS")

    echo "Estado HTTP: $HTTP_STATUS (Esperado 400 o 404)"
    echo "Cuerpo de la Respuesta (con mensaje de error):"
    echo "$BODY" | json_pp
}


# ==================================
# Ejecución Principal
# ==================================

test_missing_fields
test_size_violation
test_empty_subscription_list
test_domain_error
test_update_non_existent

echo -e "\n--- Fin de las Pruebas de Errores ---"
