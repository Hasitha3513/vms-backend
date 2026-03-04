CREATE OR REPLACE VIEW supplier_overview_v AS
SELECT
    COUNT(*) AS total_suppliers,
    COUNT(*) FILTER (WHERE COALESCE(is_active, FALSE) = TRUE) AS active_suppliers,
    COUNT(*) FILTER (WHERE COALESCE(is_active, FALSE) = FALSE) AS inactive_suppliers,
    COUNT(DISTINCT supplier_type_id) FILTER (WHERE supplier_type_id IS NOT NULL) AS total_types
FROM supplier;

CREATE OR REPLACE VIEW supplier_type_summary_v AS
SELECT
    s.supplier_type_id AS supplier_type_id,
    COALESCE(st.type_name, 'Unknown') AS supplier_type_name,
    COUNT(*) AS supplier_count
FROM supplier s
LEFT JOIN supplier_type st ON st.type_id = s.supplier_type_id
GROUP BY s.supplier_type_id, st.type_name;

CREATE OR REPLACE VIEW supplier_details_v AS
SELECT
    s.supplier_id,
    s.supplier_code,
    s.supplier_name,
    s.contact_name,
    s.contact_person,
    s.phone,
    s.email,
    s.address,
    s.tax_id,
    s.supplier_type_id,
    st.type_name AS supplier_type_name,
    s.is_active,
    s.created_at
FROM supplier s
LEFT JOIN supplier_type st ON st.type_id = s.supplier_type_id;
