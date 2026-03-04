package genxsolution.vms.vmsbackend.employee_hr_management.employee_grade.repository;

import genxsolution.vms.vmsbackend.employee_hr_management.employee_grade.dto.EmployeeGradeUpsertRequest;
import genxsolution.vms.vmsbackend.employee_hr_management.employee_grade.model.EmployeeGrade;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EmployeeGradeRepository {

    private final JdbcTemplate jdbcTemplate;

    public EmployeeGradeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<EmployeeGrade> findAll(UUID companyId) {
        String sql = """
                SELECT grade_id, company_id, company_code, grade_code, grade_name, category_id,
                       base_salary, base_allowance, daily_allowance, overtime_rate_per_hour, notes, created_at
                FROM employee_grade
                """ + (companyId == null ? "" : "WHERE company_id = ? ") + "ORDER BY grade_name";
        return companyId == null ? jdbcTemplate.query(sql, this::mapRow) : jdbcTemplate.query(sql, this::mapRow, companyId);
    }

    public Optional<EmployeeGrade> findById(UUID id) {
        String sql = """
                SELECT grade_id, company_id, company_code, grade_code, grade_name, category_id,
                       base_salary, base_allowance, daily_allowance, overtime_rate_per_hour, notes, created_at
                FROM employee_grade WHERE grade_id = ?
                """;
        return jdbcTemplate.query(sql, this::mapRow, id).stream().findFirst();
    }

    public EmployeeGrade create(EmployeeGradeUpsertRequest r) {
        String sql = """
                INSERT INTO employee_grade (
                    company_id, company_code, grade_code, grade_name, category_id,
                    base_salary, base_allowance, daily_allowance, overtime_rate_per_hour, notes
                ) VALUES (?, ?, ?, ?, ?, ?, COALESCE(?, 0), COALESCE(?, 0), ?, ?)
                RETURNING grade_id, company_id, company_code, grade_code, grade_name, category_id,
                          base_salary, base_allowance, daily_allowance, overtime_rate_per_hour, notes, created_at
                """;
        return jdbcTemplate.queryForObject(sql, this::mapRow,
                r.companyId(), r.companyCode(), r.gradeCode(), r.gradeName(), r.categoryId(), r.baseSalary(), r.baseAllowance(),
                r.dailyAllowance(), r.overtimeRatePerHour(), r.notes());
    }

    public Optional<EmployeeGrade> update(UUID id, EmployeeGradeUpsertRequest r) {
        String sql = """
                UPDATE employee_grade SET
                    category_id = COALESCE(?, category_id),
                    grade_name = COALESCE(?, grade_name),
                    base_salary = COALESCE(?, base_salary),
                    base_allowance = COALESCE(?, base_allowance),
                    daily_allowance = COALESCE(?, daily_allowance),
                    overtime_rate_per_hour = COALESCE(?, overtime_rate_per_hour),
                    notes = COALESCE(?, notes)
                WHERE grade_id = ?
                RETURNING grade_id, company_id, company_code, grade_code, grade_name, category_id,
                          base_salary, base_allowance, daily_allowance, overtime_rate_per_hour, notes, created_at
                """;
        return jdbcTemplate.query(sql, this::mapRow,
                r.categoryId(), r.gradeName(), r.baseSalary(), r.baseAllowance(), r.dailyAllowance(), r.overtimeRatePerHour(),
                r.notes(), id).stream().findFirst();
    }

    public boolean delete(UUID id) {
        return jdbcTemplate.update("DELETE FROM employee_grade WHERE grade_id = ?", id) > 0;
    }

    private EmployeeGrade mapRow(ResultSet rs, int n) throws SQLException {
        return new EmployeeGrade(
                rs.getObject("grade_id", UUID.class),
                rs.getObject("company_id", UUID.class),
                rs.getString("company_code"),
                rs.getString("grade_code"),
                rs.getString("grade_name"),
                rs.getObject("category_id", Integer.class),
                rs.getBigDecimal("base_salary"),
                rs.getBigDecimal("base_allowance"),
                rs.getBigDecimal("daily_allowance"),
                rs.getBigDecimal("overtime_rate_per_hour"),
                rs.getString("notes"),
                rs.getTimestamp("created_at").toInstant()
        );
    }
}







