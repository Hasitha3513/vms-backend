package genxsolution.vms.vmsbackend.employee_hr_management.payroll_deduction.repository;
import genxsolution.vms.vmsbackend.employee_hr_management.payroll_deduction.dto.PayrollDeductionUpsertRequest; import genxsolution.vms.vmsbackend.employee_hr_management.payroll_deduction.model.PayrollDeduction; import org.springframework.jdbc.core.JdbcTemplate; import org.springframework.stereotype.Repository; import java.sql.*; import java.util.*;
@Repository public class PayrollDeductionRepository { private final JdbcTemplate jdbcTemplate; public PayrollDeductionRepository(JdbcTemplate jdbcTemplate){this.jdbcTemplate=jdbcTemplate;}
public List<PayrollDeduction> findAll(UUID companyId){ String sql="SELECT pay_ded_id, company_id, company_code, payroll_id, deduction_type_id, reference_id, amount, note FROM payroll_deduction "+(companyId==null?"":"WHERE company_id = ? ")+"ORDER BY pay_ded_id DESC"; return companyId==null?jdbcTemplate.query(sql,this::mapRow):jdbcTemplate.query(sql,this::mapRow,companyId);} 
public Optional<PayrollDeduction> findById(UUID id){ return jdbcTemplate.query("SELECT pay_ded_id, company_id, company_code, payroll_id, deduction_type_id, reference_id, amount, note FROM payroll_deduction WHERE pay_ded_id = ?", this::mapRow, id).stream().findFirst(); }
public PayrollDeduction create(PayrollDeductionUpsertRequest r){ String sql="""
INSERT INTO payroll_deduction (company_id, company_code, payroll_id, deduction_type_id, reference_id, amount, note)
VALUES (?, ?, ?, ?, ?, ?, ?)
RETURNING pay_ded_id, company_id, company_code, payroll_id, deduction_type_id, reference_id, amount, note
"""; return jdbcTemplate.queryForObject(sql,this::mapRow,r.companyId(),r.companyCode(),r.payrollId(),r.deductionTypeId(),r.referenceId(),r.amount(),r.note()); }
public Optional<PayrollDeduction> update(UUID id,PayrollDeductionUpsertRequest r){ String sql="""
UPDATE payroll_deduction SET payroll_id = COALESCE(?, payroll_id), deduction_type_id = COALESCE(?, deduction_type_id), reference_id = COALESCE(?, reference_id), amount = COALESCE(?, amount), note = COALESCE(?, note)
WHERE pay_ded_id = ?
RETURNING pay_ded_id, company_id, company_code, payroll_id, deduction_type_id, reference_id, amount, note
"""; return jdbcTemplate.query(sql,this::mapRow,r.payrollId(),r.deductionTypeId(),r.referenceId(),r.amount(),r.note(),id).stream().findFirst(); }
public boolean delete(UUID id){ return jdbcTemplate.update("DELETE FROM payroll_deduction WHERE pay_ded_id = ?", id)>0; }
private PayrollDeduction mapRow(ResultSet rs,int n)throws SQLException{ return new PayrollDeduction(rs.getObject("pay_ded_id",UUID.class),rs.getObject("company_id",UUID.class),rs.getString("company_code"),rs.getObject("payroll_id",UUID.class),rs.getObject("deduction_type_id",Integer.class),rs.getObject("reference_id",UUID.class),rs.getBigDecimal("amount"),rs.getString("note")); }
}
