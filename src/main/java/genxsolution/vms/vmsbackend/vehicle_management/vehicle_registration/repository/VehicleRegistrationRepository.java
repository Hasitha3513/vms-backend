package genxsolution.vms.vmsbackend.vehicle_management.vehicle_registration.repository;

import genxsolution.vms.vmsbackend.vehicle_management.vehicle_registration.dto.VehicleRegistrationUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_registration.model.VehicleRegistration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
public class VehicleRegistrationRepository {
    private final JdbcTemplate jdbcTemplate;

    public VehicleRegistrationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<VehicleRegistration> findAll(UUID companyId, UUID companyVehicleId) {
        StringBuilder sql = new StringBuilder("SELECT " + selectColumns() + " " + fromJoinClause() + " WHERE 1=1 ");
        if (companyId != null) sql.append(" AND vr.company_id = ? ");
        if (companyVehicleId != null) sql.append(" AND vr.companyvehicle_id = ? ");
        sql.append(" ORDER BY vr.updated_at DESC, vr.created_at DESC");

        if (companyId != null && companyVehicleId != null) return jdbcTemplate.query(sql.toString(), this::mapRow, companyId, companyVehicleId);
        if (companyId != null) return jdbcTemplate.query(sql.toString(), this::mapRow, companyId);
        if (companyVehicleId != null) return jdbcTemplate.query(sql.toString(), this::mapRow, companyVehicleId);
        return jdbcTemplate.query(sql.toString(), this::mapRow);
    }

    public Optional<VehicleRegistration> findById(UUID id) {
        return jdbcTemplate.query("SELECT " + selectColumns() + " " + fromJoinClause() + " WHERE vr.registration_id = ?", this::mapRow, id)
                .stream().findFirst();
    }

    public VehicleRegistration create(VehicleRegistrationUpsertRequest r) {
        String sql = """
                INSERT INTO vehicle_registration (
                    vehicle_id, company_id, companyvehicle_id, registration_number,
                    registration_date, registration_expiry, registering_authority,
                    registration_state, registration_city, rc_book_number, rc_status,
                    number_plate_type_id, renewal_reminder_days, notes, is_current
                ) VALUES (
                    ?, ?, ?, ?,
                    ?, ?, ?,
                    ?, ?, ?, ?,
                    ?, COALESCE(?, 30), ?, COALESCE(?, TRUE)
                )
                RETURNING registration_id
                """;

        UUID id = jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getObject("registration_id", UUID.class),
                r.vehicleId(), r.companyId(), r.companyVehicleId(), r.registrationNumber(),
                r.registrationDate(), r.registrationExpiry(), r.registeringAuthority(),
                r.registrationState(), r.registrationCity(), r.rcBookNumber(), r.rcStatus(),
                r.numberPlateTypeId(), r.renewalReminderDays(), r.notes(), r.isCurrent()
        ).stream().findFirst().orElseThrow(() -> new IllegalStateException("Failed to create Vehicle Registration"));

        return findById(id).orElseThrow(() -> new IllegalStateException("Created Vehicle Registration could not be loaded"));
    }

    public Optional<VehicleRegistration> update(UUID id, VehicleRegistrationUpsertRequest r) {
        String sql = """
                UPDATE vehicle_registration
                SET vehicle_id = COALESCE(?, vehicle_id),
                    company_id = COALESCE(?, company_id),
                    companyvehicle_id = COALESCE(?, companyvehicle_id),
                    registration_number = COALESCE(?, registration_number),
                    registration_date = COALESCE(?, registration_date),
                    registration_expiry = COALESCE(?, registration_expiry),
                    registering_authority = COALESCE(?, registering_authority),
                    registration_state = COALESCE(?, registration_state),
                    registration_city = COALESCE(?, registration_city),
                    rc_book_number = COALESCE(?, rc_book_number),
                    rc_status = COALESCE(?, rc_status),
                    number_plate_type_id = COALESCE(?, number_plate_type_id),
                    renewal_reminder_days = COALESCE(?, renewal_reminder_days),
                    notes = COALESCE(?, notes),
                    is_current = COALESCE(?, is_current)
                WHERE registration_id = ?
                RETURNING registration_id
                """;

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getObject("registration_id", UUID.class),
                r.vehicleId(), r.companyId(), r.companyVehicleId(), r.registrationNumber(),
                r.registrationDate(), r.registrationExpiry(), r.registeringAuthority(),
                r.registrationState(), r.registrationCity(), r.rcBookNumber(), r.rcStatus(),
                r.numberPlateTypeId(), r.renewalReminderDays(), r.notes(), r.isCurrent(),
                id
        ).stream().findFirst().flatMap(this::findById);
    }

    public boolean delete(UUID id) {
        return jdbcTemplate.update("DELETE FROM vehicle_registration WHERE registration_id = ?", id) > 0;
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

    public Optional<String> findVehicleRegistrationNumberByCompanyVehicleId(UUID companyVehicleId) {
        return jdbcTemplate.query(
                "SELECT registration_number FROM company_vehicles WHERE companyvehicle_id = ?",
                (rs, i) -> rs.getString("registration_number"),
                companyVehicleId
        ).stream().filter(Objects::nonNull).findFirst();
    }

    public Optional<VehicleRegistration> findLatestByCompanyVehicleId(UUID companyVehicleId) {
        return jdbcTemplate.query(
                "SELECT " + selectColumns() + " " + fromJoinClause() + " WHERE vr.companyvehicle_id = ? ORDER BY vr.updated_at DESC, vr.created_at DESC LIMIT 1",
                this::mapRow,
                companyVehicleId
        ).stream().findFirst();
    }

    public int clearCurrentForCompanyVehicle(UUID companyVehicleId, UUID exceptRegistrationId) {
        return jdbcTemplate.update(
                """
                UPDATE vehicle_registration
                SET is_current = FALSE
                WHERE companyvehicle_id = ?
                  AND (? IS NULL OR registration_id <> ?)
                """,
                companyVehicleId,
                exceptRegistrationId,
                exceptRegistrationId
        );
    }

    public int updateCompanyVehicleRegistrationNumber(UUID companyVehicleId, String registrationNumber) {
        return jdbcTemplate.update(
                "UPDATE company_vehicles SET registration_number = ?, updated_at = NOW() WHERE companyvehicle_id = ?",
                registrationNumber,
                companyVehicleId
        );
    }

    private VehicleRegistration mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        Date registrationDate = rs.getDate("registration_date");
        Date registrationExpiry = rs.getDate("registration_expiry");
        return new VehicleRegistration(
                rs.getObject("registration_id", UUID.class),
                rs.getObject("vehicle_id", UUID.class),
                rs.getObject("company_id", UUID.class),
                rs.getObject("companyvehicle_id", UUID.class),
                rs.getString("registration_number"),
                registrationDate == null ? null : registrationDate.toLocalDate(),
                registrationExpiry == null ? null : registrationExpiry.toLocalDate(),
                rs.getString("registering_authority"),
                rs.getString("registration_state"),
                rs.getString("registration_city"),
                rs.getString("rc_book_number"),
                rs.getString("rc_status"),
                rs.getObject("number_plate_type_id", Integer.class),
                rs.getObject("renewal_reminder_days", Integer.class),
                rs.getString("notes"),
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
                vr.registration_id,
                vr.vehicle_id,
                vr.company_id,
                vr.companyvehicle_id,
                vr.registration_number,
                vr.registration_date,
                vr.registration_expiry,
                vr.registering_authority,
                vr.registration_state,
                vr.registration_city,
                vr.rc_book_number,
                vr.rc_status,
                vr.number_plate_type_id,
                vr.renewal_reminder_days,
                vr.notes,
                vr.is_current,
                vr.created_at,
                vr.updated_at,
                c.company_name,
                cv.key_number AS companyvehicle_key_number,
                cv.registration_number AS companyvehicle_registration_number,
                cv.chassis_number AS companyvehicle_chassis_number
                """;
    }

    private String fromJoinClause() {
        return """
                FROM vehicle_registration vr
                LEFT JOIN company c ON c.company_id = vr.company_id
                LEFT JOIN company_vehicles cv ON cv.companyvehicle_id = vr.companyvehicle_id
                """;
    }
}
