package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_registration.repository;

import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_registration.dto.HiredVehicleRegistrationUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_registration.model.HiredVehicleRegistration;
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
public class HiredVehicleRegistrationRepository {
    private final JdbcTemplate jdbcTemplate;

    public HiredVehicleRegistrationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<HiredVehicleRegistration> findAll(UUID supplierId, UUID hiredVehicleId) {
        StringBuilder sql = new StringBuilder("SELECT " + selectColumns() + " " + fromJoinClause() + " WHERE 1=1 ");
        if (supplierId != null) sql.append(" AND vr.supplier_id = ? ");
        if (hiredVehicleId != null) sql.append(" AND vr.hiredvehicle_id = ? ");
        sql.append(" ORDER BY vr.updated_at DESC, vr.created_at DESC");

        if (supplierId != null && hiredVehicleId != null) return jdbcTemplate.query(sql.toString(), this::mapRow, supplierId, hiredVehicleId);
        if (supplierId != null) return jdbcTemplate.query(sql.toString(), this::mapRow, supplierId);
        if (hiredVehicleId != null) return jdbcTemplate.query(sql.toString(), this::mapRow, hiredVehicleId);
        return jdbcTemplate.query(sql.toString(), this::mapRow);
    }

    public Optional<HiredVehicleRegistration> findById(UUID id) {
        return jdbcTemplate.query("SELECT " + selectColumns() + " " + fromJoinClause() + " WHERE vr.registration_id = ?", this::mapRow, id)
                .stream().findFirst();
    }

    public HiredVehicleRegistration create(HiredVehicleRegistrationUpsertRequest r) {
        String sql = """
                INSERT INTO hired_vehicle_registration (
                    vehicle_id, supplier_id, hiredvehicle_id, registration_number,
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
                r.vehicleId(), r.supplierId(), r.hiredVehicleId(), r.registrationNumber(),
                r.registrationDate(), r.registrationExpiry(), r.registeringAuthority(),
                r.registrationState(), r.registrationCity(), r.rcBookNumber(), r.rcStatus(),
                r.numberPlateTypeId(), r.renewalReminderDays(), r.notes(), r.isCurrent()
        ).stream().findFirst().orElseThrow(() -> new IllegalStateException("Failed to create Vehicle Registration"));

        return findById(id).orElseThrow(() -> new IllegalStateException("Created Vehicle Registration could not be loaded"));
    }

    public Optional<HiredVehicleRegistration> update(UUID id, HiredVehicleRegistrationUpsertRequest r) {
        String sql = """
                UPDATE hired_vehicle_registration
                SET vehicle_id = COALESCE(?, vehicle_id),
                    supplier_id = COALESCE(?, supplier_id),
                    hiredvehicle_id = COALESCE(?, hiredvehicle_id),
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
                r.vehicleId(), r.supplierId(), r.hiredVehicleId(), r.registrationNumber(),
                r.registrationDate(), r.registrationExpiry(), r.registeringAuthority(),
                r.registrationState(), r.registrationCity(), r.rcBookNumber(), r.rcStatus(),
                r.numberPlateTypeId(), r.renewalReminderDays(), r.notes(), r.isCurrent(),
                id
        ).stream().findFirst().flatMap(this::findById);
    }

    public boolean delete(UUID id) {
        return jdbcTemplate.update("DELETE FROM hired_vehicle_registration WHERE registration_id = ?", id) > 0;
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

    public Optional<String> findHiredVehicleRegistrationNumberByHiredVehicleId(UUID hiredVehicleId) {
        return jdbcTemplate.query(
                "SELECT registration_number FROM hired_vehicles WHERE hiredvehicle_id = ?",
                (rs, i) -> rs.getString("registration_number"),
                hiredVehicleId
        ).stream().filter(Objects::nonNull).findFirst();
    }

    public Optional<HiredVehicleRegistration> findLatestByHiredVehicleId(UUID hiredVehicleId) {
        return jdbcTemplate.query(
                "SELECT " + selectColumns() + " " + fromJoinClause() + " WHERE vr.hiredvehicle_id = ? ORDER BY vr.updated_at DESC, vr.created_at DESC LIMIT 1",
                this::mapRow,
                hiredVehicleId
        ).stream().findFirst();
    }

    public int clearCurrentForHiredVehicle(UUID hiredVehicleId, UUID exceptRegistrationId) {
        return jdbcTemplate.update(
                """
                UPDATE hired_vehicle_registration
                SET is_current = FALSE
                WHERE hiredvehicle_id = ?
                  AND (? IS NULL OR registration_id <> ?)
                """,
                hiredVehicleId,
                exceptRegistrationId,
                exceptRegistrationId
        );
    }

    public int updateCompanyHiredVehicleRegistrationNumber(UUID hiredVehicleId, String registrationNumber) {
        return jdbcTemplate.update(
                "UPDATE hired_vehicles SET registration_number = ?, updated_at = NOW() WHERE hiredvehicle_id = ?",
                registrationNumber,
                hiredVehicleId
        );
    }

    private HiredVehicleRegistration mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        Date registrationDate = rs.getDate("registration_date");
        Date registrationExpiry = rs.getDate("registration_expiry");
        return new HiredVehicleRegistration(
                rs.getObject("registration_id", UUID.class),
                rs.getObject("vehicle_id", UUID.class),
                rs.getObject("supplier_id", UUID.class),
                rs.getObject("hiredvehicle_id", UUID.class),
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
                rs.getString("HiredVehicle_key_number"),
                rs.getString("companyhired_vehicle_registration_number"),
                rs.getString("HiredVehicle_chassis_number")
        );
    }

    private String selectColumns() {
        return """
                vr.registration_id,
                vr.vehicle_id,
                vr.supplier_id,
                vr.hiredvehicle_id,
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
                s.supplier_name AS company_name,
                hv.key_number AS HiredVehicle_key_number,
                hv.registration_number AS companyhired_vehicle_registration_number,
                hv.chassis_number AS HiredVehicle_chassis_number
                """;
    }

    private String fromJoinClause() {
        return """
                FROM hired_vehicle_registration vr
                LEFT JOIN supplier s ON s.supplier_id = vr.supplier_id
                LEFT JOIN hired_vehicles hv ON hv.hiredvehicle_id = vr.hiredvehicle_id
                """;
    }
}



