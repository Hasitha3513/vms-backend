package genxsolution.vms.vmsbackend.employee_hr_management.employee.repository;

import genxsolution.vms.vmsbackend.employee_hr_management.employee.dto.EmployeeUpsertRequest;
import genxsolution.vms.vmsbackend.employee_hr_management.employee.model.Employee;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EmployeeRepository {

    private final JdbcTemplate jdbcTemplate;

    public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Employee> findAll(UUID companyId) {
        String base = """
                SELECT employee_id, company_id, company_code, branch_id, department_id, grade_id, position_id, manager_id,
                       employee_code, first_name, last_name, date_of_birth, gender_id, national_id,
                       mobile_phone, work_email, current_address, hire_date, employment_type_id, job_title,
                       is_driver, is_operator, is_technician, employment_status_id, created_at, updated_at
                FROM employee
                """;
        String sql = companyId == null ? base + "ORDER BY first_name, last_name" : base + "WHERE company_id = ? ORDER BY first_name, last_name";
        return companyId == null ? jdbcTemplate.query(sql, this::mapRow) : jdbcTemplate.query(sql, this::mapRow, companyId);
    }

    public Optional<Employee> findById(UUID id) {
        String sql = """
                SELECT employee_id, company_id, company_code, branch_id, department_id, grade_id, position_id, manager_id,
                       employee_code, first_name, last_name, date_of_birth, gender_id, national_id,
                       mobile_phone, work_email, current_address, hire_date, employment_type_id, job_title,
                       is_driver, is_operator, is_technician, employment_status_id, created_at, updated_at
                FROM employee WHERE employee_id = ?
                """;
        return jdbcTemplate.query(sql, this::mapRow, id).stream().findFirst();
    }

    public boolean existsByWorkEmail(String workEmail) {
        String sql = """
                SELECT 1
                FROM employee
                WHERE LOWER(TRIM(COALESCE(work_email, ''))) = LOWER(TRIM(COALESCE(?, '')))
                LIMIT 1
                """;
        return !jdbcTemplate.query(sql, (rs, rowNum) -> 1, workEmail).isEmpty();
    }

    public boolean existsByWorkEmailExcludingEmployeeId(String workEmail, UUID employeeId) {
        String sql = """
                SELECT 1
                FROM employee
                WHERE LOWER(TRIM(COALESCE(work_email, ''))) = LOWER(TRIM(COALESCE(?, '')))
                  AND employee_id <> ?
                LIMIT 1
                """;
        return !jdbcTemplate.query(sql, (rs, rowNum) -> 1, workEmail, employeeId).isEmpty();
    }

    public Employee create(EmployeeUpsertRequest r) {
        String sql = """
                INSERT INTO employee (
                    company_id, company_code, branch_id, department_id, grade_id, position_id, manager_id,
                    employee_code, first_name, last_name, date_of_birth, gender_id, national_id,
                    mobile_phone, work_email, current_address, hire_date, employment_type_id, job_title,
                    is_driver, is_operator, is_technician, employment_status_id
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                          COALESCE(?, FALSE), COALESCE(?, FALSE), COALESCE(?, FALSE), ?)
                RETURNING employee_id, company_id, company_code, branch_id, department_id, grade_id, position_id, manager_id,
                       employee_code, first_name, last_name, date_of_birth, gender_id, national_id,
                       mobile_phone, work_email, current_address, hire_date, employment_type_id, job_title,
                       is_driver, is_operator, is_technician, employment_status_id, created_at, updated_at
                """;
        return jdbcTemplate.queryForObject(sql, this::mapRow,
                r.companyId(), r.companyCode(), r.branchId(), r.departmentId(), r.gradeId(), r.positionId(), r.managerId(),
                r.employeeCode(), r.firstName(), r.lastName(), r.dateOfBirth(), r.genderId(), r.nationalId(),
                r.mobilePhone(), r.workEmail(), r.currentAddress(), r.hireDate(), r.employmentTypeId(), r.jobTitle(),
                r.isDriver(), r.isOperator(), r.isTechnician(), r.employmentStatusId());
    }

    public Optional<Employee> update(UUID id, EmployeeUpsertRequest r) {
        String sql = """
                UPDATE employee SET
                    branch_id = COALESCE(?, branch_id),
                    department_id = COALESCE(?, department_id),
                    grade_id = COALESCE(?, grade_id),
                    position_id = COALESCE(?, position_id),
                    manager_id = COALESCE(?, manager_id),
                    first_name = COALESCE(?, first_name),
                    last_name = COALESCE(?, last_name),
                    date_of_birth = COALESCE(?, date_of_birth),
                    gender_id = COALESCE(?, gender_id),
                    national_id = COALESCE(?, national_id),
                    mobile_phone = COALESCE(?, mobile_phone),
                    work_email = COALESCE(?, work_email),
                    current_address = COALESCE(?, current_address),
                    hire_date = COALESCE(?, hire_date),
                    employment_type_id = COALESCE(?, employment_type_id),
                    job_title = COALESCE(?, job_title),
                    is_driver = COALESCE(?, is_driver),
                    is_operator = COALESCE(?, is_operator),
                    is_technician = COALESCE(?, is_technician),
                    employment_status_id = COALESCE(?, employment_status_id)
                WHERE employee_id = ?
                RETURNING employee_id, company_id, company_code, branch_id, department_id, grade_id, position_id, manager_id,
                       employee_code, first_name, last_name, date_of_birth, gender_id, national_id,
                       mobile_phone, work_email, current_address, hire_date, employment_type_id, job_title,
                       is_driver, is_operator, is_technician, employment_status_id, created_at, updated_at
                """;
        return jdbcTemplate.query(sql, this::mapRow,
                r.branchId(), r.departmentId(), r.gradeId(), r.positionId(), r.managerId(), r.firstName(), r.lastName(),
                r.dateOfBirth(), r.genderId(), r.nationalId(), r.mobilePhone(), r.workEmail(), r.currentAddress(),
                r.hireDate(), r.employmentTypeId(), r.jobTitle(), r.isDriver(), r.isOperator(), r.isTechnician(),
                r.employmentStatusId(), id).stream().findFirst();
    }

    public boolean delete(UUID id) {
        return jdbcTemplate.update("DELETE FROM employee WHERE employee_id = ?", id) > 0;
    }

    private Employee mapRow(ResultSet rs, int n) throws SQLException {
        var dateOfBirth = rs.getDate("date_of_birth");
        var hireDate = rs.getDate("hire_date");
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        return new Employee(
                rs.getObject("employee_id", UUID.class),
                rs.getObject("company_id", UUID.class),
                rs.getString("company_code"),
                rs.getObject("branch_id", UUID.class),
                rs.getObject("department_id", UUID.class),
                rs.getObject("grade_id", UUID.class),
                rs.getObject("position_id", UUID.class),
                rs.getObject("manager_id", UUID.class),
                rs.getString("employee_code"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                dateOfBirth == null ? null : dateOfBirth.toLocalDate(),
                rs.getObject("gender_id", Integer.class),
                rs.getString("national_id"),
                rs.getString("mobile_phone"),
                rs.getString("work_email"),
                rs.getString("current_address"),
                hireDate == null ? null : hireDate.toLocalDate(),
                rs.getObject("employment_type_id", Integer.class),
                rs.getString("job_title"),
                rs.getObject("is_driver", Boolean.class),
                rs.getObject("is_operator", Boolean.class),
                rs.getObject("is_technician", Boolean.class),
                rs.getObject("employment_status_id", Integer.class),
                createdAt == null ? null : createdAt.toInstant(),
                updatedAt == null ? null : updatedAt.toInstant()
        );
    }
}







