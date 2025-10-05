package com.photocrm.config;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TenantContext {

    private static final ThreadLocal<UUID> currentTenant = new ThreadLocal<>();

    public static void setCurrentTenant(UUID tenantId) {
        currentTenant.set(tenantId);
        if (tenantId != null) {
            CurrentTenantIdentifierResolver.setCurrentTenant(tenantId.toString());
        } else {
            CurrentTenantIdentifierResolver.clearCurrentTenant();
        }
    }

    public static UUID getCurrentTenant() {
        return currentTenant.get();
    }

    public static void clear() {
        currentTenant.remove();
        CurrentTenantIdentifierResolver.clearCurrentTenant();
    }

    public static boolean hasCurrentTenant() {
        return currentTenant.get() != null;
    }
}