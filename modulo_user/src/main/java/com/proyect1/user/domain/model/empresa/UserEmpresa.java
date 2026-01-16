package com.proyect1.user.domain.model.empresa;

import lombok.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Modelo de dominio - UserEmpresa
 * POJO puro sin anotaciones de JPA
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEmpresa {

    private Long id;
    private String cuit;
    private String razonSocial;
    private String telefono;
    private String internalType;
    private Long userAuthId;
    private String correo;
    private CondicionFrenteIva CondicionFrenteIva;

    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Lógica de dominio: Validar formato de CUIT
     * Formato esperado: XX-XXXXXXXX-X (con guiones)
     */
    public boolean isValidCuit() {
        if (cuit == null || cuit.trim().isEmpty()) {
            return false;
        }

        // Patrón básico: XX-XXXXXXXX-X
        String cuitPattern = "^\\d{2}-\\d{8}-\\d{1}$";
        return cuit.matches(cuitPattern);
    }

    /**
     * Lógica de dominio: Obtener CUIT sin guiones
     */
    public String getCuitSinGuiones() {
        if (cuit == null) {
            return null;
        }
        return cuit.replace("-", "");
    }

    /**
     * Lógica de dominio: Verificar si la empresa está activa
     */
    public boolean estaActiva() {
        return Boolean.TRUE.equals(isActive);
    }

    /**
     * Lógica de dominio: Validar datos básicos de la empresa
     */
    public boolean isValid() {
        return cuit != null && !cuit.trim().isEmpty() &&
                razonSocial != null && !razonSocial.trim().isEmpty() &&
                isValidCuit();
    }

    /**
     * Lógica de dominio: Activar empresa
     */
    public void activar() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Lógica de dominio: Desactivar empresa
     */
    public void desactivar() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Lógica de dominio: Actualizar información de contacto
     */
    public void actualizarContacto(String nuevoTelefono) {
        if (nuevoTelefono != null && !nuevoTelefono.trim().isEmpty()) {
            this.telefono = nuevoTelefono.trim();
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Lógica de dominio: Actualizar razón social
     */
    public void actualizarRazonSocial(String nuevaRazonSocial) {
        if (nuevaRazonSocial != null && !nuevaRazonSocial.trim().isEmpty()) {
            this.razonSocial = nuevaRazonSocial.trim();
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Lógica de dominio: Obtener información resumida de la empresa
     */
    public String getResumen() {
        return String.format("%s (CUIT: %s)", razonSocial, cuit);
    }

    /**
     * Lógica de dominio: Validar si el teléfono tiene formato válido
     * Formato básico: al menos 8 dígitos
     */
    public boolean tieneTelefonoValido() {
        if (telefono == null || telefono.trim().isEmpty()) {
            return false;
        }
        String telefonoLimpio = telefono.replaceAll("[^0-9]", "");
        return telefonoLimpio.length() >= 8;
    }

    /**
     * Convierte el modelo de dominio a un Map<String, Object>.
     * Este método es crucial para serializar los datos al caché y al adaptador web (JSON).
     * Incluye los datos calculados por el dominio.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id);
        map.put("cuit", this.cuit);
        map.put("razonSocial", this.razonSocial);
        map.put("correo", this.getCorreo());
        map.put("userId", this.getUserAuthId());
        map.put("telefono", this.telefono);
        map.put("isActive", this.isActive);
        map.put("createdAt", this.createdAt);
        map.put("updatedAt", this.updatedAt);
        map.put("CondicionIva", this.CondicionFrenteIva);
        // Incluir lógica de dominio calculada
        map.put("cuitSinGuiones", this.getCuitSinGuiones());
        map.put("estaActiva", this.estaActiva());
        map.put("resumen", this.getResumen());
        map.put("tieneTelefonoValido", this.tieneTelefonoValido());
        map.put("isValid", this.isValid());
        map.put("cuitValido", this.isValidCuit());


        return map;
    }



    /**
     * Lógica de dominio: Verificar si puede ser eliminada
     * (Por ejemplo, solo si está desactivada)
     */
    public boolean puedeSerEliminada() {
        return !estaActiva();
    }
}
