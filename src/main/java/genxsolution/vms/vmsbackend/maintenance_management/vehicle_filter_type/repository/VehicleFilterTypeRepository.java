package genxsolution.vms.vmsbackend.maintenance_management.vehicle_filter_type.repository;
import genxsolution.vms.vmsbackend.maintenance_management.vehicle_filter_type.dto.VehicleFilterTypeUpsertRequest; import genxsolution.vms.vmsbackend.maintenance_management.vehicle_filter_type.model.VehicleFilterType; import org.springframework.jdbc.core.JdbcTemplate; import org.springframework.stereotype.Repository; import java.sql.*; import java.util.*;
@Repository public class VehicleFilterTypeRepository { private final JdbcTemplate jdbcTemplate; public VehicleFilterTypeRepository(JdbcTemplate jdbcTemplate){this.jdbcTemplate=jdbcTemplate;}
public List<VehicleFilterType> findAll(UUID companyId){ String sql="SELECT filter_type_id, company_id, company_code, filter_name, filter_code, description, typical_life_km, typical_life_hours, typical_life_months FROM vehicle_filter_type "+(companyId==null?"":"WHERE company_id = ? ")+"ORDER BY filter_type_id DESC"; return companyId==null?jdbcTemplate.query(sql,this::mapRow):jdbcTemplate.query(sql,this::mapRow,companyId); }
public Optional<VehicleFilterType> findById(UUID id){ return jdbcTemplate.query("SELECT filter_type_id, company_id, company_code, filter_name, filter_code, description, typical_life_km, typical_life_hours, typical_life_months FROM vehicle_filter_type WHERE filter_type_id = ?", this::mapRow, id).stream().findFirst(); }
public VehicleFilterType create(VehicleFilterTypeUpsertRequest r){ String sql="""
INSERT INTO vehicle_filter_type (company_id, company_code, filter_name, filter_code, description, typical_life_km, typical_life_hours, typical_life_months)
VALUES (?, ?, ?, ?, ?, ?, ?, ?)
RETURNING filter_type_id, company_id, company_code, filter_name, filter_code, description, typical_life_km, typical_life_hours, typical_life_months
"""; return jdbcTemplate.queryForObject(sql,this::mapRow,r.companyId(),r.companyCode(),r.filterName(),r.filterCode(),r.description(),r.typicalLifeKm(),r.typicalLifeHours(),r.typicalLifeMonths()); }
public Optional<VehicleFilterType> update(UUID id,VehicleFilterTypeUpsertRequest r){ String sql="""
UPDATE vehicle_filter_type SET company_id = COALESCE(?, company_id), company_code = COALESCE(?, company_code), filter_name = COALESCE(?, filter_name), filter_code = COALESCE(?, filter_code), description = COALESCE(?, description), typical_life_km = COALESCE(?, typical_life_km), typical_life_hours = COALESCE(?, typical_life_hours), typical_life_months = COALESCE(?, typical_life_months)
WHERE filter_type_id = ?
RETURNING filter_type_id, company_id, company_code, filter_name, filter_code, description, typical_life_km, typical_life_hours, typical_life_months
"""; return jdbcTemplate.query(sql,this::mapRow,r.companyId(),r.companyCode(),r.filterName(),r.filterCode(),r.description(),r.typicalLifeKm(),r.typicalLifeHours(),r.typicalLifeMonths(),id).stream().findFirst(); }
public boolean delete(UUID id){ return jdbcTemplate.update("DELETE FROM vehicle_filter_type WHERE filter_type_id = ?", id)>0; }
private VehicleFilterType mapRow(ResultSet rs,int n)throws SQLException{  return new VehicleFilterType(rs.getObject("filter_type_id",UUID.class),rs.getObject("company_id",UUID.class),rs.getString("company_code"),rs.getString("filter_name"),rs.getString("filter_code"),rs.getString("description"),rs.getObject("typical_life_km",Integer.class),rs.getObject("typical_life_hours",Integer.class),rs.getObject("typical_life_months",Integer.class)); }
}
