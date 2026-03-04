package genxsolution.vms.vmsbackend.vehicle_management.vehicle.repository;

import genxsolution.vms.vmsbackend.vehicle_management.vehicle.dto.VehicleUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle.model.Vehicle;
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
public class VehicleRepository {
    private final JdbcTemplate jdbcTemplate;

    public VehicleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Vehicle> findAll(UUID companyId) {
        String sql = """
                SELECT *
                FROM (
                    SELECT %s
                    FROM vehicle
                ) v
                %s
                ORDER BY updated_at DESC, created_at DESC
                """.formatted(
                selectColumns(),
                companyId == null ? "" : "WHERE v.company_id = ?"
        );
        return companyId == null
                ? jdbcTemplate.query(sql, this::mapRow)
                : jdbcTemplate.query(sql, this::mapRow, companyId);
    }

    public Optional<Vehicle> findById(UUID id) {
        return jdbcTemplate.query("SELECT " + selectColumns() + " FROM vehicle WHERE vehicle_id = ?", this::mapRow, id)
                .stream().findFirst();
    }

    public Vehicle create(VehicleUpsertRequest r) {
        VehicleModelMeta meta = resolveVehicleModelMeta(r.modelId());
        UUID modelId = requiredUuid(r.modelId(), "modelId");
        UUID variantId = requiredUuid(r.variantId(), "variantId");
        UUID typeId = r.typeId() != null ? r.typeId() : meta.typeId();
        UUID categoryId = r.categoryId() != null ? r.categoryId() : meta.categoryId();
        UUID manufacturerId = r.manufacturerId() != null ? r.manufacturerId() : meta.manufacturerId();
        Integer ownershipTypeId = resolveOwnershipTypeIdForCreate(r.ownershipTypeId());

        String sql = """
                INSERT INTO vehicle (
                    modelvariant_id, vehicle_model, vehicle_type, vehicle_category,
                    registration_number, chassis_number, engine_number, key_number, vehicle_image,
                    manufacture_year, color, fuel_type_id, transmission_type_id, number_plate_type_id, body_style_id,
                    seating_capacity, undercarriage_type, engine_type, engine_manufacture,
                    initial_odometer_km, current_odometer_km, total_engine_hours,
                    consumption_method_id, rated_efficiency_kmpl, rated_consumption_lph,
                    ownership_type_id, previous_owners_count, manufacture_id, distributor_id,
                    current_ownership,
                    vehicle_condition, operational_status_id, vehicle_status_id, notes, is_active
                )
                VALUES (
                    ?, ?, ?, ?,
                    ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?, ?,
                    ?, ?, ?, ?,
                    ?, ?, ?,
                    ?, ?, ?,
                    ?, ?, ?, ?,
                    ?,
                    ?, ?, ?, ?, COALESCE(?, TRUE)
                )
                RETURNING %s
                """.formatted(selectColumns());

        return jdbcTemplate.queryForObject(sql, this::mapRow,
                variantId, modelId, typeId, categoryId,
                r.registrationNumber(), r.chassisNumber(), r.engineNumber(), r.keyNumber(), r.vehicleImage(),
                r.manufactureYear(), r.color(), r.fuelTypeId(), r.transmissionTypeId(), r.numberPlateTypeId(), r.bodyStyleId(),
                r.seatingCapacity(), r.undercarriageTypeId(), r.engineTypeId(), r.engineManufactureId(),
                r.initialOdometerKm(), r.currentOdometerKm(), r.totalEngineHours(),
                r.consumptionMethodId(), r.ratedEfficiencyKmpl(), r.ratedConsumptionLph(),
                ownershipTypeId, r.previousOwnersCount(), manufacturerId, r.distributorId(),
                r.currentOwnership(),
                r.vehicleCondition(), r.operationalStatusId(), r.vehicleStatusId(), r.notes(), r.isActive()
        );
    }

    public Optional<Vehicle> update(UUID id, VehicleUpsertRequest r) {
        UUID modelId = r.modelId();
        VehicleModelMeta meta = modelId != null ? resolveVehicleModelMeta(modelId) : null;
        UUID resolvedTypeId = r.typeId() != null ? r.typeId() : (meta == null ? null : meta.typeId());
        UUID resolvedCategoryId = r.categoryId() != null ? r.categoryId() : (meta == null ? null : meta.categoryId());
        UUID resolvedManufacturerId = r.manufacturerId() != null ? r.manufacturerId() : (meta == null ? null : meta.manufacturerId());

        String sql = """
                UPDATE vehicle
                SET modelvariant_id = COALESCE(?, modelvariant_id),
                    vehicle_model = COALESCE(?, vehicle_model),
                    vehicle_type = COALESCE(?, vehicle_type),
                    vehicle_category = COALESCE(?, vehicle_category),
                    registration_number = COALESCE(?, registration_number),
                    chassis_number = COALESCE(?, chassis_number),
                    engine_number = COALESCE(?, engine_number),
                    key_number = COALESCE(?, key_number),
                    vehicle_image = COALESCE(?, vehicle_image),
                    manufacture_year = COALESCE(?, manufacture_year),
                    color = COALESCE(?, color),
                    fuel_type_id = COALESCE(?, fuel_type_id),
                    transmission_type_id = COALESCE(?, transmission_type_id),
                    number_plate_type_id = COALESCE(?, number_plate_type_id),
                    body_style_id = COALESCE(?, body_style_id),
                    seating_capacity = COALESCE(?, seating_capacity),
                    undercarriage_type = COALESCE(?, undercarriage_type),
                    engine_type = COALESCE(?, engine_type),
                    engine_manufacture = COALESCE(?, engine_manufacture),
                    initial_odometer_km = COALESCE(?, initial_odometer_km),
                    current_odometer_km = COALESCE(?, current_odometer_km),
                    total_engine_hours = COALESCE(?, total_engine_hours),
                    consumption_method_id = COALESCE(?, consumption_method_id),
                    rated_efficiency_kmpl = COALESCE(?, rated_efficiency_kmpl),
                    rated_consumption_lph = COALESCE(?, rated_consumption_lph),
                    ownership_type_id = COALESCE(?, ownership_type_id),
                    previous_owners_count = COALESCE(?, previous_owners_count),
                    manufacture_id = COALESCE(?, manufacture_id),
                    distributor_id = COALESCE(?, distributor_id),
                    current_ownership = COALESCE(?, current_ownership),
                    vehicle_condition = COALESCE(?, vehicle_condition),
                    operational_status_id = COALESCE(?, operational_status_id),
                    vehicle_status_id = COALESCE(?, vehicle_status_id),
                    notes = COALESCE(?, notes),
                    is_active = COALESCE(?, is_active)
                WHERE vehicle_id = ?
                RETURNING %s
                """.formatted(selectColumns());

        return jdbcTemplate.query(sql, this::mapRow,
                r.variantId(), r.modelId(), resolvedTypeId, resolvedCategoryId,
                r.registrationNumber(), r.chassisNumber(), r.engineNumber(), r.keyNumber(), r.vehicleImage(),
                r.manufactureYear(), r.color(), r.fuelTypeId(), r.transmissionTypeId(), r.numberPlateTypeId(), r.bodyStyleId(),
                r.seatingCapacity(), r.undercarriageTypeId(), r.engineTypeId(), r.engineManufactureId(),
                r.initialOdometerKm(), r.currentOdometerKm(), r.totalEngineHours(),
                r.consumptionMethodId(), r.ratedEfficiencyKmpl(), r.ratedConsumptionLph(),
                r.ownershipTypeId(), r.previousOwnersCount(), resolvedManufacturerId, r.distributorId(),
                r.currentOwnership(),
                r.vehicleCondition(), r.operationalStatusId(), r.vehicleStatusId(), r.notes(), r.isActive(),
                id
        ).stream().findFirst();
    }

    public boolean delete(UUID id) {
        return jdbcTemplate.update("DELETE FROM vehicle WHERE vehicle_id = ?", id) > 0;
    }

    private Vehicle mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        Date insuranceExpiryDate = rs.getDate("insurance_expiry");
        Date registrationExpiryDate = rs.getDate("registration_expiry");
        Date decommissionDate = rs.getDate("decommission_date");
        return new Vehicle(
                rs.getObject("vehicle_id", UUID.class),
                rs.getObject("company_id", UUID.class),
                rs.getString("company_code"),
                rs.getObject("branch_id", UUID.class),
                rs.getObject("model_id", UUID.class),
                rs.getObject("variant_id", UUID.class),
                rs.getString("vehicle_code"),
                rs.getString("registration_number"),
                rs.getString("chassis_number"),
                rs.getString("engine_number"),
                rs.getString("key_number"),
                rs.getObject("ownership_type_id", Integer.class),
                rs.getString("current_ownership"),
                rs.getObject("manufacture_year", Integer.class),
                rs.getObject("registered_year", Integer.class),
                rs.getString("country"),
                rs.getString("color"),
                rs.getBigDecimal("initial_odometer_km"),
                rs.getBigDecimal("current_odometer_km"),
                rs.getBigDecimal("total_engine_hours"),
                rs.getObject("consumption_method_id", Integer.class),
                rs.getObject("air_condition", Boolean.class),
                rs.getBigDecimal("rated_efficiency_kmpl"),
                rs.getBigDecimal("rated_consumption_lph"),
                rs.getObject("operational_status_id", Integer.class),
                rs.getString("current_location"),
                rs.getObject("current_project_id", UUID.class),
                rs.getObject("current_driver_id", UUID.class),
                insuranceExpiryDate == null ? null : insuranceExpiryDate.toLocalDate(),
                registrationExpiryDate == null ? null : registrationExpiryDate.toLocalDate(),
                rs.getString("notes"),
                rs.getObject("is_active", Boolean.class),
                decommissionDate == null ? null : decommissionDate.toLocalDate(),
                rs.getString("decommission_reason"),
                createdAtTs == null ? null : createdAtTs.toInstant(),
                updatedAtTs == null ? null : updatedAtTs.toInstant(),
                rs.getObject("type_id", UUID.class),
                rs.getObject("category_id", UUID.class),
                rs.getObject("fuel_type_id", Integer.class),
                rs.getObject("transmission_type_id", Integer.class),
                rs.getObject("number_plate_type_id", Integer.class),
                rs.getObject("body_style_id", Integer.class),
                rs.getObject("seating_capacity", Integer.class),
                rs.getObject("undercarriage_type_id", Integer.class),
                rs.getObject("engine_type_id", Integer.class),
                rs.getObject("engine_manufacture_id", Integer.class),
                rs.getObject("previous_owners_count", Integer.class),
                rs.getObject("manufacturer_id", UUID.class),
                rs.getObject("distributor_id", UUID.class),
                rs.getString("vehicle_condition"),
                rs.getObject("vehicle_status_id", Integer.class),
                rs.getString("vehicle_image"),
                rs.getObject("created_by", UUID.class),
                rs.getObject("updated_by", UUID.class)
        );
    }

    private String selectColumns() {
        return """
                vehicle_id,
                (
                    SELECT cv.company_id
                    FROM company_vehicles cv
                    WHERE cv.companyvehicle_model = vehicle_model
                      AND (
                           (cv.chassis_number IS NOT NULL AND cv.chassis_number = chassis_number)
                        OR (cv.registration_number IS NOT NULL AND cv.registration_number = registration_number)
                      )
                    ORDER BY cv.updated_at DESC NULLS LAST, cv.created_at DESC NULLS LAST
                    LIMIT 1
                ) AS company_id,
                (
                    SELECT cv.company_code
                    FROM company_vehicles cv
                    WHERE cv.companyvehicle_model = vehicle_model
                      AND (
                           (cv.chassis_number IS NOT NULL AND cv.chassis_number = chassis_number)
                        OR (cv.registration_number IS NOT NULL AND cv.registration_number = registration_number)
                      )
                    ORDER BY cv.updated_at DESC NULLS LAST, cv.created_at DESC NULLS LAST
                    LIMIT 1
                ) AS company_code,
                NULL::uuid AS branch_id,
                vehicle_model AS model_id,
                modelvariant_id AS variant_id,
                NULL::varchar AS vehicle_code,
                registration_number,
                chassis_number,
                engine_number,
                key_number,
                ownership_type_id,
                current_ownership,
                manufacture_year,
                NULL::integer AS registered_year,
                NULL::varchar AS country,
                color,
                initial_odometer_km,
                current_odometer_km,
                total_engine_hours,
                consumption_method_id,
                NULL::boolean AS air_condition,
                rated_efficiency_kmpl,
                rated_consumption_lph,
                operational_status_id,
                NULL::varchar AS current_location,
                NULL::uuid AS current_project_id,
                NULL::uuid AS current_driver_id,
                NULL::date AS insurance_expiry,
                NULL::date AS registration_expiry,
                notes,
                is_active,
                NULL::date AS decommission_date,
                NULL::text AS decommission_reason,
                created_at,
                updated_at,
                vehicle_type AS type_id,
                vehicle_category AS category_id,
                fuel_type_id,
                transmission_type_id,
                number_plate_type_id,
                body_style_id,
                seating_capacity,
                undercarriage_type AS undercarriage_type_id,
                engine_type AS engine_type_id,
                engine_manufacture AS engine_manufacture_id,
                previous_owners_count,
                manufacture_id AS manufacturer_id,
                distributor_id,
                vehicle_condition,
                vehicle_status_id,
                vehicle_image,
                created_by,
                updated_by
                """;
    }

    private VehicleModelMeta resolveVehicleModelMeta(UUID modelId) {
        if (modelId == null) throw new IllegalArgumentException("modelId is required");
        return jdbcTemplate.query("""
                SELECT m.manufacturer_id, m.type_id, t.category_id
                FROM vehicle_model m
                JOIN vehicle_type t ON t.type_id = m.type_id
                WHERE m.model_id = ?
                """, (rs, i) -> new VehicleModelMeta(
                rs.getObject("manufacturer_id", UUID.class),
                rs.getObject("type_id", UUID.class),
                rs.getObject("category_id", UUID.class)
        ), modelId).stream().findFirst().orElseThrow(() -> new IllegalArgumentException("Invalid modelId"));
    }

    private Integer resolveOwnershipTypeIdForCreate(Integer requestedOwnershipTypeId) {
        if (requestedOwnershipTypeId != null) return requestedOwnershipTypeId;
        List<Integer> owned = jdbcTemplate.query(
                "SELECT type_id FROM ownership_type WHERE UPPER(type_code) = 'OWNED' ORDER BY type_id LIMIT 1",
                (rs, i) -> rs.getInt(1)
        );
        if (!owned.isEmpty()) return owned.getFirst();
        List<Integer> any = jdbcTemplate.query("SELECT type_id FROM ownership_type ORDER BY type_id LIMIT 1", (rs, i) -> rs.getInt(1));
        if (!any.isEmpty()) return any.getFirst();
        throw new IllegalArgumentException("No ownership_type found");
    }

    private UUID requiredUuid(UUID value, String fieldName) {
        if (value == null) throw new IllegalArgumentException(fieldName + " is required");
        return value;
    }

    private record VehicleModelMeta(UUID manufacturerId, UUID typeId, UUID categoryId) {}
}
