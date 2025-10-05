package com.photocrm.config;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class MultiTenantConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl<String> {

    @Autowired
    private DataSource dataSource;

    @Override
    protected DataSource selectAnyDataSource() {
        return dataSource;
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        return dataSource;
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        Connection connection = super.getConnection(tenantIdentifier);
        
        // Set the tenant context for Row Level Security
        if (tenantIdentifier != null && !tenantIdentifier.isEmpty()) {
            try (var statement = connection.prepareStatement("SELECT set_tenant_context(?)")) {
                statement.setObject(1, java.util.UUID.fromString(tenantIdentifier));
                statement.execute();
            }
        }
        
        return connection;
    }
}