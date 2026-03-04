package genxsolution.vms.vmsbackend.employee_hr_management.ration_distribution.repository;
import genxsolution.vms.vmsbackend.employee_hr_management.ration_distribution.dto.RationDistributionUpsertRequest; import genxsolution.vms.vmsbackend.employee_hr_management.ration_distribution.model.RationDistribution; import org.springframework.jdbc.core.JdbcTemplate; import org.springframework.stereotype.Repository; import java.sql.*; import java.util.*;
@Repository public class RationDistributionRepository { private final JdbcTemplate jdbcTemplate; public RationDistributionRepository(JdbcTemplate jdbcTemplate){this.jdbcTemplate=jdbcTemplate;}
public List<RationDistribution> findAll(UUID companyId){ String sql="SELECT ration_id, company_id, company_code, employee_id, project_id, ration_date, meals_count, amount, notes, created_at FROM ration_distribution "+(companyId==null?"":"WHERE company_id = ? ")+"ORDER BY ration_date DESC"; return companyId==null?jdbcTemplate.query(sql,this::mapRow):jdbcTemplate.query(sql,this::mapRow,companyId);} 
public Optional<RationDistribution> findById(UUID id){ return jdbcTemplate.query("SELECT ration_id, company_id, company_code, employee_id, project_id, ration_date, meals_count, amount, notes, created_at FROM ration_distribution WHERE ration_id = ?", this::mapRow, id).stream().findFirst(); }
public RationDistribution create(RationDistributionUpsertRequest r){ String sql="""
INSERT INTO ration_distribution (company_id, company_code, employee_id, project_id, ration_date, meals_count, amount, notes)
VALUES (?, ?, ?, ?, ?, ?, ?, ?)
RETURNING ration_id, company_id, company_code, employee_id, project_id, ration_date, meals_count, amount, notes, created_at
"""; return jdbcTemplate.queryForObject(sql,this::mapRow,r.companyId(),r.companyCode(),r.employeeId(),r.projectId(),r.rationDate(),r.mealsCount(),r.amount(),r.notes()); }
public Optional<RationDistribution> update(UUID id,RationDistributionUpsertRequest r){ String sql="""
UPDATE ration_distribution SET employee_id = COALESCE(?, employee_id), project_id = COALESCE(?, project_id), ration_date = COALESCE(?, ration_date), meals_count = COALESCE(?, meals_count), amount = COALESCE(?, amount), notes = COALESCE(?, notes)
WHERE ration_id = ?
RETURNING ration_id, company_id, company_code, employee_id, project_id, ration_date, meals_count, amount, notes, created_at
"""; return jdbcTemplate.query(sql,this::mapRow,r.employeeId(),r.projectId(),r.rationDate(),r.mealsCount(),r.amount(),r.notes(),id).stream().findFirst(); }
public boolean delete(UUID id){ return jdbcTemplate.update("DELETE FROM ration_distribution WHERE ration_id = ?", id)>0; }
private RationDistribution mapRow(ResultSet rs,int n)throws SQLException{ Timestamp c=rs.getTimestamp("created_at"); java.sql.Date d=rs.getDate("ration_date"); return new RationDistribution(rs.getObject("ration_id",UUID.class),rs.getObject("company_id",UUID.class),rs.getString("company_code"),rs.getObject("employee_id",UUID.class),rs.getObject("project_id",UUID.class),d==null?null:d.toLocalDate(),rs.getObject("meals_count",Integer.class),rs.getBigDecimal("amount"),rs.getString("notes"),c==null?null:c.toInstant()); }
}
