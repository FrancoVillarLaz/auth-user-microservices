package com.proyect1.user.application.strategy.empresa;

import java.util.Map;

public interface EmpresaSpecificStrategy {

    /**
     * Obtiene empresa por CUIT
     */
    Map<String, Object> getEmpresaByCuit(String cuit, String subscriptionId);

    /**
     * Obtiene empresa por ID de empresa (no userId)
     */
    Map<String, Object> getEmpresaById(Long empresaId, String subscriptionId);

    // Map<String, Object> getEmpresaBalance(Long empresaId, String subscriptionId);
    // Map<String, Object> getEmpresaContacts(Long empresaId, String subscriptionId);
}