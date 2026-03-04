package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.repository;

import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.dto.HiredVehicleTypeCountResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.dto.HiredVehicleUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.dto.HiredVehicleSupplierOptionResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.dto.HiredVehicleOwnershipTypeOptionResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.dto.HiredVehicleModelPrefillResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.model.HiredVehicle;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class HiredVehicleRepository {
    private final JdbcTemplate jdbcTemplate;

    public HiredVehicleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<HiredVehicle> findAll(UUID supplierId) {
        String sql = "SELECT " + selectColumns() + " FROM hired_vehicles " +
                (supplierId == null ? "" : "WHERE supplier_id = ? ") +
                "ORDER BY updated_at DESC, created_at DESC";
        return supplierId == null ? jdbcTemplate.query(sql, this::mapRow) : jdbcTemplate.query(sql, this::mapRow, supplierId);
    }

    public Optional<HiredVehicle> findById(UUID id) {
        return jdbcTemplate.query("SELECT " + selectColumns() + " FROM hired_vehicles WHERE hiredvehicle_id = ?", this::mapRow, id)
                .stream().findFirst();
    }

    public Optional<HiredVehicle> findFirst(UUID supplierId) {
        String sql = "SELECT " + selectColumns() + " FROM hired_vehicles " +
                (supplierId == null ? "" : "WHERE supplier_id = ? ") +
                "ORDER BY updated_at DESC, created_at DESC LIMIT 1";
        return (supplierId == null ? jdbcTemplate.query(sql, this::mapRow) : jdbcTemplate.query(sql, this::mapRow, supplierId))
                .stream().findFirst();
    }

    public List<HiredVehicle> findByRegistrationNumber(String query, int limit) {
        int effectiveLimit = Math.max(1, Math.min(limit, 500));
        String normalized = query == null ? "" : query.trim();
        if (normalized.isEmpty()) {
            String sql = "SELECT " + selectColumns() + " FROM hired_vehicles " +
                    "WHERE COALESCE(registration_number, '') <> '' " +
                    "ORDER BY updated_at DESC, created_at DESC LIMIT ?";
            return jdbcTemplate.query(sql, this::mapRow, effectiveLimit);
        }
        String sql = "SELECT " + selectColumns() + " FROM hired_vehicles " +
                "WHERE registration_number ILIKE ? " +
                "ORDER BY updated_at DESC, created_at DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRow, "%" + normalized + "%", effectiveLimit);
    }

    public HiredVehicle create(HiredVehicleUpsertRequest r) {
        String sql = """
                INSERT INTO hired_vehicles (
                    supplier_id, supplier_code, hiredvehicle_type, hiredvehicle_model,
                    hiredvehicle_category, hiredvehicle_manufacture,
                    registration_number, chassis_number, engine_number, key_number, vehicle_image,
                    manufacture_year, color, fuel_type_id, transmission_type_id, number_plate_type_id,
                    body_style_id, seating_capacity, undercarriage_type, engine_type, engine_manufacture,
                    initial_odometer_km, current_odometer_km, total_engine_hours,
                    consumption_method_id, rated_efficiency_kmpl, rated_consumption_lph,
                    ownership_type_id, current_ownership, previous_owners_count, manufacture_id, distributor_id,
                    vehicle_condition, operational_status_id, vehicle_status_id,
                    notes, is_active, created_by, updated_by
                ) VALUES (
                    ?, ?, ?, ?,
                    ?, ?,
                    ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?,
                    ?, ?, ?,
                    ?, ?, ?,
                    ?, ?, ?, ?, ?,
                    ?, ?, ?,
                    ?, COALESCE(?, TRUE), ?, ?
                )
                RETURNING %s
                """.formatted(selectColumns());

        return jdbcTemplate.queryForObject(sql, this::mapRow,
                r.supplierId(), r.supplierCode(), r.hiredVehicleType(), r.hiredVehicleModel(),
                r.categoryId(), r.hiredVehicleManufacture(),
                r.registrationNumber(), r.chassisNumber(), r.engineNumber(), r.keyNumber(), r.vehicleImage(),
                r.manufactureYear(), r.color(), r.fuelTypeId(), r.transmissionTypeId(), r.numberPlateTypeId(),
                r.bodyStyleId(), r.seatingCapacity(), r.undercarriageTypeId(), r.engineTypeId(), r.engineManufactureId(),
                r.initialOdometerKm(), r.currentOdometerKm(), r.totalEngineHours(),
                r.consumptionMethodId(), r.ratedEfficiencyKmpl(), r.ratedConsumptionLph(),
                r.ownershipTypeId(), r.currentOwnership(), r.previousOwnersCount(), r.manufactureId(), r.distributorId(),
                r.vehicleCondition(), r.operationalStatusId(), r.vehicleStatusId(),
                r.notes(), r.isActive(), r.createdBy(), r.updatedBy()
        );
    }

    public Optional<HiredVehicle> update(UUID id, HiredVehicleUpsertRequest r) {
        String sql = """
                UPDATE hired_vehicles
                SET supplier_id = COALESCE(?, supplier_id),
                    supplier_code = COALESCE(?, supplier_code),
                    hiredvehicle_type = COALESCE(?, hiredvehicle_type),
                    hiredvehicle_model = COALESCE(?, hiredvehicle_model),
                    hiredvehicle_category = COALESCE(?, hiredvehicle_category),
                    hiredvehicle_manufacture = COALESCE(?, hiredvehicle_manufacture),
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
                    current_ownership = COALESCE(?, current_ownership),
                    previous_owners_count = COALESCE(?, previous_owners_count),
                    manufacture_id = COALESCE(?, manufacture_id),
                    distributor_id = COALESCE(?, distributor_id),
                    vehicle_condition = COALESCE(?, vehicle_condition),
                    operational_status_id = COALESCE(?, operational_status_id),
                    vehicle_status_id = COALESCE(?, vehicle_status_id),
                    notes = COALESCE(?, notes),
                    is_active = COALESCE(?, is_active),
                    created_by = COALESCE(?, created_by),
                    updated_by = COALESCE(?, updated_by)
                WHERE hiredvehicle_id = ?
                RETURNING %s
                """.formatted(selectColumns());

        return jdbcTemplate.query(sql, this::mapRow,
                r.supplierId(), r.supplierCode(), r.hiredVehicleType(), r.hiredVehicleModel(),
                r.categoryId(), r.hiredVehicleManufacture(),
                r.registrationNumber(), r.chassisNumber(), r.engineNumber(), r.keyNumber(), r.vehicleImage(),
                r.manufactureYear(), r.color(), r.fuelTypeId(), r.transmissionTypeId(), r.numberPlateTypeId(),
                r.bodyStyleId(), r.seatingCapacity(), r.undercarriageTypeId(), r.engineTypeId(), r.engineManufactureId(),
                r.initialOdometerKm(), r.currentOdometerKm(), r.totalEngineHours(),
                r.consumptionMethodId(), r.ratedEfficiencyKmpl(), r.ratedConsumptionLph(),
                r.ownershipTypeId(), r.currentOwnership(), r.previousOwnersCount(), r.manufactureId(), r.distributorId(),
                r.vehicleCondition(), r.operationalStatusId(), r.vehicleStatusId(),
                r.notes(), r.isActive(), r.createdBy(), r.updatedBy(), id
        ).stream().findFirst();
    }

    public boolean delete(UUID id) {
        return jdbcTemplate.update("DELETE FROM hired_vehicles WHERE hiredvehicle_id = ?", id) > 0;
    }

    public boolean supplierExists(UUID supplierId) {
        List<Integer> rows = jdbcTemplate.query(
                "SELECT 1 FROM supplier WHERE supplier_id = ? LIMIT 1",
                (rs, i) -> rs.getInt(1),
                supplierId
        );
        return !rows.isEmpty();
    }

    public boolean supplierAllowedForHiring(UUID supplierId) {
        List<Integer> rows = jdbcTemplate.query(
                """
                SELECT 1
                FROM supplier s
                LEFT JOIN supplier_type st ON st.type_id = s.supplier_type_id
                WHERE s.supplier_id = ?
                  AND COALESCE(s.is_active, TRUE) = TRUE
                  AND LOWER(COALESCE(st.type_name, '')) IN ('equipment supplier', 'rental partner', 'transport provider')
                LIMIT 1
                """,
                (rs, i) -> rs.getInt(1),
                supplierId
        );
        return !rows.isEmpty();
    }

    public List<HiredVehicleSupplierOptionResponse> findHiredVehicleSupplierOptions() {
        return jdbcTemplate.query(
                """
                SELECT s.supplier_id, s.supplier_code, s.supplier_name
                FROM supplier s
                LEFT JOIN supplier_type st ON st.type_id = s.supplier_type_id
                WHERE COALESCE(s.is_active, TRUE) = TRUE
                  AND LOWER(COALESCE(st.type_name, '')) IN ('equipment supplier', 'rental partner', 'transport provider')
                ORDER BY s.supplier_name ASC
                """,
                (rs, i) -> new HiredVehicleSupplierOptionResponse(
                        rs.getObject("supplier_id", UUID.class),
                        rs.getString("supplier_code"),
                        rs.getString("supplier_name")
                )
        );
    }

    public List<HiredVehicleOwnershipTypeOptionResponse> findRentedAndThirdPartyOwnershipTypes() {
        return jdbcTemplate.query(
                """
                SELECT type_id, type_code, type_name
                FROM ownership_type
                WHERE COALESCE(is_active, TRUE) = TRUE
                  AND UPPER(REPLACE(REPLACE(REPLACE(COALESCE(type_code, ''), ' ', ''), '_', ''), '-', '')) IN ('RENTED', 'THIRDPARTY')
                ORDER BY CASE
                    WHEN UPPER(REPLACE(REPLACE(REPLACE(COALESCE(type_code, ''), ' ', ''), '_', ''), '-', '')) = 'RENTED' THEN 0
                    ELSE 1
                END, type_name ASC
                """,
                (rs, i) -> new HiredVehicleOwnershipTypeOptionResponse(
                        rs.getObject("type_id", Integer.class),
                        rs.getString("type_code"),
                        rs.getString("type_name")
                )
        );
    }

    public List<HiredVehicleOwnershipTypeOptionResponse> findActiveOwnershipTypes() {
        return jdbcTemplate.query(
                """
                SELECT type_id, type_code, type_name
                FROM ownership_type
                ORDER BY
                    CASE WHEN COALESCE(is_active, TRUE) = TRUE THEN 0 ELSE 1 END,
                    type_name ASC,
                    type_id ASC
                """,
                (rs, i) -> new HiredVehicleOwnershipTypeOptionResponse(
                        rs.getObject("type_id", Integer.class),
                        rs.getString("type_code"),
                        rs.getString("type_name")
                )
        );
    }

    public List<HiredVehicleSupplierOptionResponse> findCurrentOwnershipSupplierOptions(UUID supplierId) {
        final String sql = """
                SELECT DISTINCT
                    NULL::uuid AS supplier_id,
                    NULL::text AS supplier_code,
                    TRIM(v.current_ownership) AS supplier_name
                FROM vehicle v
                WHERE COALESCE(TRIM(v.current_ownership), '') <> ''
                ORDER BY supplier_name ASC
                """;
        return jdbcTemplate.query(
                sql,
                (rs, i) -> new HiredVehicleSupplierOptionResponse(
                        rs.getObject("supplier_id", UUID.class),
                        rs.getString("supplier_code"),
                        rs.getString("supplier_name")
                )
        );
    }

    public boolean currentOwnershipSupplierExists(UUID supplierId, String supplierName) {
        String normalized = supplierName == null ? "" : supplierName.trim().toLowerCase();
        if (normalized.isEmpty()) {
            return false;
        }
        final String sql = """
                SELECT 1
                FROM vehicle v
                WHERE LOWER(TRIM(COALESCE(v.current_ownership, ''))) = ?
                LIMIT 1
                """;
        List<Integer> rows = jdbcTemplate.query(sql, (rs, i) -> rs.getInt(1), normalized);
        return !rows.isEmpty();
    }

    public Optional<Integer> findOwnershipTypeIdByCode(String typeCode) {
        return jdbcTemplate.query(
                """
                SELECT type_id
                FROM ownership_type
                WHERE UPPER(REPLACE(REPLACE(REPLACE(COALESCE(type_code, ''), ' ', ''), '_', ''), '-', ''))
                      = UPPER(REPLACE(REPLACE(REPLACE(COALESCE(?, ''), ' ', ''), '_', ''), '-', ''))
                LIMIT 1
                """,
                (rs, i) -> rs.getInt("type_id"),
                typeCode
        ).stream().findFirst();
    }

    public Optional<String> findOwnershipTypeCodeById(Integer typeId) {
        return jdbcTemplate.query(
                """
                SELECT type_code
                FROM ownership_type
                WHERE type_id = ?
                LIMIT 1
                """,
                (rs, i) -> rs.getString("type_code"),
                typeId
        ).stream().findFirst();
    }

    public Optional<String> findSupplierCodeById(UUID supplierId) {
        return jdbcTemplate.query(
                "SELECT supplier_code FROM supplier WHERE supplier_id = ?",
                (rs, i) -> rs.getString("supplier_code"),
                supplierId
        ).stream().findFirst();
    }

    public Optional<UUID> findTypeIdByModelId(UUID modelId) {
        return jdbcTemplate.query(
                "SELECT type_id FROM vehicle_model WHERE model_id = ?",
                (rs, i) -> rs.getObject("type_id", UUID.class),
                modelId
        ).stream().findFirst();
    }

    public List<String> findIdentificationCodesBySupplierAndType(UUID supplierId, UUID typeId) {
        return jdbcTemplate.query(
                """
                SELECT key_number
                FROM hired_vehicles
                WHERE supplier_id = ?
                  AND hiredvehicle_type = ?
                  AND key_number IS NOT NULL
                """,
                (rs, i) -> rs.getString("key_number"),
                supplierId,
                typeId
        );
    }

    public List<String> findIdentificationCodesByType(UUID typeId) {
        return jdbcTemplate.query(
                """
                SELECT key_number
                FROM hired_vehicles
                WHERE hiredvehicle_type = ?
                  AND key_number IS NOT NULL
                """,
                (rs, i) -> rs.getString("key_number"),
                typeId
        );
    }

    public Optional<UUID> findCompanyIdBySupplierId(UUID supplierId) {
        return jdbcTemplate.query(
                "SELECT company_id FROM supplier WHERE supplier_id = ?",
                (rs, i) -> rs.getObject("company_id", UUID.class),
                supplierId
        ).stream().findFirst();
    }

    public Optional<String> findCompanyTypeIdentifierCode(UUID companyId, UUID typeId) {
        return jdbcTemplate.query(
                """
                SELECT idtype_code AS type_identifier
                FROM hiredvehicleidtype
                WHERE idtype_com = ?
                  AND idtype_typeid = ?
                  AND is_active = TRUE
                ORDER BY updated_at DESC
                LIMIT 1
                """,
                (rs, i) -> rs.getString("type_identifier"),
                companyId,
                typeId
        ).stream().findFirst();
    }

    public Optional<String> findAnyTypeIdentifierCode(UUID typeId) {
        return jdbcTemplate.query(
                """
                SELECT idtype_code AS type_identifier
                FROM hiredvehicleidtype
                WHERE idtype_typeid = ?
                  AND is_active = TRUE
                ORDER BY updated_at DESC
                LIMIT 1
                """,
                (rs, i) -> rs.getString("type_identifier"),
                typeId
        ).stream().findFirst();
    }

    public Optional<HiredVehicleModelPrefillResponse> findModelPrefill(UUID modelId, UUID supplierId) {
        return jdbcTemplate.query(
                """
                SELECT
                    v.model_id,
                    v.type_id,
                    v.category_id,
                    v.manufacturer_id,
                    v.ownership_type_id,
                    v.current_ownership,
                    v.initial_odometer_km,
                    v.current_odometer_km,
                    v.total_engine_hours,
                    v.consumption_method_id,
                    v.rated_efficiency_kmpl,
                    v.rated_consumption_lph
                FROM vehicle v
                LEFT JOIN supplier s ON s.supplier_id = ?::uuid
                WHERE v.model_id = ?::uuid
                ORDER BY
                    CASE
                        WHEN ?::uuid IS NOT NULL
                             AND s.company_id IS NOT NULL
                             AND v.company_id = s.company_id THEN 0
                        ELSE 1
                    END,
                    CASE WHEN v.ownership_type_id IS NOT NULL THEN 0 ELSE 1 END,
                    CASE WHEN COALESCE(TRIM(v.current_ownership), '') <> '' THEN 0 ELSE 1 END,
                    v.updated_at DESC,
                    v.created_at DESC
                LIMIT 1
                """,
                (rs, i) -> new HiredVehicleModelPrefillResponse(
                        rs.getObject("model_id", UUID.class),
                        rs.getObject("type_id", UUID.class),
                        rs.getObject("category_id", UUID.class),
                        rs.getObject("manufacturer_id", UUID.class),
                        rs.getObject("ownership_type_id", Integer.class),
                        rs.getString("current_ownership"),
                        rs.getBigDecimal("initial_odometer_km"),
                        rs.getBigDecimal("current_odometer_km"),
                        rs.getBigDecimal("total_engine_hours"),
                        rs.getObject("consumption_method_id", Integer.class),
                        rs.getBigDecimal("rated_efficiency_kmpl"),
                        rs.getBigDecimal("rated_consumption_lph")
                ),
                supplierId,
                modelId,
                supplierId
        ).stream().findFirst();
    }

    public Optional<HiredVehicleModelPrefillResponse> findTypePrefill(UUID typeId, UUID supplierId) {
        return jdbcTemplate.query(
                """
                SELECT
                    v.model_id,
                    v.type_id,
                    v.category_id,
                    v.manufacturer_id,
                    v.ownership_type_id,
                    v.current_ownership,
                    v.initial_odometer_km,
                    v.current_odometer_km,
                    v.total_engine_hours,
                    v.consumption_method_id,
                    v.rated_efficiency_kmpl,
                    v.rated_consumption_lph
                FROM vehicle v
                LEFT JOIN supplier s ON s.supplier_id = ?::uuid
                WHERE v.type_id = ?::uuid
                ORDER BY
                    CASE
                        WHEN ?::uuid IS NOT NULL
                             AND s.company_id IS NOT NULL
                             AND v.company_id = s.company_id THEN 0
                        ELSE 1
                    END,
                    CASE WHEN v.ownership_type_id IS NOT NULL THEN 0 ELSE 1 END,
                    CASE WHEN COALESCE(TRIM(v.current_ownership), '') <> '' THEN 0 ELSE 1 END,
                    v.updated_at DESC,
                    v.created_at DESC
                LIMIT 1
                """,
                (rs, i) -> new HiredVehicleModelPrefillResponse(
                        rs.getObject("model_id", UUID.class),
                        rs.getObject("type_id", UUID.class),
                        rs.getObject("category_id", UUID.class),
                        rs.getObject("manufacturer_id", UUID.class),
                        rs.getObject("ownership_type_id", Integer.class),
                        rs.getString("current_ownership"),
                        rs.getBigDecimal("initial_odometer_km"),
                        rs.getBigDecimal("current_odometer_km"),
                        rs.getBigDecimal("total_engine_hours"),
                        rs.getObject("consumption_method_id", Integer.class),
                        rs.getBigDecimal("rated_efficiency_kmpl"),
                        rs.getBigDecimal("rated_consumption_lph")
                ),
                supplierId,
                typeId,
                supplierId
        ).stream().findFirst();
    }

    public Optional<Integer> findDefaultOwnershipTypeId() {
        return jdbcTemplate.query(
                """
                SELECT type_id
                FROM ownership_type
                WHERE COALESCE(is_active, TRUE) = TRUE
                ORDER BY type_id
                LIMIT 1
                """,
                (rs, i) -> rs.getInt("type_id")
        ).stream().findFirst();
    }

    public long[] overview(UUID supplierId) {
        return jdbcTemplate.queryForObject(
                """
                SELECT
                    COUNT(*) AS total_vehicles,
                    COUNT(*) FILTER (WHERE COALESCE(is_active, FALSE) = TRUE) AS active_vehicles,
                    COUNT(*) FILTER (WHERE COALESCE(is_active, FALSE) = FALSE) AS inactive_vehicles,
                    COUNT(DISTINCT hiredvehicle_type) AS total_types,
                    COUNT(DISTINCT hiredvehicle_manufacture) AS total_brands
                FROM hired_vehicles
                WHERE (?::uuid IS NULL OR supplier_id = ?::uuid)
                """,
                (rs, rowNum) -> new long[] {
                        rs.getLong("total_vehicles"),
                        rs.getLong("active_vehicles"),
                        rs.getLong("inactive_vehicles"),
                        rs.getLong("total_types"),
                        rs.getLong("total_brands")
                },
                supplierId,
                supplierId
        );
    }

    public List<HiredVehicleTypeCountResponse> overviewByType(UUID supplierId) {
        return jdbcTemplate.query(
                """
                SELECT
                    hv.hiredvehicle_type AS type_id,
                    COALESCE(vt.type_name, 'Unknown') AS type_name,
                    COUNT(*) AS vehicle_count
                FROM hired_vehicles hv
                LEFT JOIN vehicle_type vt ON vt.type_id = hv.hiredvehicle_type
                WHERE (?::uuid IS NULL OR hv.supplier_id = ?::uuid)
                GROUP BY hv.hiredvehicle_type, vt.type_name
                ORDER BY vehicle_count DESC, type_name ASC
                """,
                (rs, i) -> new HiredVehicleTypeCountResponse(
                        rs.getObject("type_id", UUID.class),
                        rs.getString("type_name"),
                        rs.getLong("vehicle_count")
                ),
                supplierId,
                supplierId
        );
    }

    private HiredVehicle mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        return new HiredVehicle(
                rs.getObject("hiredvehicle_id", UUID.class),
                rs.getObject("supplier_id", UUID.class),
                rs.getString("supplier_code"),
                rs.getString("supplier_name"),
                rs.getObject("hiredvehicle_type", UUID.class),
                rs.getString("hiredvehicle_type_name"),
                rs.getObject("hiredvehicle_model", UUID.class),
                rs.getString("hiredvehicle_model_name"),
                rs.getObject("hiredvehicle_category", UUID.class),
                rs.getString("category_name"),
                rs.getObject("hiredvehicle_manufacture", UUID.class),
                rs.getString("hiredvehicle_manufacture_brand"),
                rs.getString("registration_number"),
                rs.getString("chassis_number"),
                rs.getString("engine_number"),
                rs.getString("key_number"),
                rs.getString("vehicle_image"),
                rs.getObject("manufacture_year", Integer.class),
                rs.getString("color"),
                rs.getObject("fuel_type_id", Integer.class),
                rs.getObject("transmission_type_id", Integer.class),
                rs.getObject("number_plate_type_id", Integer.class),
                rs.getObject("body_style_id", Integer.class),
                rs.getObject("seating_capacity", Integer.class),
                rs.getObject("undercarriage_type", Integer.class),
                rs.getObject("engine_type", Integer.class),
                rs.getObject("engine_manufacture", Integer.class),
                rs.getBigDecimal("initial_odometer_km"),
                rs.getBigDecimal("current_odometer_km"),
                rs.getBigDecimal("total_engine_hours"),
                rs.getObject("consumption_method_id", Integer.class),
                rs.getBigDecimal("rated_efficiency_kmpl"),
                rs.getBigDecimal("rated_consumption_lph"),
                rs.getObject("ownership_type_id", Integer.class),
                rs.getString("ownership_type_name"),
                rs.getString("current_ownership"),
                rs.getObject("previous_owners_count", Integer.class),
                rs.getObject("manufacture_id", UUID.class),
                rs.getObject("distributor_id", UUID.class),
                rs.getString("distributor_name"),
                rs.getString("vehicle_condition"),
                rs.getObject("operational_status_id", Integer.class),
                rs.getString("operational_status_name"),
                rs.getString("consumption_method_name"),
                rs.getObject("vehicle_status_id", Integer.class),
                rs.getString("notes"),
                rs.getObject("is_active", Boolean.class),
                rs.getObject("created_by", UUID.class),
                rs.getObject("updated_by", UUID.class),
                createdAtTs == null ? null : createdAtTs.toInstant(),
                updatedAtTs == null ? null : updatedAtTs.toInstant()
        );
    }

    private String selectColumns() {
        return """
                hiredvehicle_id,
                supplier_id,
                supplier_code,
                (
                    SELECT s.supplier_name
                    FROM supplier s
                    WHERE s.supplier_id = hired_vehicles.supplier_id
                ) AS supplier_name,
                hiredvehicle_type,
                (
                    SELECT vt.type_name
                    FROM vehicle_type vt
                    WHERE vt.type_id = hired_vehicles.hiredvehicle_type
                ) AS hiredvehicle_type_name,
                hiredvehicle_model,
                (
                    SELECT vm.model_name
                    FROM vehicle_model vm
                    WHERE vm.model_id = hiredvehicle_model
                ) AS hiredvehicle_model_name,
                hiredvehicle_category,
                (
                    SELECT vc.category_name
                    FROM vehicle_category vc
                    WHERE vc.category_id = hiredvehicle_category
                ) AS category_name,
                hiredvehicle_manufacture,
                (
                    SELECT vmfr.manufacturer_brand
                    FROM vehicle_manufacturer vmfr
                    WHERE vmfr.manufacturer_id = hiredvehicle_manufacture
                ) AS hiredvehicle_manufacture_brand,
                registration_number,
                chassis_number,
                engine_number,
                key_number,
                vehicle_image,
                manufacture_year,
                color,
                fuel_type_id,
                transmission_type_id,
                number_plate_type_id,
                body_style_id,
                seating_capacity,
                undercarriage_type,
                engine_type,
                engine_manufacture,
                initial_odometer_km,
                current_odometer_km,
                total_engine_hours,
                consumption_method_id,
                rated_efficiency_kmpl,
                rated_consumption_lph,
                ownership_type_id,
                (
                    SELECT ot.type_name
                    FROM ownership_type ot
                    WHERE ot.type_id = ownership_type_id
                ) AS ownership_type_name,
                current_ownership,
                previous_owners_count,
                manufacture_id,
                distributor_id,
                (
                    SELECT d.distributor_name
                    FROM distributor d
                    WHERE d.distributor_id = hired_vehicles.distributor_id
                ) AS distributor_name,
                vehicle_condition,
                operational_status_id,
                (
                    SELECT os.status_name
                    FROM operational_status os
                    WHERE os.status_id = operational_status_id
                ) AS operational_status_name,
                (
                    SELECT cm.method_name
                    FROM consumption_method cm
                    WHERE cm.method_id = consumption_method_id
                ) AS consumption_method_name,
                vehicle_status_id,
                notes,
                is_active,
                created_by,
                updated_by,
                created_at,
                updated_at
                """;
    }
}
