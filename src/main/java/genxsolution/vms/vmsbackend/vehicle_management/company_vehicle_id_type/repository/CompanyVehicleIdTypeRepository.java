package genxsolution.vms.vmsbackend.vehicle_management.company_vehicle_id_type.repository;

import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle_id_type.dto.CompanyVehicleIdTypeUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle_id_type.model.CompanyVehicleIdType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CompanyVehicleIdTypeRepository {
    private final JdbcTemplate jdbcTemplate;

    public CompanyVehicleIdTypeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CompanyVehicleIdType> findAll(UUID companyId) {
        String sql = "SELECT " + selectColumns() + " " + fromJoinClause() + " " +
                (companyId == null ? "" : "WHERE cvit.idtype_com = ? ") +
                "ORDER BY cvit.idtype_code";
        return companyId == null
                ? jdbcTemplate.query(sql, this::mapRow)
                : jdbcTemplate.query(sql, this::mapRow, companyId);
    }

    public Optional<CompanyVehicleIdType> findById(UUID id) {
        return jdbcTemplate.query("SELECT " + selectColumns() + " " + fromJoinClause() + " WHERE cvit.idtype_id = ?", this::mapRow, id)
                .stream().findFirst();
    }

    public boolean existsByCompanyAndCode(UUID companyId, String idTypeCode, UUID excludeId) {
        String sql;
        List<Integer> rows;
        if (excludeId == null) {
            sql = """
                    SELECT 1
                    FROM companyvehicleidtype
                    WHERE idtype_com = ?
                      AND lower(trim(idtype_code)) = lower(trim(?))
                    LIMIT 1
                    """;
            rows = jdbcTemplate.query(sql, (rs, i) -> rs.getInt(1), companyId, idTypeCode);
        } else {
            sql = """
                    SELECT 1
                    FROM companyvehicleidtype
                    WHERE idtype_com = ?
                      AND lower(trim(idtype_code)) = lower(trim(?))
                      AND idtype_id <> ?
                    LIMIT 1
                    """;
            rows = jdbcTemplate.query(sql, (rs, i) -> rs.getInt(1), companyId, idTypeCode, excludeId);
        }
        return !rows.isEmpty();
    }

    public CompanyVehicleIdType create(CompanyVehicleIdTypeUpsertRequest r) {
        String sql = """
                INSERT INTO companyvehicleidtype (idtype_com, company_code, idtype_typeid, idtype_code, is_active)
                VALUES (?, ?, ?, ?, COALESCE(?, TRUE))
                RETURNING idtype_id
                """;
        UUID insertedId = jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> rs.getObject("idtype_id", UUID.class),
                r.companyId(), r.companyCode(), r.typeId(), r.idTypeCode(), r.isActive()
        );
        if (insertedId == null) {
            throw new IllegalStateException("Failed to create Company Vehicle Identity Type");
        }
        return findById(insertedId)
                .orElseThrow(() -> new IllegalStateException("Created Company Vehicle Identity Type could not be loaded"));
    }

    public Optional<CompanyVehicleIdType> update(UUID id, CompanyVehicleIdTypeUpsertRequest r) {
        String sql = """
                UPDATE companyvehicleidtype
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
        return jdbcTemplate.update("DELETE FROM companyvehicleidtype WHERE idtype_id = ?", id) > 0;
    }

    public Optional<String> findCompanyCodeById(UUID companyId) {
        return jdbcTemplate.query(
                "SELECT company_code FROM company WHERE company_id = ?",
                (rs, i) -> rs.getString("company_code"),
                companyId
        ).stream().findFirst();
    }

    private CompanyVehicleIdType mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        return new CompanyVehicleIdType(
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
                cvit.idtype_id,
                cvit.idtype_com,
                c.company_name AS company_name,
                cvit.company_code,
                cvit.idtype_typeid,
                vt.type_name AS type_name,
                cvit.idtype_code,
                cvit.is_active,
                cvit.created_at,
                cvit.updated_at
                """;
    }

    private String fromJoinClause() {
        return """
                FROM companyvehicleidtype cvit
                LEFT JOIN company c ON c.company_id = cvit.idtype_com
                LEFT JOIN vehicle_type vt ON vt.type_id = cvit.idtype_typeid
                """;
    }
}
