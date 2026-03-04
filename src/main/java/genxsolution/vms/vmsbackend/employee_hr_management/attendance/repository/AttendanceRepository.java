package genxsolution.vms.vmsbackend.employee_hr_management.attendance.repository;

import genxsolution.vms.vmsbackend.employee_hr_management.attendance.dto.AttendanceUpsertRequest;
import genxsolution.vms.vmsbackend.employee_hr_management.attendance.model.Attendance;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AttendanceRepository {
    private final JdbcTemplate jdbcTemplate;
    public AttendanceRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public List<Attendance> findAll(UUID companyId) {
        String sql = "SELECT attendance_id, company_id, company_code, employee_id, attendance_date, check_in_time, check_out_time, project_id, latitude_in, longitude_in, latitude_out, longitude_out, scheduled_hours, actual_hours, overtime_hours, status_id FROM attendance " + (companyId == null ? "" : "WHERE company_id = ? ") + "ORDER BY attendance_date DESC";
        return companyId == null ? jdbcTemplate.query(sql, this::mapRow) : jdbcTemplate.query(sql, this::mapRow, companyId);
    }
    public Optional<Attendance> findById(UUID id) {
        return jdbcTemplate.query("SELECT attendance_id, company_id, company_code, employee_id, attendance_date, check_in_time, check_out_time, project_id, latitude_in, longitude_in, latitude_out, longitude_out, scheduled_hours, actual_hours, overtime_hours, status_id FROM attendance WHERE attendance_id = ?", this::mapRow, id).stream().findFirst();
    }
    public Attendance create(AttendanceUpsertRequest r) {
        String sql = """
                INSERT INTO attendance (company_id, company_code, employee_id, attendance_date, check_in_time, check_out_time, project_id, latitude_in, longitude_in, latitude_out, longitude_out, scheduled_hours, actual_hours, overtime_hours, status_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING attendance_id, company_id, company_code, employee_id, attendance_date, check_in_time, check_out_time, project_id, latitude_in, longitude_in, latitude_out, longitude_out, scheduled_hours, actual_hours, overtime_hours, status_id
                """;
        return jdbcTemplate.queryForObject(sql, this::mapRow, r.companyId(), r.companyCode(), r.employeeId(), r.attendanceDate(), r.checkInTime(), r.checkOutTime(), r.projectId(), r.latitudeIn(), r.longitudeIn(), r.latitudeOut(), r.longitudeOut(), r.scheduledHours(), r.actualHours(), r.overtimeHours(), r.statusId());
    }
    public Optional<Attendance> update(UUID id, AttendanceUpsertRequest r) {
        String sql = """
                UPDATE attendance SET employee_id = COALESCE(?, employee_id), attendance_date = COALESCE(?, attendance_date), check_in_time = COALESCE(?, check_in_time), check_out_time = COALESCE(?, check_out_time), project_id = COALESCE(?, project_id), latitude_in = COALESCE(?, latitude_in), longitude_in = COALESCE(?, longitude_in), latitude_out = COALESCE(?, latitude_out), longitude_out = COALESCE(?, longitude_out), scheduled_hours = COALESCE(?, scheduled_hours), actual_hours = COALESCE(?, actual_hours), overtime_hours = COALESCE(?, overtime_hours), status_id = COALESCE(?, status_id)
                WHERE attendance_id = ?
                RETURNING attendance_id, company_id, company_code, employee_id, attendance_date, check_in_time, check_out_time, project_id, latitude_in, longitude_in, latitude_out, longitude_out, scheduled_hours, actual_hours, overtime_hours, status_id
                """;
        return jdbcTemplate.query(sql, this::mapRow, r.employeeId(), r.attendanceDate(), r.checkInTime(), r.checkOutTime(), r.projectId(), r.latitudeIn(), r.longitudeIn(), r.latitudeOut(), r.longitudeOut(), r.scheduledHours(), r.actualHours(), r.overtimeHours(), r.statusId(), id).stream().findFirst();
    }
    public boolean delete(UUID id) { return jdbcTemplate.update("DELETE FROM attendance WHERE attendance_id = ?", id) > 0; }
    private Attendance mapRow(ResultSet rs, int n) throws SQLException {
        return new Attendance(rs.getObject("attendance_id", UUID.class), rs.getObject("company_id", UUID.class), rs.getString("company_code"), rs.getObject("employee_id", UUID.class), rs.getDate("attendance_date").toLocalDate(), toInstant(rs.getTimestamp("check_in_time")), toInstant(rs.getTimestamp("check_out_time")), rs.getObject("project_id", UUID.class), rs.getBigDecimal("latitude_in"), rs.getBigDecimal("longitude_in"), rs.getBigDecimal("latitude_out"), rs.getBigDecimal("longitude_out"), rs.getBigDecimal("scheduled_hours"), rs.getBigDecimal("actual_hours"), rs.getBigDecimal("overtime_hours"), rs.getObject("status_id", Integer.class));
    }
    private java.time.Instant toInstant(Timestamp ts) { return ts == null ? null : ts.toInstant(); }
}
