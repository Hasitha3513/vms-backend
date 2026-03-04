package genxsolution.vms.vmsbackend.employee_hr_management.overtime_request.repository;

import genxsolution.vms.vmsbackend.employee_hr_management.overtime_request.dto.OvertimeRequestUpsertRequest;
import genxsolution.vms.vmsbackend.employee_hr_management.overtime_request.model.OvertimeRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class OvertimeRequestRepository {
    private final JdbcTemplate jdbcTemplate;
    public OvertimeRequestRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }
    public List<OvertimeRequest> findAll(UUID companyId) {
        String sql = "SELECT overtime_id, company_id, company_code, employee_id, project_id, ot_date, hours, ot_type_id, approved, approved_by, approved_at, created_at FROM overtime_request " + (companyId == null ? "" : "WHERE company_id = ? ") + "ORDER BY ot_date DESC, created_at DESC";
        return companyId == null ? jdbcTemplate.query(sql, this::mapRow) : jdbcTemplate.query(sql, this::mapRow, companyId);
    }
    public Optional<OvertimeRequest> findById(UUID id) {
        return jdbcTemplate.query("SELECT overtime_id, company_id, company_code, employee_id, project_id, ot_date, hours, ot_type_id, approved, approved_by, approved_at, created_at FROM overtime_request WHERE overtime_id = ?", this::mapRow, id).stream().findFirst();
    }
    public OvertimeRequest create(OvertimeRequestUpsertRequest r) {
        String sql = """
                INSERT INTO overtime_request (company_id, company_code, employee_id, project_id, ot_date, hours, ot_type_id, approved, approved_by, approved_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING overtime_id, company_id, company_code, employee_id, project_id, ot_date, hours, ot_type_id, approved, approved_by, approved_at, created_at
                """;
        return jdbcTemplate.queryForObject(sql, this::mapRow, r.companyId(), r.companyCode(), r.employeeId(), r.projectId(), r.otDate(), r.hours(), r.otTypeId(), r.approved(), r.approvedBy(), r.approvedAt());
    }
    public Optional<OvertimeRequest> update(UUID id, OvertimeRequestUpsertRequest r) {
        String sql = """
                UPDATE overtime_request SET employee_id = COALESCE(?, employee_id), project_id = COALESCE(?, project_id), ot_date = COALESCE(?, ot_date), hours = COALESCE(?, hours), ot_type_id = COALESCE(?, ot_type_id), approved = COALESCE(?, approved), approved_by = COALESCE(?, approved_by), approved_at = COALESCE(?, approved_at)
                WHERE overtime_id = ?
                RETURNING overtime_id, company_id, company_code, employee_id, project_id, ot_date, hours, ot_type_id, approved, approved_by, approved_at, created_at
                """;
        return jdbcTemplate.query(sql, this::mapRow, r.employeeId(), r.projectId(), r.otDate(), r.hours(), r.otTypeId(), r.approved(), r.approvedBy(), r.approvedAt(), id).stream().findFirst();
    }
    public boolean delete(UUID id) { return jdbcTemplate.update("DELETE FROM overtime_request WHERE overtime_id = ?", id) > 0; }
    private OvertimeRequest mapRow(ResultSet rs, int n) throws SQLException {
        return new OvertimeRequest(rs.getObject("overtime_id", UUID.class), rs.getObject("company_id", UUID.class), rs.getString("company_code"), rs.getObject("employee_id", UUID.class), rs.getObject("project_id", UUID.class), rs.getDate("ot_date").toLocalDate(), rs.getBigDecimal("hours"), rs.getObject("ot_type_id", Integer.class), rs.getObject("approved", Boolean.class), rs.getObject("approved_by", UUID.class), toInstant(rs.getTimestamp("approved_at")), toInstant(rs.getTimestamp("created_at")));
    }
    private java.time.Instant toInstant(Timestamp ts) { return ts == null ? null : ts.toInstant(); }
}
