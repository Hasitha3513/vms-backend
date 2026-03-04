package genxsolution.vms.vmsbackend.organization.repository;

import genxsolution.vms.vmsbackend.organization.dto.department.CreateDepartmentRequest;
import genxsolution.vms.vmsbackend.organization.dto.department.UpdateDepartmentRequest;
import genxsolution.vms.vmsbackend.organization.model.Department;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class DepartmentRepository {

    private final JdbcTemplate jdbcTemplate;

    public DepartmentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Department> findAll(boolean activeOnly) {
        String sql = """
                SELECT department_id, company_id, company_code, branch_id, department_code,
                       department_name, parent_department_id, is_active, created_at, updated_at
                FROM department
                """ + (activeOnly ? "WHERE is_active = TRUE " : "") + "ORDER BY department_name";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    public List<Department> findAllByCompany(UUID companyId, boolean activeOnly) {
        String sql = """
                SELECT department_id, company_id, company_code, branch_id, department_code,
                       department_name, parent_department_id, is_active, created_at, updated_at
                FROM department
                WHERE company_id = ?
                """ + (activeOnly ? "AND is_active = TRUE " : "") + "ORDER BY department_name";
        return jdbcTemplate.query(sql, this::mapRow, companyId);
    }

    public Optional<Department> findById(UUID departmentId) {
        String sql = """
                SELECT department_id, company_id, company_code, branch_id, department_code,
                       department_name, parent_department_id, is_active, created_at, updated_at
                FROM department
                WHERE department_id = ?
                """;
        return jdbcTemplate.query(sql, this::mapRow, departmentId).stream().findFirst();
    }

    public Department create(CreateDepartmentRequest request) {
        String sql = """
                INSERT INTO department (
                    company_id, company_code, branch_id, department_code,
                    department_name, parent_department_id, is_active
                )
                VALUES (?, ?, ?, ?, ?, ?, COALESCE(?, TRUE))
                RETURNING department_id, company_id, company_code, branch_id, department_code,
                          department_name, parent_department_id, is_active, created_at, updated_at
                """;

        return jdbcTemplate.queryForObject(
                sql,
                this::mapRow,
                request.companyId(),
                request.companyCode(),
                request.branchId(),
                request.departmentCode(),
                request.departmentName(),
                request.parentDepartmentId(),
                request.isActive()
        );
    }

    public Optional<Department> update(UUID departmentId, UpdateDepartmentRequest request) {
        String sql = """
                UPDATE department
                SET branch_id = COALESCE(?, branch_id),
                    department_name = COALESCE(?, department_name),
                    parent_department_id = COALESCE(?, parent_department_id),
                    is_active = COALESCE(?, is_active)
                WHERE department_id = ?
                RETURNING department_id, company_id, company_code, branch_id, department_code,
                          department_name, parent_department_id, is_active, created_at, updated_at
                """;

        return jdbcTemplate.query(
                sql,
                this::mapRow,
                request.branchId(),
                request.departmentName(),
                request.parentDepartmentId(),
                request.isActive(),
                departmentId
        ).stream().findFirst();
    }

    public boolean delete(UUID departmentId) {
        String sql = "DELETE FROM department WHERE department_id = ?";
        return jdbcTemplate.update(sql, departmentId) > 0;
    }

    private Department mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Department(
                rs.getObject("department_id", UUID.class),
                rs.getObject("company_id", UUID.class),
                rs.getString("company_code"),
                rs.getObject("branch_id", UUID.class),
                rs.getString("department_code"),
                rs.getString("department_name"),
                rs.getObject("parent_department_id", UUID.class),
                rs.getObject("is_active", Boolean.class),
                rs.getTimestamp("created_at").toInstant(),
                rs.getTimestamp("updated_at").toInstant()
        );
    }
}






