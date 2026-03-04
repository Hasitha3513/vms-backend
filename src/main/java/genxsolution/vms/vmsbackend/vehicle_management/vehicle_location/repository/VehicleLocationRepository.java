package genxsolution.vms.vmsbackend.vehicle_management.vehicle_location.repository;

import genxsolution.vms.vmsbackend.vehicle_management.vehicle_location.dto.VehicleLocationUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_location.model.VehicleLocation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class VehicleLocationRepository {
    private final JdbcTemplate jdbcTemplate;

    public VehicleLocationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<VehicleLocation> findAll(UUID companyId, UUID vehicleId) {
        String sql = """
                SELECT location_id, vehicle_id, company_id, branch_id, department_id, location_type, location_name,
                       address_line1, address_line2, city, state, country, pin_code, assigned_zone, assigned_region,
                       period_start_date, period_end_date, duration_days,
                       recorded_at, recorded_by, is_current, notes
                FROM vehicle_location
                WHERE (?::uuid IS NULL OR company_id = ?::uuid)
                  AND (?::uuid IS NULL OR vehicle_id = ?::uuid)
                ORDER BY recorded_at DESC, location_name ASC
                """;
        return jdbcTemplate.query(sql, this::mapRow, companyId, companyId, vehicleId, vehicleId);
    }

    public Optional<VehicleLocation> findById(UUID id) {
        String sql = """
                SELECT location_id, vehicle_id, company_id, branch_id, department_id, location_type, location_name,
                       address_line1, address_line2, city, state, country, pin_code, assigned_zone, assigned_region,
                       period_start_date, period_end_date, duration_days,
                       recorded_at, recorded_by, is_current, notes
                FROM vehicle_location
                WHERE location_id = ?
                """;
        return jdbcTemplate.query(sql, this::mapRow, id).stream().findFirst();
    }

    public VehicleLocation create(VehicleLocationUpsertRequest r) {
        String sql = """
                INSERT INTO vehicle_location (
                    vehicle_id, company_id, branch_id, department_id, location_type, location_name,
                    address_line1, address_line2, city, state, country, pin_code, assigned_zone,
                    assigned_region, period_start_date, period_end_date, duration_days, recorded_by, is_current, notes
                ) VALUES (
                    ?, ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?, COALESCE(?, TRUE), ?
                )
                RETURNING location_id, vehicle_id, company_id, branch_id, department_id, location_type, location_name,
                          address_line1, address_line2, city, state, country, pin_code, assigned_zone, assigned_region,
                          period_start_date, period_end_date, duration_days,
                          recorded_at, recorded_by, is_current, notes
                """;
        return jdbcTemplate.queryForObject(
                sql,
                this::mapRow,
                r.vehicleId(),
                r.companyId(),
                r.branchId(),
                r.departmentId(),
                r.locationType(),
                r.locationName(),
                r.addressLine1(),
                r.addressLine2(),
                r.city(),
                r.state(),
                r.country(),
                r.pinCode(),
                r.assignedZone(),
                r.assignedRegion(),
                r.periodStartDate(),
                r.periodEndDate(),
                calculateDurationDays(r.periodStartDate(), r.periodEndDate()),
                r.recordedBy(),
                r.isCurrent(),
                r.notes()
        );
    }

    public Optional<VehicleLocation> update(UUID id, VehicleLocationUpsertRequest r) {
        String sql = """
                UPDATE vehicle_location
                SET vehicle_id = COALESCE(?, vehicle_id),
                    company_id = COALESCE(?, company_id),
                    branch_id = COALESCE(?, branch_id),
                    department_id = COALESCE(?, department_id),
                    location_type = COALESCE(?, location_type),
                    location_name = COALESCE(?, location_name),
                    address_line1 = COALESCE(?, address_line1),
                    address_line2 = COALESCE(?, address_line2),
                    city = COALESCE(?, city),
                    state = COALESCE(?, state),
                    country = COALESCE(?, country),
                    pin_code = COALESCE(?, pin_code),
                    assigned_zone = COALESCE(?, assigned_zone),
                    assigned_region = COALESCE(?, assigned_region),
                    period_start_date = COALESCE(?, period_start_date),
                    period_end_date = COALESCE(?, period_end_date),
                    duration_days = COALESCE(?, duration_days),
                    recorded_by = COALESCE(?, recorded_by),
                    is_current = COALESCE(?, is_current),
                    notes = COALESCE(?, notes)
                WHERE location_id = ?
                RETURNING location_id, vehicle_id, company_id, branch_id, department_id, location_type, location_name,
                          address_line1, address_line2, city, state, country, pin_code, assigned_zone, assigned_region,
                          period_start_date, period_end_date, duration_days,
                          recorded_at, recorded_by, is_current, notes
                """;
        return jdbcTemplate.query(
                sql,
                this::mapRow,
                r.vehicleId(),
                r.companyId(),
                r.branchId(),
                r.departmentId(),
                r.locationType(),
                r.locationName(),
                r.addressLine1(),
                r.addressLine2(),
                r.city(),
                r.state(),
                r.country(),
                r.pinCode(),
                r.assignedZone(),
                r.assignedRegion(),
                r.periodStartDate(),
                r.periodEndDate(),
                calculateDurationDays(r.periodStartDate(), r.periodEndDate()),
                r.recordedBy(),
                r.isCurrent(),
                r.notes(),
                id
        ).stream().findFirst();
    }

    public boolean delete(UUID id) {
        return jdbcTemplate.update("DELETE FROM vehicle_location WHERE location_id = ?", id) > 0;
    }

    private VehicleLocation mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp recordedAtTs = rs.getTimestamp("recorded_at");
        return new VehicleLocation(
                rs.getObject("location_id", UUID.class),
                rs.getObject("vehicle_id", UUID.class),
                rs.getObject("company_id", UUID.class),
                rs.getObject("branch_id", UUID.class),
                rs.getObject("department_id", UUID.class),
                rs.getString("location_type"),
                rs.getString("location_name"),
                rs.getString("address_line1"),
                rs.getString("address_line2"),
                rs.getString("city"),
                rs.getString("state"),
                rs.getString("country"),
                rs.getString("pin_code"),
                rs.getString("assigned_zone"),
                rs.getString("assigned_region"),
                rs.getDate("period_start_date") == null ? null : rs.getDate("period_start_date").toLocalDate(),
                rs.getDate("period_end_date") == null ? null : rs.getDate("period_end_date").toLocalDate(),
                rs.getObject("duration_days", Integer.class),
                recordedAtTs == null ? null : recordedAtTs.toInstant(),
                rs.getObject("recorded_by", UUID.class),
                rs.getObject("is_current", Boolean.class),
                rs.getString("notes")
        );
    }

    private Integer calculateDurationDays(java.time.LocalDate start, java.time.LocalDate end) {
        if (start == null) return null;
        java.time.LocalDate effectiveEnd = end != null ? end : java.time.LocalDate.now();
        long days = java.time.temporal.ChronoUnit.DAYS.between(start, effectiveEnd) + 1;
        if (days < 1) return 1;
        return (int) days;
    }
}
