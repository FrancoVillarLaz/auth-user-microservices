package com.proyect1.user.ports.in;

import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface UserCrudUseCase {
    Map<String, Object> createUser(Object command, String subscriptionId);

    Map<String, Object> getUserInfo(Long userId, String subscriptionId);

    Map<String, Object> getAllUsers(String subscriptionId, Object filter, Pageable pageable);

    Map<String, Object> updateUser(Long userId, Object command, String subscriptionId);

    Map<String, Object> deactivateUser(Long userId, String subscriptionId);

    Map<String, Object> activateUser(Long userId, String subscriptionId);

}
