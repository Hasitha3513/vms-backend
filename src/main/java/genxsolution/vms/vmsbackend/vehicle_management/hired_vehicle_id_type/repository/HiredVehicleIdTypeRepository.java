package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_id_type.repository;

import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_id_type.dto.HiredVehicleIdTypeUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_id_type.model.HiredVehicleIdType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class HiredVehicleIdTypeRepository {
    private final JdbcTemplate jdbcTemplate;

    public HiredVehicleIdTypeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<HiredVehicleIdType> findAll(UUID companyId) {
        String sql = "SELECT " + selectColumns() + " " + fromJoinClause() + " " +
                (companyId == null ? "" : "WHERE hvit.idtype_com = ? ") +
                "ORDER BY hvit.idtype_code";
        return companyId == null
                ? jdbcTemplate.query(sql, this::mapRow)
                : jdbcTemplate.query(sql, this::mapRow, companyId);
    }

    public Optional<HiredVehicleIdType> findById(UUID id) {
        return jdbcTemplate.query("SELECT " + selectColumns() + " " + fromJoinClause() + " WHERE hvit.idtype_id = ?", this::mapRow, id)
                .stream().findFirst();
    }

    public boolean existsByCompanyAndCode(UUID companyId, String idTypeCode, UUID excludeId) {
        String sql;
        List<Integer> rows;
        if (excludeId == null) {
            sql = """
                    SELECT 1
                    FROM hiredvehicleidtype
                    WHERE idtype_com = ?
                      AND lower(trim(idtype_code)) = lower(trim(?))
                    LIMIT 1
                    """;
            rows = jdbcTemplate.query(sql, (rs, i) -> rs.getInt(1), companyId, idTypeCode);
        } else {
            sql = """
                    SELECT 1
                    FROM hiredvehicleidtype
                    WHERE idtype_com = ?
                      AND lower(trim(idtype_code)) = lower(trim(?))
                      AND idtype_id <> ?
                    LIMIT 1
                    """;
            rows = jdbcTemplate.query(sql, (rs, i) -> rs.getInt(1), companyId, idTypeCode, excludeId);
        }
        return !rows.isEmpty();
    }

    public HiredVehicleIdType create(HiredVehicleIdTypeUpsertRequest r) {
        String sql = """
                INSERT INTO hiredvehicleidtype (idtype_com, company_code, idtype_typeid, idtype_code, is_active)
                VALUES (?, ?, ?, ?, COALESCE(?, TRUE))
                RETURNING idtype_id
                """;
        UUID insertedId = jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> rs.getObject("idtype_id", UUID.class),
                r.companyId(), r.companyCode(), r.typeId(), r.idTypeCode(), r.isActive()
        );
        if (insertedId == null) {
            throw new IllegalStateException("Failed to create Hired Vehicle Identity Type");
        }
        return findById(insertedId)
                .orElseThrow(() -> new IllegalStateException("Created Hired Vehicle Identity Type could not be loaded"));
    }

    public Optional<HiredVehicleIdType> update(UUID id, HiredVehicleIdTypeUpsertRequest r) {
        String sql = """
                UPDATE hiredvehicleidtype
                SET idtype_com = COALESCE(?, idtype_com),
                    company_code = COALESCE(?, company_code),
                    idtype_typeid = COALESCE(?, idtype_typeid),
                    idtype_code = COALESCE(?, idtype_code),
                    is_active = COALESCE(?, is_active)
                WHERE idtype_id = ?
                RETURNING idtype_id
                """;
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getObject("idtype_id", UUID.class),
                r.companyId(), r.companyCode(), r.typeId(), r.idTypeCode(), r.isActive(), id
        ).stream().findFirst().flatMap(this::findById);
    }

    public boolean delete(UUID id) {
        return jdbcTemplate.update("DELETE FROM hiredvehicleidtype WHERE idtype_id = ?", id) > 0;
    }

    public Optional<String> findCompanyCodeById(UUID companyId) {
        return jdbcTemplate.query(
                "SELECT company_code FROM company WHERE company_id = ?",
                (rs, i) -> rs.getString("company_code"),
                companyId
        ).stream().findFirst();
    }

    private HiredVehicleIdType mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        return new HiredVehicleIdType(
                rs.getObject("idtype_id", UUID.class),
                rs.getObject("idtype_com", UUID.class),
                rs.getString("company_name"),
                rs.getString("company_code"),
                rs.getObject("idtype_typeid", UUID.class),
                rs.getString("type_name"),
                rs.getString("idtype_code"),
                rs.getObject("is_active", Boolean.class),
                createdAtTs == null ? null : createdAtTs.toInstant(),
                updatedAtTs == null ? null : updatedAtTs.toInstant()
        );
    }

    private String selectColumns() {
        return """
                hvit.idtype_id,
                hvit.idtype_com,
                c.company_name AS company_name,
                hvit.company_code,
                hvit.idtype_typeid,
                vt.type_name AS type_name,
                hvit.idtype_code,
                hvit.is_active,
                hvit.created_at,
                hvit.updated_at
                """;
    }

    private String fromJoinClause() {
        return """
                FROM hiredvehicleidtype hvit
                LEFT JOIN company c ON c.company_id = hvit.idtype_com
                LEFT JOIN vehicle_type vt ON vt.type_id = hvit.idtype_typeid
                """;
    }
}
