package genxsolution.vms.vmsbackend.vehicle_management.vehicle_type.repository;

import genxsolution.vms.vmsbackend.vehicle_management.vehicle_type.dto.VehicleTypeUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_type.model.VehicleType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class VehicleTypeRepository {
    private final JdbcTemplate jdbcTemplate;

    public VehicleTypeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<VehicleType> findAll() {
        String sql = "SELECT type_id, category_id, type_name, body_style_id, fuel_type_id, undercarriage_type_id, number_of_wheels, seating_capacity_min, seating_capacity_max, usage_type, service_interval_km, service_interval_months, service_interval_hours, oil_change_interval_km, description, is_active, created_at, updated_at FROM vehicle_type ORDER BY type_name";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    public Optional<VehicleType> findById(UUID id) {
        return jdbcTemplate.query(
                        "SELECT type_id, category_id, type_name, body_style_id, fuel_type_id, undercarriage_type_id, number_of_wheels, seating_capacity_min, seating_capacity_max, usage_type, service_interval_km, service_interval_months, service_interval_hours, oil_change_interval_km, description, is_active, created_at, updated_at FROM vehicle_type WHERE type_id = ?",
                        this::mapRow,
                        id
                )
                .stream()
                .findFirst();
    }

    public VehicleType create(VehicleTypeUpsertRequest r) {
        String sql = """
                INSERT INTO vehicle_type (category_id, type_name, body_style_id, fuel_type_id, undercarriage_type_id, number_of_wheels, seating_capacity_min, seating_capacity_max, usage_type, service_interval_km, service_interval_months, service_interval_hours, oil_change_interval_km, description, is_active)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING type_id, category_id, type_name, body_style_id, fuel_type_id, undercarriage_type_id, number_of_wheels, seating_capacity_min, seating_capacity_max, usage_type, service_interval_km, service_interval_months, service_interval_hours, oil_change_interval_km, description, is_active, created_at, updated_at
                """;
        if (r.categoryId() == null) {
            throw new IllegalArgumentException("Category is required");
        }
        boolean isActive = r.isActive() == null || r.isActive();
        return jdbcTemplate.queryForObject(
                sql,
                this::mapRow,
                r.categoryId(),
                r.typeName(),
                r.bodyStyleId(),
                r.fuelTypeId(),
                r.undercarriageTypeId(),
                r.numberOfWheels(),
                r.seatingCapacityMin(),
                r.seatingCapacityMax(),
                r.usageType(),
                r.serviceIntervalKm(),
                r.serviceIntervalMonths(),
                r.serviceIntervalHours(),
                r.oilChangeIntervalKm(),
                r.description(),
                isActive
        );
    }

    public Optional<VehicleType> update(UUID id, VehicleTypeUpsertRequest r) {
        String sql = """
                UPDATE vehicle_type
                SET category_id = COALESCE(?, category_id),
                    type_name = COALESCE(?, type_name),
                    body_style_id = COALESCE(?, body_style_id),
                    fuel_type_id = COALESCE(?, fuel_type_id),
                    undercarriage_type_id = COALESCE(?, undercarriage_type_id),
                    number_of_wheels = COALESCE(?, number_of_wheels),
                    seating_capacity_min = COALESCE(?, seating_capacity_min),
                    seating_capacity_max = COALESCE(?, seating_capacity_max),
                    usage_type = COALESCE(?, usage_type),
                    service_interval_km = COALESCE(?, service_interval_km),
                    service_interval_months = COALESCE(?, service_interval_months),
                    service_interval_hours = COALESCE(?, service_interval_hours),
                    oil_change_interval_km = COALESCE(?, oil_change_interval_km),
                    description = COALESCE(?, description),
                    is_active = COALESCE(?, is_active)
                WHERE type_id = ?
                RETURNING type_id, category_id, type_name, body_style_id, fuel_type_id, undercarriage_type_id, number_of_wheels, seating_capacity_min, seating_capacity_max, usage_type, service_interval_km, service_interval_months, service_interval_hours, oil_change_interval_km, description, is_active, created_at, updated_at
                """;
        return jdbcTemplate.query(
                        sql,
                        this::mapRow,
                        r.categoryId(),
                        r.typeName(),
                        r.bodyStyleId(),
                        r.fuelTypeId(),
                        r.undercarriageTypeId(),
                        r.numberOfWheels(),
                        r.seatingCapacityMin(),
                        r.seatingCapacityMax(),
                        r.usageType(),
                        r.serviceIntervalKm(),
                        r.serviceIntervalMonths(),
                        r.serviceIntervalHours(),
                        r.oilChangeIntervalKm(),
                        r.description(),
                        r.isActive(),
                        id
                )
                .stream()
                .findFirst();
    }

    public boolean delete(UUID id) {
        return jdbcTemplate.update("DELETE FROM vehicle_type WHERE type_id = ?", id) > 0;
    }

    private VehicleType mapRow(ResultSet rs, int n) throws SQLException {
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        return new VehicleType(
                rs.getObject("type_id", UUID.class),
                rs.getObject("category_id", UUID.class),
                rs.getString("type_name"),
                rs.getObject("body_style_id", Integer.class),
                rs.getObject("fuel_type_id", Integer.class),
                rs.getObject("undercarriage_type_id", Integer.class),
                rs.getObject("number_of_wheels", Integer.class),
                rs.getObject("seating_capacity_min", Integer.class),
                rs.getObject("seating_capacity_max", Integer.class),
                rs.getString("usage_type"),
                rs.getObject("service_interval_km", Integer.class),
                rs.getObject("service_interval_months", Integer.class),
                rs.getObject("service_interval_hours", Integer.class),
                rs.getObject("oil_change_interval_km", Integer.class),
                rs.getString("description"),
                rs.getObject("is_active", Boolean.class),
                createdAtTs == null ? null : createdAtTs.toInstant(),
                updatedAtTs == null ? null : updatedAtTs.toInstant()
        );
    }
}
