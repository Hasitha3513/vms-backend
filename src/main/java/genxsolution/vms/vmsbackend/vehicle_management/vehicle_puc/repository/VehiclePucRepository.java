package genxsolution.vms.vmsbackend.vehicle_management.vehicle_puc.repository;

import genxsolution.vms.vmsbackend.vehicle_management.vehicle_puc.dto.VehiclePucUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_puc.model.VehiclePuc;
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
public class VehiclePucRepository {
    private final JdbcTemplate jdbcTemplate;

    public VehiclePucRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<VehiclePuc> findAll(UUID companyId, UUID companyVehicleId) {
        StringBuilder sql = new StringBuilder("SELECT " + selectColumns() + " " + fromJoinClause() + " WHERE 1=1 ");
        if (companyId != null) sql.append(" AND vp.company_id = ? ");
        if (companyVehicleId != null) sql.append(" AND vp.companyvehicle_id = ? ");
        sql.append(" ORDER BY vp.updated_at DESC, vp.created_at DESC");

        if (companyId != null && companyVehicleId != null) return jdbcTemplate.query(sql.toString(), this::mapRow, companyId, companyVehicleId);
        if (companyId != null) return jdbcTemplate.query(sql.toString(), this::mapRow, companyId);
        if (companyVehicleId != null) return jdbcTemplate.query(sql.toString(), this::mapRow, companyVehicleId);
        return jdbcTemplate.query(sql.toString(), this::mapRow);
    }

    public Optional<VehiclePuc> findById(UUID id) {
        return jdbcTemplate.query("SELECT " + selectColumns() + " " + fromJoinClause() + " WHERE vp.puc_id = ?", this::mapRow, id)
                .stream().findFirst();
    }

    public VehiclePuc create(VehiclePucUpsertRequest r) {
        String sql = """
                INSERT INTO vehicle_puc (
                    vehicle_id, company_id, companyvehicle_id, certificate_number,
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
                r.vehicleId(), r.companyId(), r.companyVehicleId(), r.certificateNumber(),
                r.issuingCenter(), r.issueDate(), r.expiryDate(), r.coEmissionPercent(),
                r.hcEmissionPpm(), r.testResult(), r.pucStatus(), r.renewalReminderDays(), r.isCurrent()
        ).stream().findFirst().orElseThrow(() -> new IllegalStateException("Failed to create Vehicle PUC"));

        return findById(id).orElseThrow(() -> new IllegalStateException("Created Vehicle PUC could not be loaded"));
    }

    public Optional<VehiclePuc> update(UUID id, VehiclePucUpsertRequest r) {
        String sql = """
                UPDATE vehicle_puc
                SET vehicle_id = COALESCE(?, vehicle_id),
                    company_id = COALESCE(?, company_id),
                    companyvehicle_id = COALESCE(?, companyvehicle_id),
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
                r.vehicleId(), r.companyId(), r.companyVehicleId(), r.certificateNumber(),
                r.issuingCenter(), r.issueDate(), r.expiryDate(), r.coEmissionPercent(),
                r.hcEmissionPpm(), r.testResult(), r.pucStatus(), r.renewalReminderDays(), r.isCurrent(),
                id
        ).stream().findFirst().flatMap(this::findById);
    }

    public boolean delete(UUID id) {
        return jdbcTemplate.update("DELETE FROM vehicle_puc WHERE puc_id = ?", id) > 0;
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

    public Optional<VehiclePuc> findLatestByCompanyVehicleId(UUID companyVehicleId) {
        return jdbcTemplate.query(
                "SELECT " + selectColumns() + " " + fromJoinClause() + " WHERE vp.companyvehicle_id = ? ORDER BY vp.updated_at DESC, vp.created_at DESC LIMIT 1",
                this::mapRow,
                companyVehicleId
        ).stream().findFirst();
    }

    public int clearCurrentForCompanyVehicle(UUID companyVehicleId, UUID exceptPucId) {
        return jdbcTemplate.update(
                """
                UPDATE vehicle_puc
                SET is_current = FALSE
                WHERE companyvehicle_id = ?
                  AND (? IS NULL OR puc_id <> ?)
                """,
                companyVehicleId,
                exceptPucId,
                exceptPucId
        );
    }

    private VehiclePuc mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        Date issueDate = rs.getDate("issue_date");
        Date expiryDate = rs.getDate("expiry_date");

        return new VehiclePuc(
                rs.getObject("puc_id", UUID.class),
                rs.getObject("vehicle_id", UUID.class),
                rs.getObject("company_id", UUID.class),
                rs.getObject("companyvehicle_id", UUID.class),
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
                rs.getString("companyvehicle_key_number"),
                rs.getString("companyvehicle_registration_number"),
                rs.getString("companyvehicle_chassis_number")
        );
    }

    private String selectColumns() {
        return """
                vp.puc_id,
                vp.vehicle_id,
                vp.company_id,
                vp.companyvehicle_id,
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
                c.company_name,
                cv.key_number AS companyvehicle_key_number,
                cv.registration_number AS companyvehicle_registration_number,
                cv.chassis_number AS companyvehicle_chassis_number
                """;
    }

    private String fromJoinClause() {
        return """
                FROM vehicle_puc vp
                LEFT JOIN company c ON c.company_id = vp.company_id
                LEFT JOIN company_vehicles cv ON cv.companyvehicle_id = vp.companyvehicle_id
                """;
    }
}
