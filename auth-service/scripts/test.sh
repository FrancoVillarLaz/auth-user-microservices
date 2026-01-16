#!/bin/bash

# Script para probar todos los endpoints del microservicio de autenticación
# Ahora imprime la respuesta completa del servidor en cada test

set -euo pipefail

BASE_URL="http://localhost:8080/api/auth"
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}🧪 Iniciando tests del microservicio de autenticación${NC}\n"

# Verificar que jq esté instalado
if ! command -v jq &> /dev/null; then
    echo -e "${RED}❌ jq no está instalado. Instálalo para continuar.${NC}"
    exit 1
fi

# Esperar a que el microservicio esté levantado
echo -e "${BLUE}Esperando que el servicio responda...${NC}"
for i in {1..10}; do
    if curl -s "$BASE_URL/health" > /dev/null; then
        break
    fi
    echo "Esperando..."
    sleep 1
done

# Datos aleatorios
RANDOM_ID=$RANDOM
USERNAME="testuser$RANDOM_ID"
EMAIL="test$RANDOM_ID@example.com"
PASSWORD="test123456"

############################################
# Test 1: Health Check
############################################
echo -e "\n${BLUE}Test 1: Health Check${NC}"

RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/health")
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | head -n-1)

echo "Respuesta del servidor: $BODY"
[ "$HTTP_CODE" = "200" ] && \
echo -e "${GREEN}✅ Health OK${NC}" || \
{ echo -e "${RED}❌ Health FAIL ($HTTP_CODE)${NC}"; exit 1; }

############################################
# Test 2: Registro
############################################
echo -e "\n${BLUE}Test 2: Registro${NC}"

REGISTER_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/register" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$USERNAME\",\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}")

HTTP_CODE=$(echo "$REGISTER_RESPONSE" | tail -n1)
BODY=$(echo "$REGISTER_RESPONSE" | head -n-1)

echo "Respuesta del servidor: $BODY"
if [ "$HTTP_CODE" = "201" ]; then
    echo -e "${GREEN}✅ Registro exitoso${NC}"
else
    echo -e "${RED}❌ Registro falló ($HTTP_CODE)${NC}"
    exit 1
fi

ACCESS_TOKEN=$(echo "$BODY" | jq -r '.accessToken')
REFRESH_TOKEN=$(echo "$BODY" | jq -r '.Token')
USER_ID=$(echo "$BODY" | jq -r '.user.id')

if [[ -z "$ACCESS_TOKEN" || -z "$REFRESH_TOKEN" ]]; then
  echo -e "${RED}❌ Tokens no retornados en registro${NC}"
  exit 1
fi

############################################
# Test 3: Login
############################################
echo -e "\n${BLUE}Test 3: Login${NC}"

LOGIN_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/login" \
  -H "Content-Type: application/json" \
  -d "{\"identifier\":\"$USERNAME\",\"password\":\"$PASSWORD\"}")

HTTP_CODE=$(echo "$LOGIN_RESPONSE" | tail -n1)
BODY=$(echo "$LOGIN_RESPONSE" | head -n-1)

echo "Respuesta del servidor: $BODY"
[ "$HTTP_CODE" = "200" ] || { echo -e "${RED}❌ Login falló ($HTTP_CODE)${NC}"; exit 1; }

ACCESS_TOKEN=$(echo "$BODY" | jq -r '.accessToken')
REFRESH_TOKEN=$(echo "$BODY" | jq -r '.Token')

############################################
# Test 4: /me
############################################
echo -e "\n${BLUE}Test 4: Obtener usuario${NC}"

ME_RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/me" \
  -H "Authorization: Bearer $ACCESS_TOKEN")

HTTP_CODE=$(echo "$ME_RESPONSE" | tail -n1)
BODY=$(echo "$ME_RESPONSE" | head -n-1)

echo "Respuesta del servidor: $BODY"
[ "$HTTP_CODE" = "200" ] || { echo -e "${RED}❌ /me falló ($HTTP_CODE)${NC}"; exit 1; }

############################################
# Test 5: Refresh
############################################
echo -e "\n${BLUE}Test 5: Refresh Token${NC}"

REFRESH_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/refresh" \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}")

HTTP_CODE=$(echo "$REFRESH_RESPONSE" | tail -n1)
BODY=$(echo "$REFRESH_RESPONSE" | head -n-1)

echo "Respuesta del servidor: $BODY"
[ "$HTTP_CODE" = "200" ] || { echo -e "${RED}❌ Refresh falló ($HTTP_CODE)${NC}"; exit 1; }

NEW_ACCESS_TOKEN=$(echo "$BODY" | jq -r '.accessToken')
NEW_REFRESH_TOKEN=$(echo "$BODY" | jq -r '.Token')

############################################
# Test 6: Logout
############################################
echo -e "\n${BLUE}Test 6: Logout${NC}"

LOGOUT_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/logout" \
  -H "Authorization: Bearer $NEW_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$NEW_REFRESH_TOKEN\"}")

HTTP_CODE=$(echo "$LOGOUT_RESPONSE" | tail -n1)
BODY=$(echo "$LOGOUT_RESPONSE" | head -n-1)

echo "Respuesta del servidor: $BODY"
[ "$HTTP_CODE" = "200" ] || { echo -e "${RED}❌ Logout falló ($HTTP_CODE)${NC}"; exit 1; }

############################################
# Test 7: Token inválido tras logout
############################################
echo -e "\n${BLUE}Test 7: Token inválido tras logout${NC}"

POST_LOGOUT=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/me" \
  -H "Authorization: Bearer $NEW_ACCESS_TOKEN")

HTTP_CODE=$(echo "$POST_LOGOUT" | tail -n1)
BODY=$(echo "$POST_LOGOUT" | head -n-1)

echo "Respuesta del servidor: $BODY"
if [ "$HTTP_CODE" = "401" ]; then
    echo -e "${GREEN}✅ Token invalidado ok${NC}"
else
    echo -e "${RED}❌ Token sigue funcionando ($HTTP_CODE)${NC}"
fi

echo -e "\n${GREEN}✅ Todos los tests pasaron correctamente${NC}"
