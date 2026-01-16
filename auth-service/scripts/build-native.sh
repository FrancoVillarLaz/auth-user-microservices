#!/bin/bash

# Script para compilar la aplicación a binario nativo con GraalVM usando Docker builder

set -e

APP_NAME="auth-service"
VERSION="1.0.0"
RUNNER="target/${APP_NAME}-${VERSION}-runner"

echo "🚀 Iniciando compilación nativa con GraalVM Docker builder..."

# Limpiar compilaciones anteriores
echo "🧹 Limpiando compilaciones anteriores..."
./mvnw clean

# Compilar con profile native usando Docker builder
echo "🔨 Compilando a binario nativo (esto puede tomar varios minutos)..."
./mvnw package -Pnative \
  -Dquarkus.native.container-build=true \
  -Dquarkus.native.builder-image=quay.io/quarkus/ubi9-quarkus-graalvmce-builder-image:jdk-21 \
  -DskipTests

# Verificar que se creó el binario
if [ -f "$RUNNER" ]; then
    echo "✅ Compilación exitosa!"
    echo ""
    echo "📦 Binario nativo creado en: $RUNNER"
    echo ""

    # Mostrar tamaño del binario
    SIZE=$(du -h "$RUNNER" | cut -f1)
    echo "📏 Tamaño del binario: $SIZE"
    echo ""

    # Mostrar instrucciones de ejecución
    echo "🎯 Para ejecutar el binario nativo:"
    echo "   $RUNNER"
    echo ""
    echo "🐳 Para crear imagen Docker nativa:"
    echo "   docker build -f Dockerfile.nativo -t ${APP_NAME}-native ."
else
    echo "❌ Error: No se pudo crear el binario nativo"
    exit 1
fi
