# Script para generar llaves RSA para JWT

KEYS_DIR="src/main/resources"

# Crear directorio si no existe
mkdir -p $KEYS_DIR

# Generar llave privada
openssl genrsa -out $KEYS_DIR/privateKey.pem 2048

# Generar llave pública
openssl rsa -in $KEYS_DIR/privateKey.pem -pubout -out $KEYS_DIR/publicKey.pem

echo "Llaves generadas exitosamente en $KEYS_DIR"
echo "- privateKey.pem"
echo "- publicKey.pem"
