-- Enable Row Level Security (RLS) for multi-tenant isolation
-- This ensures complete data isolation between tenants

-- Create application role for the application
CREATE ROLE application_role;
GRANT USAGE ON SCHEMA public TO application_role;

-- Grant necessary permissions to application_role
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO application_role;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO application_role;

-- Enable RLS on all tenant-scoped tables
ALTER TABLE tenants ENABLE ROW LEVEL SECURITY;
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE clients ENABLE ROW LEVEL SECURITY;
ALTER TABLE orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE photos ENABLE ROW LEVEL SECURITY;
ALTER TABLE photo_selections ENABLE ROW LEVEL SECURITY;
ALTER TABLE invoices ENABLE ROW LEVEL SECURITY;
ALTER TABLE payments ENABLE ROW LEVEL SECURITY;
ALTER TABLE expenses ENABLE ROW LEVEL SECURITY;
ALTER TABLE notifications ENABLE ROW LEVEL SECURITY;
ALTER TABLE audit_logs ENABLE ROW LEVEL SECURITY;

-- Create RLS policies for tenant isolation
-- These policies ensure that users can only access data from their own tenant

-- Tenants table - only allow access to own tenant
CREATE POLICY tenant_isolation_tenants ON tenants
    FOR ALL TO application_role
    USING (id = current_setting('app.current_tenant_id')::uuid);

-- Users table - only allow access to users from current tenant
CREATE POLICY tenant_isolation_users ON users
    FOR ALL TO application_role
    USING (tenant_id = current_setting('app.current_tenant_id')::uuid);

-- Clients table - only allow access to clients from current tenant
CREATE POLICY tenant_isolation_clients ON clients
    FOR ALL TO application_role
    USING (tenant_id = current_setting('app.current_tenant_id')::uuid);

-- Orders table - only allow access to orders from current tenant
CREATE POLICY tenant_isolation_orders ON orders
    FOR ALL TO application_role
    USING (tenant_id = current_setting('app.current_tenant_id')::uuid);

-- Photos table - only allow access to photos from current tenant
CREATE POLICY tenant_isolation_photos ON photos
    FOR ALL TO application_role
    USING (tenant_id = current_setting('app.current_tenant_id')::uuid);

-- Photo selections table - only allow access to selections from current tenant
CREATE POLICY tenant_isolation_photo_selections ON photo_selections
    FOR ALL TO application_role
    USING (tenant_id = current_setting('app.current_tenant_id')::uuid);

-- Invoices table - only allow access to invoices from current tenant
CREATE POLICY tenant_isolation_invoices ON invoices
    FOR ALL TO application_role
    USING (tenant_id = current_setting('app.current_tenant_id')::uuid);

-- Payments table - only allow access to payments from current tenant
CREATE POLICY tenant_isolation_payments ON payments
    FOR ALL TO application_role
    USING (tenant_id = current_setting('app.current_tenant_id')::uuid);

-- Expenses table - only allow access to expenses from current tenant
CREATE POLICY tenant_isolation_expenses ON expenses
    FOR ALL TO application_role
    USING (tenant_id = current_setting('app.current_tenant_id')::uuid);

-- Notifications table - only allow access to notifications from current tenant
CREATE POLICY tenant_isolation_notifications ON notifications
    FOR ALL TO application_role
    USING (tenant_id = current_setting('app.current_tenant_id')::uuid);

-- Audit logs table - only allow access to audit logs from current tenant
CREATE POLICY tenant_isolation_audit_logs ON audit_logs
    FOR ALL TO application_role
    USING (tenant_id = current_setting('app.current_tenant_id')::uuid);

-- Create function to set tenant context
CREATE OR REPLACE FUNCTION set_tenant_context(tenant_uuid UUID)
RETURNS void AS $$
BEGIN
    PERFORM set_config('app.current_tenant_id', tenant_uuid::text, true);
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Create function to get current tenant ID
CREATE OR REPLACE FUNCTION get_current_tenant_id()
RETURNS UUID AS $$
BEGIN
    RETURN current_setting('app.current_tenant_id')::uuid;
EXCEPTION
    WHEN OTHERS THEN
        RETURN NULL;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Grant execute permissions
GRANT EXECUTE ON FUNCTION set_tenant_context(UUID) TO application_role;
GRANT EXECUTE ON FUNCTION get_current_tenant_id() TO application_role;