CREATE TABLE IF NOT EXISTS hiredvehicleidtype (
    idtype_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    idtype_sup UUID NOT NULL REFERENCES supplier(supplier_id),
    supplier_code VARCHAR NOT NULL,
    idtype_typeid UUID NOT NULL REFERENCES vehicle_type(type_id),
    idtype_code VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_hiredvehicleidtype_supplier_code UNIQUE (idtype_sup, idtype_code)
);

CREATE INDEX IF NOT EXISTS idx_hiredvehicleidtype_supplier ON hiredvehicleidtype(idtype_sup);
CREATE INDEX IF NOT EXISTS idx_hiredvehicleidtype_type ON hiredvehicleidtype(idtype_typeid);
DROP TRIGGER IF EXISTS trg_hiredvehicleidtype_upd ON hiredvehicleidtype;
CREATE TRIGGER trg_hiredvehicleidtype_upd BEFORE UPDATE ON hiredvehicleidtype FOR EACH ROW EXECUTE FUNCTION set_updated_at();
