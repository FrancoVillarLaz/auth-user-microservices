package org.proyect1.dto.auth;

import java.time.Instant;

public class ParsedToken {
    public Long userId;
    public String role;
    public String issuer;
    public Instant expiresAt;
}
