package genxsolution.vms.vmsbackend.employee_hr_management.leave_application.repository;

import genxsolution.vms.vmsbackend.employee_hr_management.leave_application.dto.LeaveApplicationUpsertRequest;
import genxsolution.vms.vmsbackend.employee_hr_management.leave_application.model.LeaveApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class LeaveApplicationRepository {
    private final JdbcTemplate jdbcTemplate;
    public LeaveApplicationRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }
    public List<LeaveApplication> findAll(UUID companyId) {
        String sql = "SELECT leave_id, company_id, company_code, employee_id, leave_type_id, start_date, end_date, total_days, status_id, applied_at, approved_by, approved_at, updated_at FROM leave_application " + (companyId == null ? "" : "WHERE company_id = ? ") + "ORDER BY start_date DESC, applied_at DESC";
        return companyId == null ? jdbcTemplate.query(sql, this::mapRow) : jdbcTemplate.query(sql, this::mapRow, companyId);
    }
    public Optional<LeaveApplication> findById(UUID id) {
        return jdbcTemplate.query("SELECT leave_id, company_id, company_code, employee_id, leave_type_id, start_date, end_date, total_days, status_id, applied_at, approved_by, approved_at, updated_at FROM leave_application WHERE leave_id = ?", this::mapRow, id).stream().findFirst();
    }
    public LeaveApplication create(LeaveApplicationUpsertRequest r) {
        String sql = """
                INSERT INTO leave_application (company_id, company_code, employee_id, leave_type_id, start_date, end_date, total_days, status_id, approved_by, approved_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING leave_id, company_id, company_code, employee_id, leave_type_id, start_date, end_date, total_days, status_id, applied_at, approved_by, approved_at, updated_at
                """;
        return jdbcTemplate.queryForObject(sql, this::mapRow, r.companyId(), r.companyCode(), r.employeeId(), r.leaveTypeId(), r.startDate(), r.endDate(), r.totalDays(), r.statusId(), r.approvedBy(), r.approvedAt());
    }
    public Optional<LeaveApplication> update(UUID id, LeaveApplicationUpsertRequest r) {
        String sql = """
                UPDATE leave_application SET employee_id = COALESCE(?, employee_id), leave_type_id = COALESCE(?, leave_type_id), start_date = COALESCE(?, start_date), end_date = COALESCE(?, end_date), total_days = COALESCE(?, total_days), status_id = COALESCE(?, status_id), approved_by = COALESCE(?, approved_by), approved_at = COALESCE(?, approved_at), updated_at = CURRENT_TIMESTAMP
                WHERE leave_id = ?
                RETURNING leave_id, company_id, company_code, employee_id, leave_type_id, start_date, end_date, total_days, status_id, applied_at, approved_by, approved_at, updated_at
                """;
        return jdbcTemplate.query(sql, this::mapRow, r.employeeId(), r.leaveTypeId(), r.startDate(), r.endDate(), r.totalDays(), r.statusId(), r.approvedBy(), r.approvedAt(), id).stream().findFirst();
    }
    public boolean delete(UUID id) { return jdbcTemplate.update("DELETE FROM leave_application WHERE leave_id = ?", id) > 0; }
    private LeaveApplication mapRow(ResultSet rs, int n) throws SQLException {
        return new LeaveApplication(rs.getObject("leave_id", UUID.class), rs.getObject("company_id", UUID.class), rs.getString("company_code"), rs.getObject("employee_id", UUID.class), rs.getObject("leave_type_id", UUID.class), rs.getDate("start_date").toLocalDate(), rs.getDate("end_date").toLocalDate(), rs.getBigDecimal("total_days"), rs.getObject("status_id", Integer.class), toInstant(rs.getTimestamp("applied_at")), rs.getObject("approved_by", UUID.class), toInstant(rs.getTimestamp("approved_at")), toInstant(rs.getTimestamp("updated_at")));
    }
    private java.time.Instant toInstant(Timestamp ts) { return ts == null ? null : ts.toInstant(); }
}
