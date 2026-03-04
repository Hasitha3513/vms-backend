package genxsolution.vms.vmsbackend.maintenance_management.maintenance_program.repository;
import genxsolution.vms.vmsbackend.maintenance_management.maintenance_program.dto.MaintenanceProgramUpsertRequest; import genxsolution.vms.vmsbackend.maintenance_management.maintenance_program.model.MaintenanceProgram; import org.springframework.jdbc.core.JdbcTemplate; import org.springframework.stereotype.Repository; import java.sql.*; import java.util.*;
@Repository public class MaintenanceProgramRepository { private final JdbcTemplate jdbcTemplate; public MaintenanceProgramRepository(JdbcTemplate jdbcTemplate){this.jdbcTemplate=jdbcTemplate;}
public List<MaintenanceProgram> findAll(UUID companyId){ String sql="SELECT program_id, company_id, company_code, program_name, program_type_id, description, is_active, created_at FROM maintenance_program "+(companyId==null?"":"WHERE company_id = ? ")+"ORDER BY created_at DESC"; return companyId==null?jdbcTemplate.query(sql,this::mapRow):jdbcTemplate.query(sql,this::mapRow,companyId); }
public Optional<MaintenanceProgram> findById(UUID id){ return jdbcTemplate.query("SELECT program_id, company_id, company_code, program_name, program_type_id, description, is_active, created_at FROM maintenance_program WHERE program_id = ?", this::mapRow, id).stream().findFirst(); }
public MaintenanceProgram create(MaintenanceProgramUpsertRequest r){ String sql="""
INSERT INTO maintenance_program (company_id, company_code, program_name, program_type_id, description, is_active)
VALUES (?, ?, ?, ?, ?, ?)
RETURNING program_id, company_id, company_code, program_name, program_type_id, description, is_active, created_at
"""; return jdbcTemplate.queryForObject(sql,this::mapRow,r.companyId(),r.companyCode(),r.programName(),r.programTypeId(),r.description(),r.isActive()); }
public Optional<MaintenanceProgram> update(UUID id,MaintenanceProgramUpsertRequest r){ String sql="""
UPDATE maintenance_program SET company_id = COALESCE(?, company_id), company_code = COALESCE(?, company_code), program_name = COALESCE(?, program_name), program_type_id = COALESCE(?, program_type_id), description = COALESCE(?, description), is_active = COALESCE(?, is_active)
WHERE program_id = ?
RETURNING program_id, company_id, company_code, program_name, program_type_id, description, is_active, created_at
"""; return jdbcTemplate.query(sql,this::mapRow,r.companyId(),r.companyCode(),r.programName(),r.programTypeId(),r.description(),r.isActive(),id).stream().findFirst(); }
public boolean delete(UUID id){ return jdbcTemplate.update("DELETE FROM maintenance_program WHERE program_id = ?", id)>0; }
private MaintenanceProgram mapRow(ResultSet rs,int n)throws SQLException{ Timestamp createdAtTs=rs.getTimestamp("created_at"); return new MaintenanceProgram(rs.getObject("program_id",UUID.class),rs.getObject("company_id",UUID.class),rs.getString("company_code"),rs.getString("program_name"),rs.getObject("program_type_id",Integer.class),rs.getString("description"),rs.getObject("is_active",Boolean.class),createdAtTs==null?null:createdAtTs.toInstant()); }
}
