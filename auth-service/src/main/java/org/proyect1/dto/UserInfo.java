package org.proyect1.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class UserInfo {
    public Long id;
    public String role;

    public UserInfo(Long id, String role) {
        this.id = id;
        this.role = role;
    }
}