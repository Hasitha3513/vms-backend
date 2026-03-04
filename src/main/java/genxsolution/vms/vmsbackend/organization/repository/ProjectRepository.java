package genxsolution.vms.vmsbackend.organization.repository;

import genxsolution.vms.vmsbackend.organization.dto.project.CreateProjectRequest;
import genxsolution.vms.vmsbackend.organization.dto.project.UpdateProjectRequest;
import genxsolution.vms.vmsbackend.organization.model.Project;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProjectRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProjectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Project> findAll() {
        String sql = """
                SELECT project_id, company_id, company_code, branch_id, project_code, project_name,
                       project_type_id, site_address, site_latitude, site_longitude, start_date,
                       planned_end_date, actual_end_date, budget_amount, actual_cost,
                       project_manager, status_id, created_at, updated_at
                FROM project
                ORDER BY project_name
                """;
        return jdbcTemplate.query(sql, this::mapRow);
    }

    public List<Project> findAllByCompany(UUID companyId) {
        String sql = """
                SELECT project_id, company_id, company_code, branch_id, project_code, project_name,
                       project_type_id, site_address, site_latitude, site_longitude, start_date,
                       planned_end_date, actual_end_date, budget_amount, actual_cost,
                       project_manager, status_id, created_at, updated_at
                FROM project
                WHERE company_id = ?
                ORDER BY project_name
                """;
        return jdbcTemplate.query(sql, this::mapRow, companyId);
    }

    public Optional<Project> findById(UUID projectId) {
        String sql = """
                SELECT project_id, company_id, company_code, branch_id, project_code, project_name,
                       project_type_id, site_address, site_latitude, site_longitude, start_date,
                       planned_end_date, actual_end_date, budget_amount, actual_cost,
                       project_manager, status_id, created_at, updated_at
                FROM project
                WHERE project_id = ?
                """;
        return jdbcTemplate.query(sql, this::mapRow, projectId).stream().findFirst();
    }

    public Project create(CreateProjectRequest request) {
        String sql = """
                INSERT INTO project (
                    company_id, company_code, branch_id, project_code, project_name,
                    project_type_id, site_address, site_latitude, site_longitude, start_date,
                    planned_end_date, actual_end_date, budget_amount, actual_cost,
                    project_manager, status_id
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING project_id, company_id, company_code, branch_id, project_code, project_name,
                          project_type_id, site_address, site_latitude, site_longitude, start_date,
                          planned_end_date, actual_end_date, budget_amount, actual_cost,
                          project_manager, status_id, created_at, updated_at
                """;

        return jdbcTemplate.queryForObject(
                sql,
                this::mapRow,
                request.companyId(),
                request.companyCode(),
                request.branchId(),
                request.projectCode(),
                request.projectName(),
                request.projectTypeId(),
                request.siteAddress(),
                request.siteLatitude(),
                request.siteLongitude(),
                request.startDate(),
                request.plannedEndDate(),
                request.actualEndDate(),
                request.budgetAmount(),
                request.actualCost(),
                request.projectManager(),
                request.statusId()
        );
    }

    public Optional<Project> update(UUID projectId, UpdateProjectRequest request) {
        String sql = """
                UPDATE project
                SET branch_id = COALESCE(?, branch_id),
                    project_name = COALESCE(?, project_name),
                    project_type_id = COALESCE(?, project_type_id),
                    site_address = COALESCE(?, site_address),
                    site_latitude = COALESCE(?, site_latitude),
                    site_longitude = COALESCE(?, site_longitude),
                    start_date = COALESCE(?, start_date),
                    planned_end_date = COALESCE(?, planned_end_date),
                    actual_end_date = COALESCE(?, actual_end_date),
                    budget_amount = COALESCE(?, budget_amount),
                    actual_cost = COALESCE(?, actual_cost),
                    project_manager = COALESCE(?, project_manager),
                    status_id = COALESCE(?, status_id)
                WHERE project_id = ?
                RETURNING project_id, company_id, company_code, branch_id, project_code, project_name,
                          project_type_id, site_address, site_latitude, site_longitude, start_date,
                          planned_end_date, actual_end_date, budget_amount, actual_cost,
                          project_manager, status_id, created_at, updated_at
                """;

        return jdbcTemplate.query(
                sql,
                this::mapRow,
                request.branchId(),
                request.projectName(),
                request.projectTypeId(),
                request.siteAddress(),
                request.siteLatitude(),
                request.siteLongitude(),
                request.startDate(),
                request.plannedEndDate(),
                request.actualEndDate(),
                request.budgetAmount(),
                request.actualCost(),
                request.projectManager(),
                request.statusId(),
                projectId
        ).stream().findFirst();
    }

    public boolean delete(UUID projectId) {
        String sql = "DELETE FROM project WHERE project_id = ?";
        return jdbcTemplate.update(sql, projectId) > 0;
    }

    private Project mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Project(
                rs.getObject("project_id", UUID.class),
                rs.getObject("company_id", UUID.class),
                rs.getString("company_code"),
                rs.getObject("branch_id", UUID.class),
                rs.getString("project_code"),
                rs.getString("project_name"),
                rs.getObject("project_type_id", Integer.class),
                rs.getString("site_address"),
                rs.getBigDecimal("site_latitude"),
                rs.getBigDecimal("site_longitude"),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("planned_end_date") == null ? null : rs.getDate("planned_end_date").toLocalDate(),
                rs.getDate("actual_end_date") == null ? null : rs.getDate("actual_end_date").toLocalDate(),
                rs.getBigDecimal("budget_amount"),
                rs.getBigDecimal("actual_cost"),
                rs.getString("project_manager"),
                rs.getObject("status_id", Integer.class),
                rs.getTimestamp("created_at").toInstant(),
                rs.getTimestamp("updated_at").toInstant()
        );
    }
}






