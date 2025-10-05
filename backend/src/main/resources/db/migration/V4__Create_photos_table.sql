-- Create photos table
CREATE TABLE photos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    filename VARCHAR(255) NOT NULL,
    original_url VARCHAR(500) NOT NULL,
    thumbnail_url VARCHAR(500) NOT NULL,
    preview_url VARCHAR(500),
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    metadata JSONB,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_selected BOOLEAN NOT NULL DEFAULT false,
    selection_order INTEGER,
    price DECIMAL(8,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT true
);

-- Create photo_selections table for tracking client selections
CREATE TABLE photo_selections (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    photo_id UUID NOT NULL REFERENCES photos(id) ON DELETE CASCADE,
    selected_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    client_notes TEXT,
    UNIQUE(order_id, photo_id)
);

-- Create indexes for performance
CREATE INDEX idx_photos_tenant_id ON photos(tenant_id);
CREATE INDEX idx_photos_order_id ON photos(order_id);
CREATE INDEX idx_photos_tenant_order ON photos(tenant_id, order_id);
CREATE INDEX idx_photos_selected ON photos(is_selected);
CREATE INDEX idx_photos_order_selected ON photos(order_id, is_selected);
CREATE INDEX idx_photos_uploaded_at ON photos(uploaded_at);
CREATE INDEX idx_photos_mime_type ON photos(mime_type);
CREATE INDEX idx_photos_active ON photos(is_active);

CREATE INDEX idx_photo_selections_tenant_id ON photo_selections(tenant_id);
CREATE INDEX idx_photo_selections_order_id ON photo_selections(order_id);
CREATE INDEX idx_photo_selections_photo_id ON photo_selections(photo_id);
CREATE INDEX idx_photo_selections_selected_at ON photo_selections(selected_at);

-- Add trigger for updated_at
CREATE TRIGGER update_photos_updated_at 
    BEFORE UPDATE ON photos 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();