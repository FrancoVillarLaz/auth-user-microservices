package com.proyect1.user.domain.model;

import com.proyect1.user.infra.out.persistence.entity.UserEmpresaEntity;
import com.proyect1.user.infra.out.persistence.entity.UserStandardEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuth {
    private Long id;

    private String correo;

    private Integer estado;

    private String username;

    private List<UserEmpresaEntity> empresas = new ArrayList<>();

    private List<UserStandardEntity> standard = new ArrayList<>();

}
