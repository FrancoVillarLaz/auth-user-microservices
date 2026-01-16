package org.proyect1.dto.auth.response;


import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class TokenResponse {
    public String accessToken;
    public String refreshToken;
    public String tokenType = "Bearer";
    public Long expiresIn;

    public TokenResponse(String accessToken, String refreshToken, Long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }
}
