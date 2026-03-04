package genxsolution.vms.vmsbackend.employee_hr_management.employee_skill.repository;

import genxsolution.vms.vmsbackend.employee_hr_management.employee_skill.dto.EmployeeSkillUpsertRequest;
import genxsolution.vms.vmsbackend.employee_hr_management.employee_skill.model.EmployeeSkill;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import java.util.Map;

@Repository
public class EmployeeSkillRepository {
    private final JdbcTemplate jdbcTemplate;
    public EmployeeSkillRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public List<EmployeeSkill> findAll(UUID companyId, String q, int limit, Map<String, String> filters) {
        StringBuilder sql = new StringBuilder("""
                SELECT skill_id, company_id, company_code, skill_name, skill_category_id, description
                FROM employee_skill
                WHERE 1=1
                """);
        List<Object> args = new ArrayList<>();
        if (companyId != null) {
            sql.append(" AND company_id = ? ");
            args.add(companyId);
        }

        if (q != null && !q.isBlank()) {
            sql.append(" AND skill_name ILIKE ? ");
            args.add("%" + q.trim() + "%");
        }

        String skillCategoryId = filters == null ? null : filters.get("skillCategoryId");
        if (skillCategoryId != null && !skillCategoryId.isBlank() && !"null".equalsIgnoreCase(skillCategoryId) && !"undefined".equalsIgnoreCase(skillCategoryId)) {
            sql.append(" AND skill_category_id = ? ");
            args.add(Integer.parseInt(skillCategoryId));
        }


        sql.append(" ORDER BY skill_name LIMIT ?");
        args.add(limit);
        return jdbcTemplate.query(sql.toString(), this::mapRow, args.toArray());
    }


    public Optional<EmployeeSkill> findById(UUID id) {
        return jdbcTemplate.query("SELECT skill_id, company_id, company_code, skill_name, skill_category_id, description FROM employee_skill WHERE skill_id = ?", this::mapRow, id).stream().findFirst();
    }

    public EmployeeSkill create(EmployeeSkillUpsertRequest r) {
        String sql = """
                INSERT INTO employee_skill (company_id, company_code, skill_name, skill_category_id, description)
                VALUES (?, ?, ?, ?, ?)
                RETURNING skill_id, company_id, company_code, skill_name, skill_category_id, description
                """;
        return jdbcTemplate.queryForObject(sql, this::mapRow, r.companyId(), r.companyCode(), r.skillName(), r.skillCategoryId(), r.description());
    }

    public Optional<EmployeeSkill> update(UUID id, EmployeeSkillUpsertRequest r) {
        String sql = """
                UPDATE employee_skill SET
                    skill_name = COALESCE(?, skill_name),
                    skill_category_id = COALESCE(?, skill_category_id),
                    description = COALESCE(?, description)
                WHERE skill_id = ?
                RETURNING skill_id, company_id, company_code, skill_name, skill_category_id, description
                """;
        return jdbcTemplate.query(sql, this::mapRow, r.skillName(), r.skillCategoryId(), r.description(), id).stream().findFirst();
    }

    public boolean delete(UUID id) { return jdbcTemplate.update("DELETE FROM employee_skill WHERE skill_id = ?", id) > 0; }

    private EmployeeSkill mapRow(ResultSet rs, int n) throws SQLException {
        return new EmployeeSkill(rs.getObject("skill_id", UUID.class), rs.getObject("company_id", UUID.class), rs.getString("company_code"),
                rs.getString("skill_name"), rs.getObject("skill_category_id", Integer.class), rs.getString("description"));
    }
}






