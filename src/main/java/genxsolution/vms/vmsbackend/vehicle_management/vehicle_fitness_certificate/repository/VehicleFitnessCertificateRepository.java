package genxsolution.vms.vmsbackend.vehicle_management.vehicle_fitness_certificate.repository;

import genxsolution.vms.vmsbackend.vehicle_management.vehicle_fitness_certificate.dto.VehicleFitnessCertificateUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_fitness_certificate.model.VehicleFitnessCertificate;
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
public class VehicleFitnessCertificateRepository {
    private final JdbcTemplate jdbcTemplate;

    public VehicleFitnessCertificateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<VehicleFitnessCertificate> findAll(UUID companyId, UUID companyVehicleId) {
        StringBuilder sql = new StringBuilder("SELECT " + selectColumns() + " " + fromJoinClause() + " WHERE 1=1 ");
        if (companyId != null) sql.append(" AND vf.company_id = ? ");
        if (companyVehicleId != null) sql.append(" AND vf.companyvehicle_id = ? ");
        sql.append(" ORDER BY vf.updated_at DESC, vf.created_at DESC");

        if (companyId != null && companyVehicleId != null) return jdbcTemplate.query(sql.toString(), this::mapRow, companyId, companyVehicleId);
        if (companyId != null) return jdbcTemplate.query(sql.toString(), this::mapRow, companyId);
        if (companyVehicleId != null) return jdbcTemplate.query(sql.toString(), this::mapRow, companyVehicleId);
        return jdbcTemplate.query(sql.toString(), this::mapRow);
    }

    public Optional<VehicleFitnessCertificate> findById(UUID id) {
        return jdbcTemplate.query("SELECT " + selectColumns() + " " + fromJoinClause() + " WHERE vf.fitness_id = ?", this::mapRow, id)
                .stream().findFirst();
    }

    public VehicleFitnessCertificate create(VehicleFitnessCertificateUpsertRequest r) {
        String sql = """
                INSERT INTO vehicle_fitness_certificate (
                    vehicle_id, company_id, companyvehicle_id, certificate_number,
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
                r.vehicleId(), r.companyId(), r.companyVehicleId(), r.certificateNumber(),
                r.issuingAuthority(), r.inspectionCenter(), r.inspectorId(), r.inspectorName(),
                r.issueDate(), r.expiryDate(), r.validityDurationYears(), r.inspectionResultId(),
                r.remarks(), r.renewalReminderDays(), r.fitnessStatus(), r.isCurrent()
        ).stream().findFirst().orElseThrow(() -> new IllegalStateException("Failed to create Vehicle Fitness Certificate"));

        return findById(id).orElseThrow(() -> new IllegalStateException("Created Vehicle Fitness Certificate could not be loaded"));
    }

    public Optional<VehicleFitnessCertificate> update(UUID id, VehicleFitnessCertificateUpsertRequest r) {
        String sql = """
                UPDATE vehicle_fitness_certificate
                SET vehicle_id = COALESCE(?, vehicle_id),
                    company_id = COALESCE(?, company_id),
                    companyvehicle_id = COALESCE(?, companyvehicle_id),
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
                r.vehicleId(), r.companyId(), r.companyVehicleId(), r.certificateNumber(),
                r.issuingAuthority(), r.inspectionCenter(), r.inspectorId(), r.inspectorName(),
                r.issueDate(), r.expiryDate(), r.validityDurationYears(), r.inspectionResultId(),
                r.remarks(), r.renewalReminderDays(), r.fitnessStatus(), r.isCurrent(),
                id
        ).stream().findFirst().flatMap(this::findById);
    }

    public boolean delete(UUID id) {
        return jdbcTemplate.update("DELETE FROM vehicle_fitness_certificate WHERE fitness_id = ?", id) > 0;
    }

    public boolean companyVehicleBelongsToCompany(UUID companyVehicleId, UUID companyId) {
        List<Integer> rows = jdbcTemplate.query(
                "SELECT 1 FROM company_vehicles WHERE companyvehicle_id = ? AND company_id = ? LIMIT 1",
                (rs, i) -> rs.getInt(1),
                companyVehicleId,
                companyId
        );
        return !rows.isEmpty();
    }

    public Optional<UUID> findVehicleIdByCompanyVehicleId(UUID companyVehicleId) {
        return jdbcTemplate.query(
                """
                SELECT v.vehicle_id
                FROM company_vehicles cv
                LEFT JOIN vehicle v
                  ON (
                       v.chassis_number = cv.chassis_number
                    OR (
                         cv.registration_number IS NOT NULL
                         AND v.registration_number = cv.registration_number
                       )
                  )
                WHERE cv.companyvehicle_id = ?
                ORDER BY v.updated_at DESC NULLS LAST, v.created_at DESC NULLS LAST
                LIMIT 1
                """,
                (rs, i) -> rs.getObject("vehicle_id", UUID.class),
                companyVehicleId
        ).stream().findFirst();
    }

    public Optional<UUID> findCompanyIdByCompanyVehicleId(UUID companyVehicleId) {
        return jdbcTemplate.query(
                "SELECT company_id FROM company_vehicles WHERE companyvehicle_id = ?",
                (rs, i) -> rs.getObject("company_id", UUID.class),
                companyVehicleId
        ).stream().findFirst();
    }

    public Optional<VehicleFitnessCertificate> findLatestByCompanyVehicleId(UUID companyVehicleId) {
        return jdbcTemplate.query(
                "SELECT " + selectColumns() + " " + fromJoinClause() + " WHERE vf.companyvehicle_id = ? ORDER BY vf.updated_at DESC, vf.created_at DESC LIMIT 1",
                this::mapRow,
                companyVehicleId
        ).stream().findFirst();
    }

    public int clearCurrentForCompanyVehicle(UUID companyVehicleId, UUID exceptFitnessId) {
        return jdbcTemplate.update(
                """
                UPDATE vehicle_fitness_certificate
                SET is_current = FALSE
                WHERE companyvehicle_id = ?
                  AND (? IS NULL OR fitness_id <> ?)
                """,
                companyVehicleId,
                exceptFitnessId,
                exceptFitnessId
        );
    }

    private VehicleFitnessCertificate mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        Date issueDate = rs.getDate("issue_date");
        Date expiryDate = rs.getDate("expiry_date");

        return new VehicleFitnessCertificate(
                rs.getObject("fitness_id", UUID.class),
                rs.getObject("vehicle_id", UUID.class),
                rs.getObject("company_id", UUID.class),
                rs.getObject("companyvehicle_id", UUID.class),
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
                rs.getString("companyvehicle_key_number"),
                rs.getString("companyvehicle_registration_number"),
                rs.getString("companyvehicle_chassis_number")
        );
    }

    private String selectColumns() {
        return """
                vf.fitness_id,
                vf.vehicle_id,
                vf.company_id,
                vf.companyvehicle_id,
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
                c.company_name,
                cv.key_number AS companyvehicle_key_number,
                cv.registration_number AS companyvehicle_registration_number,
                cv.chassis_number AS companyvehicle_chassis_number
                """;
    }

    private String fromJoinClause() {
        return """
                FROM vehicle_fitness_certificate vf
                LEFT JOIN company c ON c.company_id = vf.company_id
                LEFT JOIN company_vehicles cv ON cv.companyvehicle_id = vf.companyvehicle_id
                """;
    }
}
