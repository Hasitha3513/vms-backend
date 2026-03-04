package genxsolution.vms.vmsbackend.maintenance_management.maintenance_strategy.repository;
import genxsolution.vms.vmsbackend.maintenance_management.maintenance_strategy.dto.MaintenanceStrategyUpsertRequest; import genxsolution.vms.vmsbackend.maintenance_management.maintenance_strategy.model.MaintenanceStrategy; import org.springframework.jdbc.core.JdbcTemplate; import org.springframework.stereotype.Repository; import java.sql.*; import java.util.*;
@Repository public class MaintenanceStrategyRepository { private final JdbcTemplate jdbcTemplate; public MaintenanceStrategyRepository(JdbcTemplate jdbcTemplate){this.jdbcTemplate=jdbcTemplate;}
public List<MaintenanceStrategy> findAll(UUID companyId){ String sql="SELECT strategy_id, company_id, company_code, strategy_name, strategy_type_id, description FROM maintenance_strategy "+(companyId==null?"":"WHERE company_id = ? ")+"ORDER BY strategy_id DESC"; return companyId==null?jdbcTemplate.query(sql,this::mapRow):jdbcTemplate.query(sql,this::mapRow,companyId); }
public Optional<MaintenanceStrategy> findById(UUID id){ return jdbcTemplate.query("SELECT strategy_id, company_id, company_code, strategy_name, strategy_type_id, description FROM maintenance_strategy WHERE strategy_id = ?", this::mapRow, id).stream().findFirst(); }
public MaintenanceStrategy create(MaintenanceStrategyUpsertRequest r){ String sql="""
INSERT INTO maintenance_strategy (company_id, company_code, strategy_name, strategy_type_id, description)
VALUES (?, ?, ?, ?, ?)
RETURNING strategy_id, company_id, company_code, strategy_name, strategy_type_id, description
"""; return jdbcTemplate.queryForObject(sql,this::mapRow,r.companyId(),r.companyCode(),r.strategyName(),r.strategyTypeId(),r.description()); }
public Optional<MaintenanceStrategy> update(UUID id,MaintenanceStrategyUpsertRequest r){ String sql="""
UPDATE maintenance_strategy SET company_id = COALESCE(?, company_id), company_code = COALESCE(?, company_code), strategy_name = COALESCE(?, strategy_name), strategy_type_id = COALESCE(?, strategy_type_id), description = COALESCE(?, description)
WHERE strategy_id = ?
RETURNING strategy_id, company_id, company_code, strategy_name, strategy_type_id, description
"""; return jdbcTemplate.query(sql,this::mapRow,r.companyId(),r.companyCode(),r.strategyName(),r.strategyTypeId(),r.description(),id).stream().findFirst(); }
public boolean delete(UUID id){ return jdbcTemplate.update("DELETE FROM maintenance_strategy WHERE strategy_id = ?", id)>0; }
private MaintenanceStrategy mapRow(ResultSet rs,int n)throws SQLException{  return new MaintenanceStrategy(rs.getObject("strategy_id",UUID.class),rs.getObject("company_id",UUID.class),rs.getString("company_code"),rs.getString("strategy_name"),rs.getObject("strategy_type_id",Integer.class),rs.getString("description")); }
}
