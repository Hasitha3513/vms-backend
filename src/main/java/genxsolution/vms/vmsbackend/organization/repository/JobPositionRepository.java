package genxsolution.vms.vmsbackend.organization.repository;

import genxsolution.vms.vmsbackend.organization.dto.jobposition.CreateJobPositionRequest;
import genxsolution.vms.vmsbackend.organization.dto.jobposition.UpdateJobPositionRequest;
import genxsolution.vms.vmsbackend.organization.model.JobPosition;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JobPositionRepository {

    private final JdbcTemplate jdbcTemplate;

    public JobPositionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<JobPosition> findAll(boolean activeOnly) {
        String sql = """
                SELECT position_id, company_id, company_code, position_code, position_name,
                       description, is_active, created_at
                FROM job_position
                """ + (activeOnly ? "WHERE is_active = TRUE " : "") + "ORDER BY position_name";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    public List<JobPosition> findAllByCompany(UUID companyId, boolean activeOnly) {
        String sql = """
                SELECT position_id, company_id, company_code, position_code, position_name,
                       description, is_active, created_at
                FROM job_position
                WHERE company_id = ?
                """ + (activeOnly ? "AND is_active = TRUE " : "") + "ORDER BY position_name";
        return jdbcTemplate.query(sql, this::mapRow, companyId);
    }

    public Optional<JobPosition> findById(UUID positionId) {
        String sql = """
                SELECT position_id, company_id, company_code, position_code, position_name,
                       description, is_active, created_at
                FROM job_position
                WHERE position_id = ?
                """;
        return jdbcTemplate.query(sql, this::mapRow, positionId).stream().findFirst();
    }

    public JobPosition create(CreateJobPositionRequest request) {
        String sql = """
                INSERT INTO job_position (
                    company_id, company_code, position_code, position_name, description, is_active
                )
                VALUES (?, ?, ?, ?, ?, COALESCE(?, TRUE))
                RETURNING position_id, company_id, company_code, position_code, position_name,
                          description, is_active, created_at
                """;

        return jdbcTemplate.queryForObject(
                sql,
                this::mapRow,
                request.companyId(),
                request.companyCode(),
                request.positionCode(),
                request.positionName(),
                request.description(),
                request.isActive()
        );
    }

    public Optional<JobPosition> update(UUID positionId, UpdateJobPositionRequest request) {
        String sql = """
                UPDATE job_position
                SET position_name = COALESCE(?, position_name),
                    description = COALESCE(?, description),
                    is_active = COALESCE(?, is_active)
                WHERE position_id = ?
                RETURNING position_id, company_id, company_code, position_code, position_name,
                          description, is_active, created_at
                """;

        return jdbcTemplate.query(
                sql,
                this::mapRow,
                request.positionName(),
                request.description(),
                request.isActive(),
                positionId
        ).stream().findFirst();
    }

    public boolean delete(UUID positionId) {
        String sql = "DELETE FROM job_position WHERE position_id = ?";
        return jdbcTemplate.update(sql, positionId) > 0;
    }

    private JobPosition mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new JobPosition(
                rs.getObject("position_id", UUID.class),
                rs.getObject("company_id", UUID.class),
                rs.getString("company_code"),
                rs.getString("position_code"),
                rs.getString("position_name"),
                rs.getString("description"),
                rs.getObject("is_active", Boolean.class),
                rs.getTimestamp("created_at").toInstant()
        );
    }
}






