package genxsolution.vms.vmsbackend.employee_hr_management.employee_advance.repository;
import genxsolution.vms.vmsbackend.employee_hr_management.employee_advance.dto.EmployeeAdvanceUpsertRequest; import genxsolution.vms.vmsbackend.employee_hr_management.employee_advance.model.EmployeeAdvance; import org.springframework.jdbc.core.JdbcTemplate; import org.springframework.stereotype.Repository; import java.sql.*; import java.util.*;
@Repository public class EmployeeAdvanceRepository { private final JdbcTemplate jdbcTemplate; public EmployeeAdvanceRepository(JdbcTemplate jdbcTemplate){this.jdbcTemplate=jdbcTemplate;}
public List<EmployeeAdvance> findAll(UUID companyId){ String sql="SELECT advance_id, company_id, company_code, employee_id, issued_date, amount, balance, purpose, status_id, created_at FROM employee_advance "+(companyId==null?"":"WHERE company_id = ? ")+"ORDER BY issued_date DESC, created_at DESC"; return companyId==null?jdbcTemplate.query(sql,this::mapRow):jdbcTemplate.query(sql,this::mapRow,companyId);} 
public Optional<EmployeeAdvance> findById(UUID id){ return jdbcTemplate.query("SELECT advance_id, company_id, company_code, employee_id, issued_date, amount, balance, purpose, status_id, created_at FROM employee_advance WHERE advance_id = ?", this::mapRow, id).stream().findFirst(); }
public EmployeeAdvance create(EmployeeAdvanceUpsertRequest r){ String sql="""
INSERT INTO employee_advance (company_id, company_code, employee_id, issued_date, amount, balance, purpose, status_id)
VALUES (?, ?, ?, ?, ?, ?, ?, ?)
RETURNING advance_id, company_id, company_code, employee_id, issued_date, amount, balance, purpose, status_id, created_at
"""; return jdbcTemplate.queryForObject(sql,this::mapRow,r.companyId(),r.companyCode(),r.employeeId(),r.issuedDate(),r.amount(),r.balance(),r.purpose(),r.statusId()); }
public Optional<EmployeeAdvance> update(UUID id,EmployeeAdvanceUpsertRequest r){ String sql="""
UPDATE employee_advance SET employee_id = COALESCE(?, employee_id), issued_date = COALESCE(?, issued_date), amount = COALESCE(?, amount), balance = COALESCE(?, balance), purpose = COALESCE(?, purpose), status_id = COALESCE(?, status_id)
WHERE advance_id = ?
RETURNING advance_id, company_id, company_code, employee_id, issued_date, amount, balance, purpose, status_id, created_at
"""; return jdbcTemplate.query(sql,this::mapRow,r.employeeId(),r.issuedDate(),r.amount(),r.balance(),r.purpose(),r.statusId(),id).stream().findFirst(); }
public boolean delete(UUID id){ return jdbcTemplate.update("DELETE FROM employee_advance WHERE advance_id = ?", id)>0; }
private EmployeeAdvance mapRow(ResultSet rs,int n)throws SQLException{ Timestamp c=rs.getTimestamp("created_at"); java.sql.Date d=rs.getDate("issued_date"); return new EmployeeAdvance(rs.getObject("advance_id",UUID.class),rs.getObject("company_id",UUID.class),rs.getString("company_code"),rs.getObject("employee_id",UUID.class),d==null?null:d.toLocalDate(),rs.getBigDecimal("amount"),rs.getBigDecimal("balance"),rs.getString("purpose"),rs.getObject("status_id",Integer.class),c==null?null:c.toInstant()); }
}
