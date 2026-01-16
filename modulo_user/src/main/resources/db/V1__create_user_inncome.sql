-- ============================================
-- V1__create_user_inncome.sql
-- Ubicación: src/main/resources/db/migration/
-- ============================================

-- Tabla para usuarios de la suscripción "inncome"
CREATE TABLE user_inncome (
    user_id BIGINT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    internal_type VARCHAR(50),
    numero_documento VARCHAR(50),
    establecimiento_id BIGINT,
    manzana VARCHAR(50),
    lote VARCHAR(50),
    telefono VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT true
);

-- Índices para búsquedas frecuentes
CREATE INDEX idx_user_inncome_numero_documento ON user_inncome(numero_documento);
CREATE INDEX idx_user_inncome_establecimiento ON user_inncome(establecimiento_id);
CREATE INDEX idx_user_inncome_is_active ON user_inncome(is_active);

-- Comentarios
COMMENT ON TABLE user_inncome IS 'Usuarios de la suscripción inncome';
COMMENT ON COLUMN user_inncome.user_id IS 'ID del usuario (mismo que en Auth Service)';
COMMENT ON COLUMN user_inncome.internal_type IS 'Tipo interno de usuario (PROPIETARIO, INQUILINO, etc.)';
COMMENT ON COLUMN user_inncome.establecimiento_id IS 'ID del establecimiento al que pertenece';
COMMENT ON COLUMN user_inncome.manzana IS 'Manzana del lote';
COMMENT ON COLUMN user_inncome.lote IS 'Número de lote';

-- Datos de ejemplo para testing
INSERT INTO user_inncome (
    user_id, nombre, apellido, internal_type,
    numero_documento, establecimiento_id,
    manzana, lote, telefono
) VALUES
(1, 'Juan', 'Pérez', 'PROPIETARIO', '12345678', 1, 'A', '10', '2614123456'),
(2, 'María', 'González', 'INQUILINO', '87654321', 1, 'B', '5', '2614654321'),
(3, 'Carlos', 'Rodríguez', 'PROPIETARIO', '11223344', 2, 'C', '15', '2614789012');