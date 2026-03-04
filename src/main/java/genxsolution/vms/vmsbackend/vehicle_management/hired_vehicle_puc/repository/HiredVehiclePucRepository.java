package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_puc.repository;

import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_puc.dto.HiredVehiclePucUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_puc.model.HiredVehiclePuc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class HiredVehiclePucRepository {
    private final JdbcTemplate jdbcTemplate;

    public HiredVehiclePucRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<HiredVehiclePuc> findAll(UUID supplierId, UUID hiredVehicleId) {
        StringBuilder sql = new StringBuilder("SELECT " + selectColumns() + " " + fromJoinClause() + " WHERE 1=1 ");
        if (supplierId != null) sql.append(" AND vp.supplier_id = ? ");
        if (hiredVehicleId != null) sql.append(" AND vp.hiredvehicle_id = ? ");
        sql.append(" ORDER BY vp.updated_at DESC, vp.created_at DESC");

        if (supplierId != null && hiredVehicleId != null) return jdbcTemplate.query(sql.toString(), this::mapRow, supplierId, hiredVehicleId);
        if (supplierId != null) return jdbcTemplate.query(sql.toString(), this::mapRow, supplierId);
        if (hiredVehicleId != null) return jdbcTemplate.query(sql.toString(), this::mapRow, hiredVehicleId);
        return jdbcTemplate.query(sql.toString(), this::mapRow);
    }

    public Optional<HiredVehiclePuc> findById(UUID id) {
        return jdbcTemplate.query("SELECT " + selectColumns() + " " + fromJoinClause() + " WHERE vp.puc_id = ?", this::mapRow, id)
                .stream().findFirst();
    }

    public HiredVehiclePuc create(HiredVehiclePucUpsertRequest r) {
        String sql = """
                INSERT INTO hired_vehicle_puc (
                    vehicle_id, supplier_id, hiredvehicle_id, certificate_number,
                    issuing_center, issue_date, expiry_date, co_emission_percent,
                    hc_emission_ppm, test_result, puc_status, renewal_reminder_days, is_current
                ) VALUES (
                    ?, ?, ?, ?,
                    ?, ?, ?, ?,
                    ?, COALESCE(?, 'Pass'), COALESCE(?, 'Valid'), COALESCE(?, 15), COALESCE(?, TRUE)
                )
                RETURNING puc_id
                """;

        UUID id = jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getObject("puc_id", UUID.class),
                r.vehicleId(), r.supplierId(), r.hiredVehicleId(), r.certificateNumber(),
                r.issuingCenter(), r.issueDate(), r.expiryDate(), r.coEmissionPercent(),
                r.hcEmissionPpm(), r.testResult(), r.pucStatus(), r.renewalReminderDays(), r.isCurrent()
        ).stream().findFirst().orElseThrow(() -> new IllegalStateException("Failed to create Vehicle PUC"));

        return findById(id).orElseThrow(() -> new IllegalStateException("Created Vehicle PUC could not be loaded"));
    }

    public Optional<HiredVehiclePuc> update(UUID id, HiredVehiclePucUpsertRequest r) {
        String sql = """
                UPDATE hired_vehicle_puc
                SET vehicle_id = COALESCE(?, vehicle_id),
                    supplier_id = COALESCE(?, supplier_id),
                    hiredvehicle_id = COALESCE(?, hiredvehicle_id),
                    certificate_number = COALESCE(?, certificate_number),
                    issuing_center = COALESCE(?, issuing_center),
                    issue_date = COALESCE(?, issue_date),
                    expiry_date = COALESCE(?, expiry_date),
                    co_emission_percent = COALESCE(?, co_emission_percent),
                    hc_emission_ppm = COALESCE(?, hc_emission_ppm),
                    test_result = COALESCE(?, test_result),
                    puc_status = COALESCE(?, puc_status),
                    renewal_reminder_days = COALESCE(?, renewal_reminder_days),
                    is_current = COALESCE(?, is_current)
                WHERE puc_id = ?
                RETURNING puc_id
                """;

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getObject("puc_id", UUID.class),
                r.vehicleId(), r.supplierId(), r.hiredVehicleId(), r.certificateNumber(),
                r.issuingCenter(), r.issueDate(), r.expiryDate(), r.coEmissionPercent(),
                r.hcEmissionPpm(), r.testResult(), r.pucStatus(), r.renewalReminderDays(), r.isCurrent(),
                id
        ).stream().findFirst().flatMap(this::findById);
    }

    public boolean delete(UUID id) {
        return jdbcTemplate.update("DELETE FROM hired_vehicle_puc WHERE puc_id = ?", id) > 0;
    }

    public boolean HiredVehicleBelongsToCompany(UUID hiredVehicleId, UUID supplierId) {
        List<Integer> rows = jdbcTemplate.query(
                "SELECT 1 FROM hired_vehicles WHERE hiredvehicle_id = ? AND supplier_id = ? LIMIT 1",
                (rs, i) -> rs.getInt(1),
                hiredVehicleId,
                supplierId
        );
        return !rows.isEmpty();
    }

    public Optional<UUID> findVehicleIdByHiredVehicleId(UUID hiredVehicleId) {
        return jdbcTemplate.query(
                """
                SELECT v.vehicle_id
                FROM hired_vehicles hv
                LEFT JOIN vehicle v
                  ON (
                       v.chassis_number = hv.chassis_number
                    OR (
                         hv.registration_number IS NOT NULL
                         AND v.registration_number = hv.registration_number
                       )
                  )
                WHERE hv.hiredvehicle_id = ?
                ORDER BY v.updated_at DESC NULLS LAST, v.created_at DESC NULLS LAST
                LIMIT 1
                """,
                (rs, i) -> rs.getObject("vehicle_id", UUID.class),
                hiredVehicleId
        ).stream().findFirst();
    }

    public Optional<UUID> findSupplierIdByHiredVehicleId(UUID hiredVehicleId) {
        return jdbcTemplate.query(
                "SELECT supplier_id FROM hired_vehicles WHERE hiredvehicle_id = ?",
                (rs, i) -> rs.getObject("supplier_id", UUID.class),
                hiredVehicleId
        ).stream().findFirst();
    }

    public Optional<HiredVehiclePuc> findLatestByHiredVehicleId(UUID hiredVehicleId) {
        return jdbcTemplate.query(
                "SELECT " + selectColumns() + " " + fromJoinClause() + " WHERE vp.hiredvehicle_id = ? ORDER BY vp.updated_at DESC, vp.created_at DESC LIMIT 1",
                this::mapRow,
                hiredVehicleId
        ).stream().findFirst();
    }

    public int clearCurrentForHiredVehicle(UUID hiredVehicleId, UUID exceptPucId) {
        return jdbcTemplate.update(
                """
                UPDATE hired_vehicle_puc
                SET is_current = FALSE
                WHERE hiredvehicle_id = ?
                  AND (? IS NULL OR puc_id <> ?)
                """,
                hiredVehicleId,
                exceptPucId,
                exceptPucId
        );
    }

    private HiredVehiclePuc mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        Date issueDate = rs.getDate("issue_date");
        Date expiryDate = rs.getDate("expiry_date");

        return new HiredVehiclePuc(
                rs.getObject("puc_id", UUID.class),
                rs.getObject("vehicle_id", UUID.class),
                rs.getObject("supplier_id", UUID.class),
                rs.getObject("hiredvehicle_id", UUID.class),
                rs.getString("certificate_number"),
                rs.getString("issuing_center"),
                issueDate == null ? null : issueDate.toLocalDate(),
                expiryDate == null ? null : expiryDate.toLocalDate(),
                rs.getBigDecimal("co_emission_percent"),
                rs.getBigDecimal("hc_emission_ppm"),
                rs.getString("test_result"),
                rs.getString("puc_status"),
                rs.getObject("renewal_reminder_days", Integer.class),
                rs.getObject("is_current", Boolean.class),
                createdAtTs == null ? null : createdAtTs.toInstant(),
                updatedAtTs == null ? null : updatedAtTs.toInstant(),
                rs.getString("company_name"),
                rs.getString("HiredVehicle_key_number"),
                rs.getString("companyhired_vehicle_registration_number"),
                rs.getString("HiredVehicle_chassis_number")
        );
    }

    private String selectColumns() {
        return """
                vp.puc_id,
                vp.vehicle_id,
                vp.supplier_id,
                vp.hiredvehicle_id,
                vp.certificate_number,
                vp.issuing_center,
                vp.issue_date,
                vp.expiry_date,
                vp.co_emission_percent,
                vp.hc_emission_ppm,
                vp.test_result,
                vp.puc_status,
                vp.renewal_reminder_days,
                vp.is_current,
                vp.created_at,
                vp.updated_at,
                s.supplier_name AS company_name,
                hv.key_number AS HiredVehicle_key_number,
                hv.registration_number AS companyhired_vehicle_registration_number,
                hv.chassis_number AS HiredVehicle_chassis_number
                """;
    }

    private String fromJoinClause() {
        return """
                FROM hired_vehicle_puc vp
                LEFT JOIN supplier s ON s.supplier_id = vp.supplier_id
                LEFT JOIN hired_vehicles hv ON hv.hiredvehicle_id = vp.hiredvehicle_id
                """;
    }
}



