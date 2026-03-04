package genxsolution.vms.vmsbackend.maintenance_management.vehicle_maintenance_program.repository;
import genxsolution.vms.vmsbackend.maintenance_management.vehicle_maintenance_program.dto.VehicleMaintenanceProgramUpsertRequest; import genxsolution.vms.vmsbackend.maintenance_management.vehicle_maintenance_program.model.VehicleMaintenanceProgram; import org.springframework.jdbc.core.JdbcTemplate; import org.springframework.stereotype.Repository; import java.sql.*; import java.util.*;
@Repository public class VehicleMaintenanceProgramRepository { private final JdbcTemplate jdbcTemplate; public VehicleMaintenanceProgramRepository(JdbcTemplate jdbcTemplate){this.jdbcTemplate=jdbcTemplate;}
public List<VehicleMaintenanceProgram> findAll(UUID companyId){ String sql="SELECT vehicle_program_id, company_id, company_code, vehicle_id, program_id, start_date, end_date, is_active, created_at FROM vehicle_maintenance_program "+(companyId==null?"":"WHERE company_id = ? ")+"ORDER BY created_at DESC"; return companyId==null?jdbcTemplate.query(sql,this::mapRow):jdbcTemplate.query(sql,this::mapRow,companyId); }
public Optional<VehicleMaintenanceProgram> findById(UUID id){ return jdbcTemplate.query("SELECT vehicle_program_id, company_id, company_code, vehicle_id, program_id, start_date, end_date, is_active, created_at FROM vehicle_maintenance_program WHERE vehicle_program_id = ?", this::mapRow, id).stream().findFirst(); }
public VehicleMaintenanceProgram create(VehicleMaintenanceProgramUpsertRequest r){ String sql="""
INSERT INTO vehicle_maintenance_program (company_id, company_code, vehicle_id, program_id, start_date, end_date, is_active)
VALUES (?, ?, ?, ?, ?, ?, ?)
RETURNING vehicle_program_id, company_id, company_code, vehicle_id, program_id, start_date, end_date, is_active, created_at
"""; return jdbcTemplate.queryForObject(sql,this::mapRow,r.companyId(),r.companyCode(),r.vehicleId(),r.programId(),r.startDate(),r.endDate(),r.isActive()); }
public Optional<VehicleMaintenanceProgram> update(UUID id,VehicleMaintenanceProgramUpsertRequest r){ String sql="""
UPDATE vehicle_maintenance_program SET company_id = COALESCE(?, company_id), company_code = COALESCE(?, company_code), vehicle_id = COALESCE(?, vehicle_id), program_id = COALESCE(?, program_id), start_date = COALESCE(?, start_date), end_date = COALESCE(?, end_date), is_active = COALESCE(?, is_active)
WHERE vehicle_program_id = ?
RETURNING vehicle_program_id, company_id, company_code, vehicle_id, program_id, start_date, end_date, is_active, created_at
"""; return jdbcTemplate.query(sql,this::mapRow,r.companyId(),r.companyCode(),r.vehicleId(),r.programId(),r.startDate(),r.endDate(),r.isActive(),id).stream().findFirst(); }
public boolean delete(UUID id){ return jdbcTemplate.update("DELETE FROM vehicle_maintenance_program WHERE vehicle_program_id = ?", id)>0; }
private VehicleMaintenanceProgram mapRow(ResultSet rs,int n)throws SQLException{ java.sql.Date startDateDate=rs.getDate("start_date"); java.sql.Date endDateDate=rs.getDate("end_date"); Timestamp createdAtTs=rs.getTimestamp("created_at"); return new VehicleMaintenanceProgram(rs.getObject("vehicle_program_id",UUID.class),rs.getObject("company_id",UUID.class),rs.getString("company_code"),rs.getObject("vehicle_id",UUID.class),rs.getObject("program_id",UUID.class),startDateDate==null?null:startDateDate.toLocalDate(),endDateDate==null?null:endDateDate.toLocalDate(),rs.getObject("is_active",Boolean.class),createdAtTs==null?null:createdAtTs.toInstant()); }
}
