package genxsolution.vms.vmsbackend.employee_hr_management.employee_training.repository;

import genxsolution.vms.vmsbackend.employee_hr_management.employee_training.dto.EmployeeTrainingUpsertRequest;
import genxsolution.vms.vmsbackend.employee_hr_management.employee_training.model.EmployeeTraining;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EmployeeTrainingRepository {
    private final JdbcTemplate jdbcTemplate;
    public EmployeeTrainingRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public List<EmployeeTraining> findAll(UUID companyId) {
        String sql = "SELECT training_id, company_id, company_code, training_name, training_type_id, description, duration_hours, provider, created_at FROM employee_training " +
                (companyId == null ? "" : "WHERE company_id = ? ") + "ORDER BY training_name";
        return companyId == null ? jdbcTemplate.query(sql, this::mapRow) : jdbcTemplate.query(sql, this::mapRow, companyId);
    }

    public Optional<EmployeeTraining> findById(UUID id) {
        return jdbcTemplate.query("SELECT training_id, company_id, company_code, training_name, training_type_id, description, duration_hours, provider, created_at FROM employee_training WHERE training_id = ?", this::mapRow, id).stream().findFirst();
    }

    public EmployeeTraining create(EmployeeTrainingUpsertRequest r) {
        String sql = """
                INSERT INTO employee_training (company_id, company_code, training_name, training_type_id, description, duration_hours, provider)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING training_id, company_id, company_code, training_name, training_type_id, description, duration_hours, provider, created_at
                """;
        return jdbcTemplate.queryForObject(sql, this::mapRow, r.companyId(), r.companyCode(), r.trainingName(), r.trainingTypeId(), r.description(), r.durationHours(), r.provider());
    }

    public Optional<EmployeeTraining> update(UUID id, EmployeeTrainingUpsertRequest r) {
        String sql = """
                UPDATE employee_training SET
                    training_name = COALESCE(?, training_name),
                    training_type_id = COALESCE(?, training_type_id),
                    description = COALESCE(?, description),
                    duration_hours = COALESCE(?, duration_hours),
                    provider = COALESCE(?, provider)
                WHERE training_id = ?
                RETURNING training_id, company_id, company_code, training_name, training_type_id, description, duration_hours, provider, created_at
                """;
        return jdbcTemplate.query(sql, this::mapRow, r.trainingName(), r.trainingTypeId(), r.description(), r.durationHours(), r.provider(), id).stream().findFirst();
    }

    public boolean delete(UUID id) { return jdbcTemplate.update("DELETE FROM employee_training WHERE training_id = ?", id) > 0; }

    private EmployeeTraining mapRow(ResultSet rs, int n) throws SQLException {
        return new EmployeeTraining(rs.getObject("training_id", UUID.class), rs.getObject("company_id", UUID.class), rs.getString("company_code"),
                rs.getString("training_name"), rs.getObject("training_type_id", Integer.class), rs.getString("description"),
                rs.getObject("duration_hours", Integer.class), rs.getString("provider"), rs.getTimestamp("created_at").toInstant());
    }
}







