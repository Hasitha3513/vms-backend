package genxsolution.vms.vmsbackend.organization.repository;

import genxsolution.vms.vmsbackend.organization.dto.company.CreateCompanyRequest;
import genxsolution.vms.vmsbackend.organization.dto.company.UpdateCompanyRequest;
import genxsolution.vms.vmsbackend.organization.model.Company;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CompanyRepository {

    private final JdbcTemplate jdbcTemplate;

    public CompanyRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Company> findAll(boolean activeOnly) {
        String sql = """
                SELECT company_id, company_code, company_name, company_type_id, registration_no, tax_id,
                       email, phone_primary, address, timezone, currency, is_active, created_at, updated_at
                FROM company
                """ + (activeOnly ? "WHERE is_active = TRUE " : "") + "ORDER BY company_name";

        return jdbcTemplate.query(sql, this::mapRow);
    }

    public Optional<Company> findById(UUID companyId) {
        String sql = """
                SELECT company_id, company_code, company_name, company_type_id, registration_no, tax_id,
                       email, phone_primary, address, timezone, currency, is_active, created_at, updated_at
                FROM company
                WHERE company_id = ?
                """;
        return jdbcTemplate.query(sql, this::mapRow, companyId).stream().findFirst();
    }

    public Optional<Company> findByCode(String companyCode) {
        String sql = """
                SELECT company_id, company_code, company_name, company_type_id, registration_no, tax_id,
                       email, phone_primary, address, timezone, currency, is_active, created_at, updated_at
                FROM company
                WHERE company_code = ?
                """;
        return jdbcTemplate.query(sql, this::mapRow, companyCode).stream().findFirst();
    }

    public Company create(CreateCompanyRequest request) {
        String sql = """
                INSERT INTO company (
                    company_code, company_name, company_type_id, registration_no, tax_id,
                    email, phone_primary, address, timezone, currency, is_active
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, COALESCE(?, 'Asia/Colombo'), COALESCE(?, 'LKR'), COALESCE(?, TRUE))
                RETURNING company_id, company_code, company_name, company_type_id, registration_no, tax_id,
                          email, phone_primary, address, timezone, currency, is_active, created_at, updated_at
                """;

        return jdbcTemplate.queryForObject(
                sql,
                this::mapRow,
                request.companyCode(),
                request.companyName(),
                request.companyTypeId(),
                request.registrationNo(),
                request.taxId(),
                request.email(),
                request.phonePrimary(),
                request.address(),
                request.timezone(),
                request.currency(),
                request.isActive()
        );
    }

    public Optional<Company> update(UUID companyId, UpdateCompanyRequest request) {
        String sql = """
                UPDATE company
                SET company_name = COALESCE(?, company_name),
                    company_type_id = COALESCE(?, company_type_id),
                    registration_no = COALESCE(?, registration_no),
                    tax_id = COALESCE(?, tax_id),
                    email = COALESCE(?, email),
                    phone_primary = COALESCE(?, phone_primary),
                    address = COALESCE(?, address),
                    timezone = COALESCE(?, timezone),
                    currency = COALESCE(?, currency),
                    is_active = COALESCE(?, is_active)
                WHERE company_id = ?
                RETURNING company_id, company_code, company_name, company_type_id, registration_no, tax_id,
                          email, phone_primary, address, timezone, currency, is_active, created_at, updated_at
                """;

        return jdbcTemplate.query(
                sql,
                this::mapRow,
                request.companyName(),
                request.companyTypeId(),
                request.registrationNo(),
                request.taxId(),
                request.email(),
                request.phonePrimary(),
                request.address(),
                request.timezone(),
                request.currency(),
                request.isActive(),
                companyId
        ).stream().findFirst();
    }

    public boolean delete(UUID companyId) {
        String sql = "DELETE FROM company WHERE company_id = ?";
        return jdbcTemplate.update(sql, companyId) > 0;
    }

    private Company mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Company(
                rs.getObject("company_id", UUID.class),
                rs.getString("company_code"),
                rs.getString("company_name"),
                rs.getObject("company_type_id", Integer.class),
                rs.getString("registration_no"),
                rs.getString("tax_id"),
                rs.getString("email"),
                rs.getString("phone_primary"),
                rs.getString("address"),
                rs.getString("timezone"),
                rs.getString("currency"),
                rs.getObject("is_active", Boolean.class),
                rs.getTimestamp("created_at").toInstant(),
                rs.getTimestamp("updated_at").toInstant()
        );
    }
}






