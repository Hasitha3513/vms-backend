package genxsolution.vms.vmsbackend.employee_hr_management.leave_type.repository;

import genxsolution.vms.vmsbackend.employee_hr_management.leave_type.dto.LeaveTypeUpsertRequest;
import genxsolution.vms.vmsbackend.employee_hr_management.leave_type.model.LeaveType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class LeaveTypeRepository {
    private final JdbcTemplate jdbcTemplate;
    public LeaveTypeRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }
    public List<LeaveType> findAll(UUID companyId) {
        String sql = "SELECT leave_type_id, company_id, company_code, leave_code, leave_name, days_per_year FROM leave_type " + (companyId == null ? "" : "WHERE company_id = ? ") + "ORDER BY leave_name";
        return companyId == null ? jdbcTemplate.query(sql, this::mapRow) : jdbcTemplate.query(sql, this::mapRow, companyId);
    }
    public Optional<LeaveType> findById(UUID id) {
        return jdbcTemplate.query("SELECT leave_type_id, company_id, company_code, leave_code, leave_name, days_per_year FROM leave_type WHERE leave_type_id = ?", this::mapRow, id).stream().findFirst();
    }
    public LeaveType create(LeaveTypeUpsertRequest r) {
        String sql = """
                INSERT INTO leave_type (company_id, company_code, leave_code, leave_name, days_per_year)
                VALUES (?, ?, ?, ?, ?)
                RETURNING leave_type_id, company_id, company_code, leave_code, leave_name, days_per_year
                """;
        return jdbcTemplate.queryForObject(sql, this::mapRow, r.companyId(), r.companyCode(), r.leaveCode(), r.leaveName(), r.daysPerYear());
    }
    public Optional<LeaveType> update(UUID id, LeaveTypeUpsertRequest r) {
        String sql = """
                UPDATE leave_type SET leave_code = COALESCE(?, leave_code), leave_name = COALESCE(?, leave_name), days_per_year = COALESCE(?, days_per_year)
                WHERE leave_type_id = ?
                RETURNING leave_type_id, company_id, company_code, leave_code, leave_name, days_per_year
                """;
        return jdbcTemplate.query(sql, this::mapRow, r.leaveCode(), r.leaveName(), r.daysPerYear(), id).stream().findFirst();
    }
    public boolean delete(UUID id) { return jdbcTemplate.update("DELETE FROM leave_type WHERE leave_type_id = ?", id) > 0; }
    private LeaveType mapRow(ResultSet rs, int n) throws SQLException {
        return new LeaveType(rs.getObject("leave_type_id", UUID.class), rs.getObject("company_id", UUID.class), rs.getString("company_code"), rs.getString("leave_code"), rs.getString("leave_name"), rs.getBigDecimal("days_per_year"));
    }
}
