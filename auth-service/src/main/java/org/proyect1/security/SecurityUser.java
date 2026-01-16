package org.proyect1.security;

import java.security.Principal;

//queria usar record pero me da problemas idk
public class SecurityUser implements Principal {

    private final Long id;
    private final String username;
    private final String role;

    public SecurityUser(Long id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public Long id() {
        return id;
    }

    public String username() {
        return username;
    }

    public String role() {
        return role;
    }

    @Override
    public String getName() {
        return username;
    }
}
