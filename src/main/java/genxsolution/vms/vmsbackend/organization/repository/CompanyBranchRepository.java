package genxsolution.vms.vmsbackend.organization.repository;

import genxsolution.vms.vmsbackend.organization.dto.companybranch.CreateCompanyBranchRequest;
import genxsolution.vms.vmsbackend.organization.dto.companybranch.UpdateCompanyBranchRequest;
import genxsolution.vms.vmsbackend.organization.model.CompanyBranch;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CompanyBranchRepository {

    private final JdbcTemplate jdbcTemplate;

    public CompanyBranchRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CompanyBranch> findAll(boolean activeOnly) {
        String sql = """
                SELECT branch_id, company_id, company_code, branch_code, branch_name, address, city,
                       state_province, country, latitude, longitude, is_main_workshop, is_active,
                       created_at, updated_at
                FROM company_branch
                """ + (activeOnly ? "WHERE is_active = TRUE " : "") + "ORDER BY branch_name";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    public List<CompanyBranch> findAllByCompany(UUID companyId, boolean activeOnly) {
        String sql = """
                SELECT branch_id, company_id, company_code, branch_code, branch_name, address, city,
                       state_province, country, latitude, longitude, is_main_workshop, is_active,
                       created_at, updated_at
                FROM company_branch
                WHERE company_id = ?
                """ + (activeOnly ? "AND is_active = TRUE " : "") + "ORDER BY branch_name";
        return jdbcTemplate.query(sql, this::mapRow, companyId);
    }

    public Optional<CompanyBranch> findById(UUID branchId) {
        String sql = """
                SELECT branch_id, company_id, company_code, branch_code, branch_name, address, city,
                       state_province, country, latitude, longitude, is_main_workshop, is_active,
                       created_at, updated_at
                FROM company_branch
                WHERE branch_id = ?
                """;
        return jdbcTemplate.query(sql, this::mapRow, branchId).stream().findFirst();
    }

    public CompanyBranch create(CreateCompanyBranchRequest request) {
        String sql = """
                INSERT INTO company_branch (
                    company_id, company_code, branch_code, branch_name, address, city,
                    state_province, country, latitude, longitude, is_main_workshop, is_active
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, COALESCE(?, FALSE), COALESCE(?, TRUE))
                RETURNING branch_id, company_id, company_code, branch_code, branch_name, address, city,
                          state_province, country, latitude, longitude, is_main_workshop, is_active,
                          created_at, updated_at
                """;

        return jdbcTemplate.queryForObject(
                sql,
                this::mapRow,
                request.companyId(),
                request.companyCode(),
                request.branchCode(),
                request.branchName(),
                request.address(),
                request.city(),
                request.stateProvince(),
                request.country(),
                request.latitude(),
                request.longitude(),
                request.isMainWorkshop(),
                request.isActive()
        );
    }

    public Optional<CompanyBranch> update(UUID branchId, UpdateCompanyBranchRequest request) {
        String sql = """
                UPDATE company_branch
                SET branch_name = COALESCE(?, branch_name),
                    address = COALESCE(?, address),
                    city = COALESCE(?, city),
                    state_province = COALESCE(?, state_province),
                    country = COALESCE(?, country),
                    latitude = COALESCE(?, latitude),
                    longitude = COALESCE(?, longitude),
                    is_main_workshop = COALESCE(?, is_main_workshop),
                    is_active = COALESCE(?, is_active)
                WHERE branch_id = ?
                RETURNING branch_id, company_id, company_code, branch_code, branch_name, address, city,
                          state_province, country, latitude, longitude, is_main_workshop, is_active,
                          created_at, updated_at
                """;

        return jdbcTemplate.query(
                sql,
                this::mapRow,
                request.branchName(),
                request.address(),
                request.city(),
                request.stateProvince(),
                request.country(),
                request.latitude(),
                request.longitude(),
                request.isMainWorkshop(),
                request.isActive(),
                branchId
        ).stream().findFirst();
    }

    public boolean delete(UUID branchId) {
        String sql = "DELETE FROM company_branch WHERE branch_id = ?";
        return jdbcTemplate.update(sql, branchId) > 0;
    }

    private CompanyBranch mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CompanyBranch(
                rs.getObject("branch_id", UUID.class),
                rs.getObject("company_id", UUID.class),
                rs.getString("company_code"),
                rs.getString("branch_code"),
                rs.getString("branch_name"),
                rs.getString("address"),
                rs.getString("city"),
                rs.getString("state_province"),
                rs.getString("country"),
                rs.getBigDecimal("latitude"),
                rs.getBigDecimal("longitude"),
                rs.getObject("is_main_workshop", Boolean.class),
                rs.getObject("is_active", Boolean.class),
                rs.getTimestamp("created_at").toInstant(),
                rs.getTimestamp("updated_at").toInstant()
        );
    }
}






