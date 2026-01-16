package com.proyect1.user.domain.model.standard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Modelo de dominio - Usuario Standard
 * POJO puro sin anotaciones de JPA
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStandard {

    private Long id;
    private Long userId;
    private String nombre;
    private String apellido;
    private String internalType;
    private String correo;
    private String numeroDocumento;
    private Long establecimientoId;
    private String manzana;
    private String lote;
    private String telefono;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Lógica de dominio: Obtener nombre completo
     */
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    /**
     * Lógica de dominio: Verificar si es propietario
     */
    public boolean isPropietario() {
        return "PROPIETARIO".equalsIgnoreCase(internalType);
    }

    /**
     * Convierte el modelo de dominio a un Map<String, Object>.
     * Este método es crucial para serializar los datos al caché y al adaptador web (JSON).
     * Incluye los datos calculados por el dominio.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id);
        map.put("userId", this.userId);
        map.put("nombre", this.nombre);
        map.put("apellido", this.apellido);

        map.put("correo", this.correo);
        map.put("nombreCompleto", this.getNombreCompleto());
        // map.put("isPropietario", this.isPropietario());

        map.put("internalType", this.internalType);
        map.put("numeroDocumento", this.numeroDocumento);
        map.put("establecimientoId", this.establecimientoId);
        map.put("manzana", this.manzana);
        map.put("lote", this.lote);
        map.put("telefono", this.telefono);
        map.put("isActive", this.isActive);
        map.put("createdAt", this.createdAt);

        return map;
    }

    // ... (activate, deactivate, etc.)
    // Lógica de dominio: Activar usuario
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    // Lógica de dominio: Desactivar usuario
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }
}