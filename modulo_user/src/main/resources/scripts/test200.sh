#!/bin/bash

# ==========================================================
# Script para Testear el Flujo de Éxito (CRUD)
# ==========================================================

# Variables de Configuración
API_HOST="http://localhost:8083" # Host actualizado según tu configuración
API_BASE_PATH="/api/users"
TEST_USER_ID="" # Almacenará el ID creado

# Datos de prueba para la creación
CREATE_PAYLOAD='{
    "nombre": "Sofia",
    "apellido": "Gomez",
    "internalType": "EMPLEADO",
    "numeroDocumento": "99887766",
    "establecimientoId": 500,
    "manzana": "Z",
    "lote": "99",
    "telefono": "1155443322",
    "isActive": true
}'

# Datos de prueba para la actualización
UPDATE_PAYLOAD='{
    "nombre": "Sofia Carolina",
    "apellido": "Gomez Diaz",
    "internalType": "MANAGER",
    "establecimientoId": 501,
    "telefono": "1177665544",
    "isActive": true
}'

# Datos para la prueba de getUserInfo
INFO_PAYLOAD='{
    "idSuscripciones": [1, 2]
}'

# ==================================
# Funciones de Logging y Ejecución
# ==================================

print_header() {
    echo ""
    echo "=========================================================="
    echo "▶️  $1 (Esperado: $2)"
    echo "=========================================================="
}

if command -v jq &> /dev/null; then
    json_pp() { jq '.'; }
else
    json_pp() { cat; }
    echo "⚠️ ADVERTENCIA: 'jq' no encontrado. La salida JSON no será formateada."
fi

# Función para ejecutar curl y mostrar el resultado
execute_test() {
    local METHOD=$1
    local URL=$2
    local PAYLOAD=$3
    local EXPECTED_STATUS=$4
    local HEADER_USER_ID=$5

    local HEADER=""
    if [ -n "$PAYLOAD" ]; then
        HEADER="-H \"Content-Type: application/json\""
        PAYLOAD_ARG="-d '$PAYLOAD'"
    fi

    if [ -n "$HEADER_USER_ID" ]; then
        HEADER="$HEADER -H \"X-User-Id: $HEADER_USER_ID\""
    fi

    # Ejecuta curl y captura la respuesta
    COMMAND="curl -s -X $METHOD \"${API_HOST}${URL}\" $HEADER $PAYLOAD_ARG -w \"\nHTTP_STATUS:%{http_code}\""

    RESPONSE_AND_STATUS=$(eval $COMMAND 2>/dev/null)

    HTTP_STATUS=$(echo "$RESPONSE_AND_STATUS" | grep "HTTP_STATUS" | tail -n 1 | cut -d ':' -f 2)
    BODY=$(echo "$RESPONSE_AND_STATUS" | grep -v "HTTP_STATUS")

    if [ "$HTTP_STATUS" == "$EXPECTED_STATUS" ]; then
        echo "✅ Éxito - Estado HTTP: $HTTP_STATUS"
    else
        echo "❌ Fallo - Estado HTTP: $HTTP_STATUS (Esperado: $EXPECTED_STATUS)"
    fi
    echo "Cuerpo de la Respuesta:"
    echo "$BODY" | json_pp
}

# 1. TEST: Health Check (GET)
test_health_check() {
    print_header "1. 🩺 HEALTH CHECK" "200"
    execute_test "GET" "${API_BASE_PATH}/health" "" "200"
}

# 2. TEST: CREAR Usuario (POST)
test_create_user() {
    print_header "2. ➕ CREAR USUARIO (POST ${API_BASE_PATH})" "201"

    RESPONSE=$(curl -s -X POST "${API_HOST}${API_BASE_PATH}" \
        -H "Content-Type: application/json" \
        -d "$CREATE_PAYLOAD" -w "\nHTTP_STATUS:%{http_code}")

    HTTP_STATUS=$(echo "$RESPONSE" | grep "HTTP_STATUS" | tail -n 1 | cut -d ':' -f 2)
    BODY=$(echo "$RESPONSE" | grep -v "HTTP_STATUS")

    if [ "$HTTP_STATUS" == "201" ]; then
        echo "✅ Éxito - Estado HTTP: $HTTP_STATUS"
    else
        echo "❌ Fallo - Estado HTTP: $HTTP_STATUS (Esperado: 201)"
    fi

    # Extraer el ID del usuario creado
    TEST_USER_ID=$(echo "$BODY" | grep -o '"userId":[0-9]*' | grep -o '[0-9]*')

    if [ -n "$TEST_USER_ID" ]; then
        echo -e "\n✅ ID de usuario creado: $TEST_USER_ID"
    else
        echo -e "\n❌ ERROR: No se pudo extraer el ID de usuario."
    fi
    echo "Cuerpo de la Respuesta:"
    echo "$BODY" | json_pp
}

# 3. TEST: Obtener Información por Suscripciones (POST con Header)
test_get_info() {
    if [ -z "$TEST_USER_ID" ]; then
        echo -e "\n⚠️ Saltando el test de INFO: No hay ID de usuario disponible."
        return
    fi

    print_header "3. 🔍 OBTENER INFO (POST ${API_BASE_PATH}/info-by-subscriptions)" "200"

    execute_test "POST" "${API_BASE_PATH}/info-by-subscriptions" "$INFO_PAYLOAD" "200" "$TEST_USER_ID"
}

# 4. TEST: ACTUALIZAR Usuario (PUT)
test_update_user() {
    if [ -z "$TEST_USER_ID" ]; then
        echo -e "\n⚠️ Saltando el test de UPDATE: No hay ID de usuario disponible."
        return
    fi

    print_header "4. ✏️ ACTUALIZAR USUARIO (PUT ${API_BASE_PATH}/${TEST_USER_ID})" "200"

    execute_test "PUT" "${API_BASE_PATH}/${TEST_USER_ID}" "$UPDATE_PAYLOAD" "200"
}

# 5. TEST: DESACTIVAR Usuario (DELETE Lógico)
test_deactivate_user() {
    if [ -z "$TEST_USER_ID" ]; then # <-- Estructura corregida: sin llave aquí
        echo -e "\n⚠️ Saltando el test de DESACTIVAR: No hay ID de usuario disponible."
        return
    fi # <-- Cierre correcto del bloque if

    print_header "5. ❌ DESACTIVAR USUARIO (DELETE ${API_BASE_PATH}/${TEST_USER_ID})" "200"

    execute_test "DELETE" "${API_BASE_PATH}/${TEST_USER_ID}" "" "200"
}

# 6. TEST: ACTIVAR Usuario (PATCH)
test_activate_user() {
    if [ -z "$TEST_USER_ID" ]; then # <-- Estructura corregida: sin llave aquí
        echo -e "\n⚠️ Saltando el test de ACTIVAR: No hay ID de usuario disponible."
        return
    fi # <-- Cierre correcto del bloque if

    print_header "6. ✅ ACTIVAR USUARIO (PATCH ${API_BASE_PATH}/${TEST_USER_ID}/activate)" "200"

    execute_test "PATCH" "${API_BASE_PATH}/${TEST_USER_ID}/activate" "" "200"
}

# ==================================
# Ejecución Principal
# ==================================

test_health_check
test_create_user

if [ -n "$TEST_USER_ID" ]; then
    test_get_info
    test_update_user
    test_deactivate_user
    test_activate_user
fi

echo -e "\n--- Fin de las Pruebas de Flujo Exitoso ---"
