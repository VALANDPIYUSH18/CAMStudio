package com.photocrm.service;

import com.photocrm.entity.TenantEntity;
import com.photocrm.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    public TenantEntity createTenant(String name, String subdomain, TenantEntity.SubscriptionPlan plan) {
        if (tenantRepository.existsBySubdomain(subdomain)) {
            throw new RuntimeException("Subdomain already exists: " + subdomain);
        }

        TenantEntity tenant = new TenantEntity(name, subdomain, plan);
        return tenantRepository.save(tenant);
    }

    public Optional<TenantEntity> getTenantById(UUID tenantId) {
        return tenantRepository.findById(tenantId);
    }

    public Optional<TenantEntity> getTenantBySubdomain(String subdomain) {
        return tenantRepository.findBySubdomainAndIsActiveTrue(subdomain);
    }

    public TenantEntity updateTenant(UUID tenantId, String name, TenantEntity.SubscriptionPlan plan, String settings) {
        TenantEntity tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new RuntimeException("Tenant not found"));

        tenant.setName(name);
        tenant.setPlan(plan);
        tenant.setSettings(settings);

        return tenantRepository.save(tenant);
    }

    public void deactivateTenant(UUID tenantId) {
        TenantEntity tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new RuntimeException("Tenant not found"));

        tenant.setIsActive(false);
        tenantRepository.save(tenant);
    }

    public List<TenantEntity> getAllActiveTenants() {
        return tenantRepository.findAllActiveTenants();
    }

    public long countActiveTenants() {
        return tenantRepository.countActiveTenants();
    }

    public boolean existsBySubdomain(String subdomain) {
        return tenantRepository.existsBySubdomain(subdomain);
    }

    public TenantEntity getTenantByIdAndActive(UUID tenantId) {
        return tenantRepository.findById(tenantId)
            .filter(TenantEntity::getIsActive)
            .orElseThrow(() -> new RuntimeException("Active tenant not found"));
    }
}