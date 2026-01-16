#!/bin/bash

# --- Configuración y Colores ---
# Colores para la salida en terminal
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
NC='\033[0m' # Sin color

USER_SERVICE="http://localhost:8083"
TESTS_RUN=0
TESTS_PASS=0

# Función para ejecutar y reportar un test
run_test() {
    local test_name=$1
    local command=$2
    local success_check=$3
    local failure_message=$4

    TESTS_RUN=$((TESTS_RUN + 1))
    echo -e "${BLUE}Test $TESTS_RUN: $test_name${NC}"

    # Ejecuta el comando y captura la salida y el código HTTP
    if [[ "$success_check" == "HTTP" ]]; then
        RESPONSE=$(eval "$command" | tail -n +1)
        HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
        RESPONSE_BODY=$(echo "$RESPONSE" | head -n -1)
        EXPECTED_CODE=$4

        if [ "$HTTP_CODE" = "$EXPECTED_CODE" ]; then
            echo -e "${GREEN}  ✓ Éxito (Código $HTTP_CODE)${NC}"
            TESTS_PASS=$((TESTS_PASS + 1))
        else
            echo -e "${RED}  ✗ Fallo. Esperado $EXPECTED_CODE, Recibido $HTTP_CODE${NC}"
            echo -e "    Cuerpo de Respuesta: $RESPONSE_BODY"
        fi
    else
        OUTPUT=$(eval "$command")
        if eval "$success_check"; then
            echo -e "${GREEN}  ✓ Éxito${NC}"
            TESTS_PASS=$((TESTS_PASS + 1))
            if [[ "$test_name" == "Health Check" ]]; then
                 echo "    $OUTPUT"
            elif [[ "$test_name" == "Obtener info de usuario" ]]; then
                 echo "    Response:"
                 echo "$OUTPUT" | jq '.' 2>/dev/null || echo "$OUTPUT"
            fi
        else
            echo -e "${RED}  ✗ Fallo. $failure_message${NC}"
            echo "    Salida: $OUTPUT"
        fi
    fi
}

# --- Inicio de los Tests ---

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}🚀 Ejecutando Tests contra $USER_SERVICE...${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Test 1: Health check
run_test \
"Health Check" \
"curl -s \"${USER_SERVICE}/api/users/health\"" \
"echo \"\$OUTPUT\" | grep -q \"UP\"" \
"El servicio no reporta 'UP'."

echo ""

# Test 2: Get user info
run_test \
"Obtener info de usuario" \
"curl -s -X POST \"${USER_SERVICE}/api/users/info-by-subscriptions\" -H \"Content-Type: application/json\" -H \"X-User-Id: 1\" -d '{\"idSuscripciones\": [\"sub_inncome\"]}'" \
"echo \"\$OUTPUT\" | grep -q \"userId\"" \
"No se encontró 'userId' en la respuesta."

echo ""

# Test 3: Suscripción no soportada (Esperando 400 Bad Request)
run_test \
"Suscripción no soportada" \
"curl -s -w \"\n%{http_code}\" -X POST \"${USER_SERVICE}/api/users/info-by-subscriptions\" -H \"Content-Type: application/json\" -H \"X-User-Id: 1\" -d '{\"idSuscripciones\": [\"sub_invalid\"]}'" \
"HTTP" \
"400"

echo ""

# --- Resumen Final ---

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}🏁 Resumen de Tests 🏁${NC}"
echo -e "${BLUE}========================================${NC}"
echo "Total de tests ejecutados: $TESTS_RUN"

if [ "$TESTS_PASS" -eq "$TESTS_RUN" ]; then
    echo -e "${GREEN}✅ Todos los $TESTS_PASS tests pasaron.${NC}"
else
    FAILURES=$((TESTS_RUN - TESTS_PASS))
    echo -e "${GREEN}  ✓ Tests Pasados: $TESTS_PASS${NC}"
    echo -e "${RED}  ✗ Tests Fallidos: $FAILURES${NC}"
fi
echo -e "${BLUE}========================================${NC}"
echo ""
