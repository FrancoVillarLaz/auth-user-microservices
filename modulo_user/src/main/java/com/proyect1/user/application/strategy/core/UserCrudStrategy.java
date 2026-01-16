package com.proyect1.user.application.strategy.core;

import org.springframework.data.domain.Pageable;
import java.util.Map;

public interface UserCrudStrategy {

    // CREATE - Acepta Object para diferentes comandos
    Map<String, Object> createUser(Object command, String subscriptionId);

    // READ
    Map<String, Object> getUserInfo(Long userId, String subscriptionId);
    Map<String, Object> getAllUsers(String subscriptionId, Object filter, Pageable pageable);

    // UPDATE - Acepta Object
    Map<String, Object> updateUser(Long userId, Object command, String subscriptionId);

    // DELETE (lógico)
    Map<String, Object> deactivateUser(Long userId, String subscriptionId);
    Map<String, Object> activateUser(Long userId, String subscriptionId);

    // Identificador
    String getSupportedSubscriptionId(String sub);


}