package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_fitness_certificate.repository;

import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_fitness_certificate.dto.HiredVehicleFitnessCertificateUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_fitness_certificate.model.HiredVehicleFitnessCertificate;
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
public class HiredVehicleFitnessCertificateRepository {
    private final JdbcTemplate jdbcTemplate;

    public HiredVehicleFitnessCertificateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<HiredVehicleFitnessCertificate> findAll(UUID supplierId, UUID hiredVehicleId) {
        StringBuilder sql = new StringBuilder("SELECT " + selectColumns() + " " + fromJoinClause() + " WHERE 1=1 ");
        if (supplierId != null) sql.append(" AND vf.supplier_id = ? ");
        if (hiredVehicleId != null) sql.append(" AND vf.hiredvehicle_id = ? ");
        sql.append(" ORDER BY vf.updated_at DESC, vf.created_at DESC");

        if (supplierId != null && hiredVehicleId != null) return jdbcTemplate.query(sql.toString(), this::mapRow, supplierId, hiredVehicleId);
        if (supplierId != null) return jdbcTemplate.query(sql.toString(), this::mapRow, supplierId);
        if (hiredVehicleId != null) return jdbcTemplate.query(sql.toString(), this::mapRow, hiredVehicleId);
        return jdbcTemplate.query(sql.toString(), this::mapRow);
    }

    public Optional<HiredVehicleFitnessCertificate> findById(UUID id) {
        return jdbcTemplate.query("SELECT " + selectColumns() + " " + fromJoinClause() + " WHERE vf.fitness_id = ?", this::mapRow, id)
                .stream().findFirst();
    }

    public HiredVehicleFitnessCertificate create(HiredVehicleFitnessCertificateUpsertRequest r) {
        String sql = """
                INSERT INTO hired_vehicle_fitness_certificate (
                    vehicle_id, supplier_id, hiredvehicle_id, certificate_number,
                    issuing_authority, inspection_center, inspector_id, inspector_name,
                    issue_date, expiry_date, validity_duration_years, inspection_result_id,
                    remarks, renewal_reminder_days, fitness_status, is_current
                ) VALUES (
                    ?, ?, ?, ?,
                    ?, ?, ?, ?,
                    ?, ?, ?, ?,
                    ?, COALESCE(?, 30), COALESCE(?, 'Valid'), COALESCE(?, TRUE)
                )
                RETURNING fitness_id
                """;

        UUID id = jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getObject("fitness_id", UUID.class),
                r.vehicleId(), r.supplierId(), r.hiredVehicleId(), r.certificateNumber(),
                r.issuingAuthority(), r.inspectionCenter(), r.inspectorId(), r.inspectorName(),
                r.issueDate(), r.expiryDate(), r.validityDurationYears(), r.inspectionResultId(),
                r.remarks(), r.renewalReminderDays(), r.fitnessStatus(), r.isCurrent()
        ).stream().findFirst().orElseThrow(() -> new IllegalStateException("Failed to create Vehicle Fitness Certificate"));

        return findById(id).orElseThrow(() -> new IllegalStateException("Created Vehicle Fitness Certificate could not be loaded"));
    }

    public Optional<HiredVehicleFitnessCertificate> update(UUID id, HiredVehicleFitnessCertificateUpsertRequest r) {
        String sql = """
                UPDATE hired_vehicle_fitness_certificate
                SET vehicle_id = COALESCE(?, vehicle_id),
                    supplier_id = COALESCE(?, supplier_id),
                    hiredvehicle_id = COALESCE(?, hiredvehicle_id),
                    certificate_number = COALESCE(?, certificate_number),
                    issuing_authority = COALESCE(?, issuing_authority),
                    inspection_center = COALESCE(?, inspection_center),
                    inspector_id = COALESCE(?, inspector_id),
                    inspector_name = COALESCE(?, inspector_name),
                    issue_date = COALESCE(?, issue_date),
                    expiry_date = COALESCE(?, expiry_date),
                    validity_duration_years = COALESCE(?, validity_duration_years),
                    inspection_result_id = COALESCE(?, inspection_result_id),
                    remarks = COALESCE(?, remarks),
                    renewal_reminder_days = COALESCE(?, renewal_reminder_days),
                    fitness_status = COALESCE(?, fitness_status),
                    is_current = COALESCE(?, is_current)
                WHERE fitness_id = ?
                RETURNING fitness_id
                """;

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getObject("fitness_id", UUID.class),
                r.vehicleId(), r.supplierId(), r.hiredVehicleId(), r.certificateNumber(),
                r.issuingAuthority(), r.inspectionCenter(), r.inspectorId(), r.inspectorName(),
                r.issueDate(), r.expiryDate(), r.validityDurationYears(), r.inspectionResultId(),
                r.remarks(), r.renewalReminderDays(), r.fitnessStatus(), r.isCurrent(),
                id
        ).stream().findFirst().flatMap(this::findById);
    }

    public boolean delete(UUID id) {
        return jdbcTemplate.update("DELETE FROM hired_vehicle_fitness_certificate WHERE fitness_id = ?", id) > 0;
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

    public Optional<HiredVehicleFitnessCertificate> findLatestByHiredVehicleId(UUID hiredVehicleId) {
        return jdbcTemplate.query(
                "SELECT " + selectColumns() + " " + fromJoinClause() + " WHERE vf.hiredvehicle_id = ? ORDER BY vf.updated_at DESC, vf.created_at DESC LIMIT 1",
                this::mapRow,
                hiredVehicleId
        ).stream().findFirst();
    }

    public int clearCurrentForHiredVehicle(UUID hiredVehicleId, UUID exceptFitnessId) {
        return jdbcTemplate.update(
                """
                UPDATE hired_vehicle_fitness_certificate
                SET is_current = FALSE
                WHERE hiredvehicle_id = ?
                  AND (? IS NULL OR fitness_id <> ?)
                """,
                hiredVehicleId,
                exceptFitnessId,
                exceptFitnessId
        );
    }

    private HiredVehicleFitnessCertificate mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        Date issueDate = rs.getDate("issue_date");
        Date expiryDate = rs.getDate("expiry_date");

        return new HiredVehicleFitnessCertificate(
                rs.getObject("fitness_id", UUID.class),
                rs.getObject("vehicle_id", UUID.class),
                rs.getObject("supplier_id", UUID.class),
                rs.getObject("hiredvehicle_id", UUID.class),
                rs.getString("certificate_number"),
                rs.getString("issuing_authority"),
                rs.getString("inspection_center"),
                rs.getString("inspector_id"),
                rs.getString("inspector_name"),
                issueDate == null ? null : issueDate.toLocalDate(),
                expiryDate == null ? null : expiryDate.toLocalDate(),
                rs.getObject("validity_duration_years", Integer.class),
                rs.getObject("inspection_result_id", Integer.class),
                rs.getString("remarks"),
                rs.getObject("renewal_reminder_days", Integer.class),
                rs.getString("fitness_status"),
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
                vf.fitness_id,
                vf.vehicle_id,
                vf.supplier_id,
                vf.hiredvehicle_id,
                vf.certificate_number,
                vf.issuing_authority,
                vf.inspection_center,
                vf.inspector_id,
                vf.inspector_name,
                vf.issue_date,
                vf.expiry_date,
                vf.validity_duration_years,
                vf.inspection_result_id,
                vf.remarks,
                vf.renewal_reminder_days,
                vf.fitness_status,
                vf.is_current,
                vf.created_at,
                vf.updated_at,
                s.supplier_name AS company_name,
                hv.key_number AS HiredVehicle_key_number,
                hv.registration_number AS companyhired_vehicle_registration_number,
                hv.chassis_number AS HiredVehicle_chassis_number
                """;
    }

    private String fromJoinClause() {
        return """
                FROM hired_vehicle_fitness_certificate vf
                LEFT JOIN supplier s ON s.supplier_id = vf.supplier_id
                LEFT JOIN hired_vehicles hv ON hv.hiredvehicle_id = vf.hiredvehicle_id
                """;
    }
}



