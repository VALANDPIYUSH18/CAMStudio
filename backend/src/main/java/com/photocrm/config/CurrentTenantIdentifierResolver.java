package com.photocrm.config;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class CurrentTenantIdentifierResolver implements CurrentTenantIdentifierResolver<String> {

    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = currentTenant.get();
        return tenantId != null ? tenantId : "public";
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

    public static void setCurrentTenant(String tenantId) {
        currentTenant.set(tenantId);
    }

    public static void clearCurrentTenant() {
        currentTenant.remove();
    }

    public static String getCurrentTenant() {
        return currentTenant.get();
    }
}