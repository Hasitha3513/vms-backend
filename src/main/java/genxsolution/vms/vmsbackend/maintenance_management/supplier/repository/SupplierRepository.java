package genxsolution.vms.vmsbackend.maintenance_management.supplier.repository;

import genxsolution.vms.vmsbackend.maintenance_management.supplier.dto.SupplierDetailsResponse;
import genxsolution.vms.vmsbackend.maintenance_management.supplier.dto.SupplierTypeCountResponse;
import genxsolution.vms.vmsbackend.maintenance_management.supplier.dto.SupplierUpsertRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SupplierRepository {
    private final JdbcTemplate jdbcTemplate;

    public SupplierRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long[] overview() {
        try {
            return jdbcTemplate.queryForObject(
                    """
                    SELECT total_suppliers, active_suppliers, inactive_suppliers, total_types
                    FROM supplier_overview_v
                    """,
                    (rs, rowNum) -> new long[] {
                            rs.getLong("total_suppliers"),
                            rs.getLong("active_suppliers"),
                            rs.getLong("inactive_suppliers"),
                            rs.getLong("total_types")
                    }
            );
        } catch (BadSqlGrammarException ex) {
            return jdbcTemplate.queryForObject(
                    """
                    SELECT
                        COUNT(*) AS total_suppliers,
                        COUNT(*) FILTER (WHERE COALESCE(is_active, FALSE) = TRUE) AS active_suppliers,
                        COUNT(*) FILTER (WHERE COALESCE(is_active, FALSE) = FALSE) AS inactive_suppliers,
                        COUNT(DISTINCT supplier_type_id) FILTER (WHERE supplier_type_id IS NOT NULL) AS total_types
                    FROM supplier
                    """,
                    (rs, rowNum) -> new long[] {
                            rs.getLong("total_suppliers"),
                            rs.getLong("active_suppliers"),
                            rs.getLong("inactive_suppliers"),
                            rs.getLong("total_types")
                    }
            );
        }
    }

    public List<SupplierTypeCountResponse> overviewByType() {
        try {
            return jdbcTemplate.query(
                    """
                    SELECT supplier_type_id, supplier_type_name, supplier_count
                    FROM supplier_type_summary_v
                    ORDER BY supplier_count DESC, supplier_type_name ASC
                    """,
                    (rs, rowNum) -> new SupplierTypeCountResponse(
                            rs.getObject("supplier_type_id", Integer.class),
                            rs.getString("supplier_type_name"),
                            rs.getLong("supplier_count")
                    )
            );
        } catch (BadSqlGrammarException ex) {
            return jdbcTemplate.query(
                    """
                    SELECT
                        s.supplier_type_id AS supplier_type_id,
                        COALESCE(st.type_name, 'Unknown') AS supplier_type_name,
                        COUNT(*) AS supplier_count
                    FROM supplier s
                    LEFT JOIN supplier_type st ON st.type_id = s.supplier_type_id
                    GROUP BY s.supplier_type_id, st.type_name
                    ORDER BY supplier_count DESC, supplier_type_name ASC
                    """,
                    (rs, rowNum) -> new SupplierTypeCountResponse(
                            rs.getObject("supplier_type_id", Integer.class),
                            rs.getString("supplier_type_name"),
                            rs.getLong("supplier_count")
                    )
            );
        }
    }

    public List<SupplierDetailsResponse> details() {
        try {
            return jdbcTemplate.query(
                    """
                    SELECT supplier_id, supplier_code, supplier_name, contact_name, contact_person,
                           phone, email, address, tax_id, supplier_type_id, supplier_type_name, is_active, created_at
                    FROM supplier_details_v
                    ORDER BY created_at DESC, supplier_name ASC
                    """,
                    this::mapDetailsRow
            );
        } catch (BadSqlGrammarException ex) {
            return jdbcTemplate.query(
                    """
                    SELECT s.supplier_id, s.supplier_code, s.supplier_name,
                           NULL::varchar AS contact_name, s.contact_person,
                           s.phone, s.email, s.address, s.tax_id, s.supplier_type_id,
                           st.type_name AS supplier_type_name, s.is_active, s.created_at
                    FROM supplier s
                    LEFT JOIN supplier_type st ON st.type_id = s.supplier_type_id
                    ORDER BY s.created_at DESC, s.supplier_name ASC
                    """,
                    this::mapDetailsRow
            );
        }
    }

    public Optional<SupplierDetailsResponse> findById(UUID id) {
        try {
            return jdbcTemplate.query(
                    """
                    SELECT supplier_id, supplier_code, supplier_name, contact_name, contact_person,
                           phone, email, address, tax_id, supplier_type_id, supplier_type_name, is_active, created_at
                    FROM supplier_details_v
                    WHERE supplier_id = ?
                    """,
                    this::mapDetailsRow,
                    id
            ).stream().findFirst();
        } catch (BadSqlGrammarException ex) {
            return jdbcTemplate.query(
                    """
                    SELECT s.supplier_id, s.supplier_code, s.supplier_name,
                           NULL::varchar AS contact_name, s.contact_person,
                           s.phone, s.email, s.address, s.tax_id, s.supplier_type_id,
                           st.type_name AS supplier_type_name, s.is_active, s.created_at
                    FROM supplier s
                    LEFT JOIN supplier_type st ON st.type_id = s.supplier_type_id
                    WHERE s.supplier_id = ?
                    """,
                    this::mapDetailsRow,
                    id
            ).stream().findFirst();
        }
    }

    public SupplierDetailsResponse create(SupplierUpsertRequest r) {
        String sql = """
                INSERT INTO supplier (
                    company_id, company_code, supplier_code, supplier_name, contact_name, contact_person, phone, email, address, tax_id, supplier_type_id, is_active
                )
                VALUES (
                    COALESCE(
                        ?,
                        (SELECT c.company_id FROM company c WHERE LOWER(c.company_code) = LOWER(?) LIMIT 1),
                        (SELECT c.company_id FROM company c WHERE c.is_active = TRUE ORDER BY c.created_at ASC LIMIT 1)
                    ),
                    COALESCE(
                        ?,
                        (SELECT c.company_code FROM company c WHERE c.company_id = ?),
                        (SELECT c.company_code FROM company c WHERE LOWER(c.company_code) = LOWER(?) LIMIT 1),
                        (SELECT c.company_code FROM company c WHERE c.is_active = TRUE ORDER BY c.created_at ASC LIMIT 1)
                    ),
                    CASE
                        WHEN to_regclass('public.supplier_code_seq') IS NOT NULL THEN
                            'SUP' || LPAD(nextval('public.supplier_code_seq')::text, 6, '0')
                        ELSE
                            'SUP' || SUBSTRING(REPLACE(gen_random_uuid()::text, '-', '') FROM 1 FOR 10)
                    END,
                    ?, ?, ?, ?, ?, ?, ?, ?, COALESCE(?, TRUE)
                )
                RETURNING supplier_id, supplier_code, supplier_name, contact_name, contact_person, phone, email, address, tax_id, supplier_type_id,
                          (SELECT st.type_name FROM supplier_type st WHERE st.type_id = supplier_type_id) AS supplier_type_name,
                          is_active, created_at
                """;
        return jdbcTemplate.queryForObject(
                sql,
                this::mapDetailsRow,
                r.companyId(),
                r.companyCode(),
                r.companyCode(),
                r.companyId(),
                r.companyCode(),
                r.supplierName(),
                r.contactName(),
                r.contactPerson(),
                r.phone(),
                r.email(),
                r.address(),
                r.taxId(),
                r.supplierTypeId(),
                r.isActive()
        );
    }

    public Optional<SupplierDetailsResponse> update(UUID id, SupplierUpsertRequest r) {
        String sql = """
                UPDATE supplier
                SET company_id = COALESCE(?, company_id),
                    company_code = COALESCE(?, (SELECT c.company_code FROM company c WHERE c.company_id = COALESCE(?, company_id)), company_code),
                    supplier_name = COALESCE(?, supplier_name),
                    contact_name = COALESCE(?, contact_name),
                    contact_person = COALESCE(?, contact_person),
                    phone = COALESCE(?, phone),
                    email = COALESCE(?, email),
                    address = COALESCE(?, address),
                    tax_id = COALESCE(?, tax_id),
                    supplier_type_id = COALESCE(?, supplier_type_id),
                    is_active = COALESCE(?, is_active)
                WHERE supplier_id = ?
                RETURNING supplier_id, supplier_code, supplier_name, contact_name, contact_person, phone, email, address, tax_id, supplier_type_id,
                          (SELECT st.type_name FROM supplier_type st WHERE st.type_id = supplier_type_id) AS supplier_type_name,
                          is_active, created_at
                """;
        return jdbcTemplate.query(
                sql,
                this::mapDetailsRow,
                r.companyId(),
                r.companyCode(),
                r.companyId(),
                r.supplierName(),
                r.contactName(),
                r.contactPerson(),
                r.phone(),
                r.email(),
                r.address(),
                r.taxId(),
                r.supplierTypeId(),
                r.isActive(),
                id
        ).stream().findFirst();
    }

    public boolean delete(UUID id) {
        return jdbcTemplate.update("DELETE FROM supplier WHERE supplier_id = ?", id) > 0;
    }

    private SupplierDetailsResponse mapDetailsRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        return new SupplierDetailsResponse(
                rs.getObject("supplier_id", java.util.UUID.class),
                rs.getString("supplier_code"),
                rs.getString("supplier_name"),
                rs.getString("contact_name"),
                rs.getString("contact_person"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("address"),
                rs.getString("tax_id"),
                rs.getObject("supplier_type_id", Integer.class),
                rs.getString("supplier_type_name"),
                rs.getObject("is_active", Boolean.class),
                createdAtTs == null ? null : createdAtTs.toInstant()
        );
    }
}
