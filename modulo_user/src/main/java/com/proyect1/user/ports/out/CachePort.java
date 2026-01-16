package com.proyect1.user.ports.out;

import java.util.Map;
import java.util.Optional;

/**
 * Puerto de salida para interactuar con la capa de caché.
 *
 * Define el contrato para las operaciones de caché necesarias por la capa de aplicación.
 * El adaptador (RedisCacheAdapter) debe implementar esta interfaz.
 */
public interface CachePort {

    /**
     * Recupera un valor de la caché dado una clave.
     *
     * @param key La clave (String) a buscar.
     * @return Un Optional que contiene un Map<String, Object> si se encuentra, o vacío si no.
     */
    Optional<Map<String, Object>> get(String key);

    /**
     * Almacena un valor en la caché con un tiempo de vida (TTL) especificado.
     *
     * @param key La clave (String) a guardar.
     * @param value El valor (Map<String, Object>) a guardar.
     * @param ttlSeconds El tiempo de vida en segundos.
     */
    void put(String key, Map<String, Object> value, long ttlSeconds);

    /**
     * Invalida (elimina) una clave específica de la caché.
     *
     * @param key La clave a invalidar.
     */
    void invalidate(String key);

    /**
     * Invalida (elimina) todas las claves que coinciden con un patrón dado.
     *
     * @param pattern El patrón (glob) de la clave a buscar (e.g., "user:*").
     */
    void invalidatePattern(String pattern);
}