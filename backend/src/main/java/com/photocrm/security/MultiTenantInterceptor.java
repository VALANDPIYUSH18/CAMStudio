package com.photocrm.security;

import com.photocrm.config.TenantContext;
import com.photocrm.repository.TenantRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class MultiTenantInterceptor implements HandlerInterceptor {

    @Autowired
    private TenantRepository tenantRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Extract tenant from subdomain
        String host = request.getServerName();
        String subdomain = extractSubdomain(host);
        
        if (subdomain != null && !subdomain.isEmpty() && !subdomain.equals("www")) {
            // Find tenant by subdomain
            tenantRepository.findBySubdomainAndIsActiveTrue(subdomain)
                .ifPresent(tenant -> TenantContext.setCurrentTenant(tenant.getId()));
        }
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                              Object handler, Exception ex) {
        // Clear tenant context after request
        TenantContext.clear();
    }

    private String extractSubdomain(String host) {
        if (host == null || host.isEmpty()) {
            return null;
        }
        
        String[] parts = host.split("\\.");
        if (parts.length > 2) {
            return parts[0];
        }
        
        return null;
    }
}