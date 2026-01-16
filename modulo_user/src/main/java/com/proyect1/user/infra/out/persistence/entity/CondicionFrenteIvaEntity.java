package com.proyect1.user.infra.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "condiciones_frente_iva")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CondicionFrenteIvaEntity {

    @Id
    @Column(length = 10)
    private Long id;

    @Column(nullable = false)
    private String condicion;

    private String tipo;

    private String descripcion;

    @OneToMany(mappedBy = "condicionFrenteIva")
    private List<UserEmpresaEntity> empresas;

}