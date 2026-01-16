package com.proyect1.user.ports.in;

import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface UserSpecificUseCase {

    Map<String, Object> getAllUsersWithEmail(String subscriptionId, Object filter, Pageable pageable);

    Map<String, Object> getUserStandardDomicilio(Long userId, String subscriptionId);

    Map<String, Object> getEmpresaByCuit(String cuit, String subscriptionId);

    Map<String, Object> getEmpresaById(Long empresaId, String subscriptionId);
}