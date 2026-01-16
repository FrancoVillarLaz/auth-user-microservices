package proyect1.gateway.dto;

public record TokenResponse(
         Boolean active,
         String userId,
         String role
){

}
