package com.proyect1.user.infra.out.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/*
 * Entidad JPA user_Empresa
 * SEPARADA del modelo de dominio
 * se deberia sacar las anotaciones lombok
 */
@Entity
@Table(name = "user_empresas")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserEmpresaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, unique = true)
    private String cuit;

    @Column(name = "razon_social", nullable = false)
    private String razonSocial;

    // 1. COLUMNA REAL que almacena el ID en la base de datos
    @Column(name = "auth_user_id", nullable = false)
    private Long authUserId;

    @Column(length = 20)
    private String telefono;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "internal_type", length = 50)
    private String internalType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condicion_iva_id", foreignKey = @ForeignKey(name = "FK_EMPRESA_CONDICION_IVA"))
    private CondicionFrenteIvaEntity condicionFrenteIva;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}