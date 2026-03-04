package genxsolution.vms.vmsbackend.employee_hr_management.ration_policy.repository;
import genxsolution.vms.vmsbackend.employee_hr_management.ration_policy.dto.RationPolicyUpsertRequest; import genxsolution.vms.vmsbackend.employee_hr_management.ration_policy.model.RationPolicy; import org.springframework.jdbc.core.JdbcTemplate; import org.springframework.stereotype.Repository; import java.sql.*; import java.util.*;
@Repository public class RationPolicyRepository { private final JdbcTemplate jdbcTemplate; public RationPolicyRepository(JdbcTemplate jdbcTemplate){this.jdbcTemplate=jdbcTemplate;}
public List<RationPolicy> findAll(UUID companyId){ String sql="SELECT ration_policy_id, company_id, company_code, policy_name, per_day_amount, notes, is_active, created_at FROM ration_policy "+(companyId==null?"":"WHERE company_id = ? ")+"ORDER BY policy_name"; return companyId==null?jdbcTemplate.query(sql,this::mapRow):jdbcTemplate.query(sql,this::mapRow,companyId);} 
public Optional<RationPolicy> findById(UUID id){ return jdbcTemplate.query("SELECT ration_policy_id, company_id, company_code, policy_name, per_day_amount, notes, is_active, created_at FROM ration_policy WHERE ration_policy_id = ?", this::mapRow, id).stream().findFirst(); }
public RationPolicy create(RationPolicyUpsertRequest r){ String sql="""
INSERT INTO ration_policy (company_id, company_code, policy_name, per_day_amount, notes, is_active)
VALUES (?, ?, ?, ?, ?, ?)
RETURNING ration_policy_id, company_id, company_code, policy_name, per_day_amount, notes, is_active, created_at
"""; return jdbcTemplate.queryForObject(sql,this::mapRow,r.companyId(),r.companyCode(),r.policyName(),r.perDayAmount(),r.notes(),r.isActive()); }
public Optional<RationPolicy> update(UUID id,RationPolicyUpsertRequest r){ String sql="""
UPDATE ration_policy SET policy_name = COALESCE(?, policy_name), per_day_amount = COALESCE(?, per_day_amount), notes = COALESCE(?, notes), is_active = COALESCE(?, is_active)
WHERE ration_policy_id = ?
RETURNING ration_policy_id, company_id, company_code, policy_name, per_day_amount, notes, is_active, created_at
"""; return jdbcTemplate.query(sql,this::mapRow,r.policyName(),r.perDayAmount(),r.notes(),r.isActive(),id).stream().findFirst(); }
public boolean delete(UUID id){ return jdbcTemplate.update("DELETE FROM ration_policy WHERE ration_policy_id = ?", id)>0; }
private RationPolicy mapRow(ResultSet rs,int n)throws SQLException{ Timestamp c=rs.getTimestamp("created_at"); return new RationPolicy(rs.getObject("ration_policy_id",UUID.class),rs.getObject("company_id",UUID.class),rs.getString("company_code"),rs.getString("policy_name"),rs.getBigDecimal("per_day_amount"),rs.getString("notes"),rs.getObject("is_active",Boolean.class),c==null?null:c.toInstant()); }
}
