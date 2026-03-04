package genxsolution.vms.vmsbackend.vehicle_management.vehicle_model.repository;

import genxsolution.vms.vmsbackend.vehicle_management.vehicle_model.dto.VehicleModelUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_model.model.VehicleModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class VehicleModelRepository {
    private final JdbcTemplate jdbcTemplate;

    public VehicleModelRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<VehicleModel> findAll() {
        return jdbcTemplate.query(
                """
                SELECT vm.model_id,
                       vm.manufacturer_category_id,
                       vt.category_id,
                       vm.manufacturer_id, vmfr.country AS manufacturer_country, vm.type_id, vm.model_name, vm.model_code, vm.model_year, vm.body_style_id, vm.fuel_type_id,
                       vm.engine_capacity_cc, vm.power_hp, vm.torque_nm, vm.number_of_cylinders, vm.transmission_type_id, vm.drivetrain_type_id,
                       vm.seating_capacity, vm.number_of_doors, vm.kerb_weight_kg, vm.gvw_kg, vm.wheelbase_mm, vm.fuel_efficiency_kmpl,
                       vm.launch_year, vm.description, vm.image_url, vm.is_active, vm.created_at, vm.updated_at
                FROM vehicle_model vm
                LEFT JOIN vehicle_type vt ON vt.type_id = vm.type_id
                LEFT JOIN vehicle_manufacturer vmfr ON vmfr.manufacturer_id = vm.manufacturer_id
                ORDER BY vm.model_name, vm.model_year DESC NULLS LAST
                """,
                this::mapRow
        );
    }

    public Optional<VehicleModel> findById(UUID id) {
        return jdbcTemplate.query(
                """
                SELECT vm.model_id,
                       vm.manufacturer_category_id,
                       vt.category_id,
                       vm.manufacturer_id, vmfr.country AS manufacturer_country, vm.type_id, vm.model_name, vm.model_code, vm.model_year, vm.body_style_id, vm.fuel_type_id,
                       vm.engine_capacity_cc, vm.power_hp, vm.torque_nm, vm.number_of_cylinders, vm.transmission_type_id, vm.drivetrain_type_id,
                       vm.seating_capacity, vm.number_of_doors, vm.kerb_weight_kg, vm.gvw_kg, vm.wheelbase_mm, vm.fuel_efficiency_kmpl,
                       vm.launch_year, vm.description, vm.image_url, vm.is_active, vm.created_at, vm.updated_at
                FROM vehicle_model vm
                LEFT JOIN vehicle_type vt ON vt.type_id = vm.type_id
                LEFT JOIN vehicle_manufacturer vmfr ON vmfr.manufacturer_id = vm.manufacturer_id
                WHERE vm.model_id = ?
                """,
                this::mapRow,
                id
        ).stream().findFirst();
    }

    public VehicleModel create(VehicleModelUpsertRequest r) {
        UUID manufacturerCategoryId = resolveManufacturerCategoryId(r.manufacturerCategoryId(), r.categoryId(), r.manufacturerId());
        UUID modelId = UUID.randomUUID();
        String sql = """
                INSERT INTO vehicle_model (
                    model_id, manufacturer_category_id, manufacturer_id, type_id, model_name, model_code, model_year, body_style_id, fuel_type_id,
                    engine_capacity_cc, power_hp, torque_nm, number_of_cylinders, transmission_type_id, drivetrain_type_id,
                    seating_capacity, number_of_doors, kerb_weight_kg, gvw_kg, wheelbase_mm, fuel_efficiency_kmpl,
                    launch_year, description, image_url, is_active
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, COALESCE(?, TRUE))
                """;
        int inserted = jdbcTemplate.update(
                sql,
                modelId, manufacturerCategoryId, r.manufacturerId(), r.typeId(), r.modelName(), r.modelCode(), r.modelYear(), r.bodyStyleId(), r.fuelTypeId(),
                r.engineCapacityCc(), r.powerHp(), r.torqueNm(), r.numberOfCylinders(), r.transmissionTypeId(), r.drivetrainTypeId(),
                r.seatingCapacity(), r.numberOfDoors(), r.kerbWeightKg(), r.gvwKg(), r.wheelbaseMm(), r.fuelEfficiencyKmpl(),
                r.launchYear(), r.description(), r.imageUrl(), r.isActive()
        );
        if (inserted == 0) throw new IllegalArgumentException("Unable to create VehicleModel");
        return findById(modelId).orElseThrow(() -> new IllegalArgumentException("Unable to load created VehicleModel"));
    }

    public Optional<VehicleModel> update(UUID id, VehicleModelUpsertRequest r) {
        UUID manufacturerCategoryId = resolveManufacturerCategoryId(r.manufacturerCategoryId(), r.categoryId(), r.manufacturerId());
        String sql = """
                UPDATE vehicle_model
                SET manufacturer_category_id = COALESCE(?, manufacturer_category_id),
                    manufacturer_id = COALESCE(?, manufacturer_id),
                    type_id = COALESCE(?, type_id),
                    model_name = COALESCE(?, model_name),
                    model_code = COALESCE(?, model_code),
                    model_year = COALESCE(?, model_year),
                    body_style_id = COALESCE(?, body_style_id),
                    fuel_type_id = COALESCE(?, fuel_type_id),
                    engine_capacity_cc = COALESCE(?, engine_capacity_cc),
                    power_hp = COALESCE(?, power_hp),
                    torque_nm = COALESCE(?, torque_nm),
                    number_of_cylinders = COALESCE(?, number_of_cylinders),
                    transmission_type_id = COALESCE(?, transmission_type_id),
                    drivetrain_type_id = COALESCE(?, drivetrain_type_id),
                    seating_capacity = COALESCE(?, seating_capacity),
                    number_of_doors = COALESCE(?, number_of_doors),
                    kerb_weight_kg = COALESCE(?, kerb_weight_kg),
                    gvw_kg = COALESCE(?, gvw_kg),
                    wheelbase_mm = COALESCE(?, wheelbase_mm),
                    fuel_efficiency_kmpl = COALESCE(?, fuel_efficiency_kmpl),
                    launch_year = COALESCE(?, launch_year),
                    description = COALESCE(?, description),
                    image_url = COALESCE(?, image_url),
                    is_active = COALESCE(?, is_active)
                WHERE model_id = ?
                """;
        int updated = jdbcTemplate.update(
                sql,
                manufacturerCategoryId, r.manufacturerId(), r.typeId(), r.modelName(), r.modelCode(), r.modelYear(), r.bodyStyleId(), r.fuelTypeId(),
                r.engineCapacityCc(), r.powerHp(), r.torqueNm(), r.numberOfCylinders(), r.transmissionTypeId(), r.drivetrainTypeId(),
                r.seatingCapacity(), r.numberOfDoors(), r.kerbWeightKg(), r.gvwKg(), r.wheelbaseMm(), r.fuelEfficiencyKmpl(),
                r.launchYear(), r.description(), r.imageUrl(), r.isActive(), id
        );
        if (updated == 0) return Optional.empty();
        return findById(id);
    }

    public boolean delete(UUID id) {
        return jdbcTemplate.update("DELETE FROM vehicle_model WHERE model_id = ?", id) > 0;
    }

    private UUID resolveManufacturerCategoryId(UUID requestedManufacturerCategoryId, UUID categoryId, UUID manufacturerId) {
        if (requestedManufacturerCategoryId != null) {
            return requestedManufacturerCategoryId;
        }
        if (categoryId == null || manufacturerId == null) {
            return null;
        }
        List<UUID> ids = jdbcTemplate.query(
                "SELECT id FROM manufacturer_category WHERE category_id = ? AND manufacturer_id = ?",
                (rs, i) -> rs.getObject(1, UUID.class),
                categoryId, manufacturerId
        );
        if (!ids.isEmpty()) {
            return ids.get(0);
        }
        throw new IllegalArgumentException("Selected Manufacturer is not mapped to the selected Vehicle Category");
    }

    private VehicleModel mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        return new VehicleModel(
                rs.getObject("model_id", UUID.class),
                rs.getObject("manufacturer_category_id", UUID.class),
                rs.getObject("category_id", UUID.class),
                rs.getObject("manufacturer_id", UUID.class),
                rs.getString("manufacturer_country"),
                rs.getObject("type_id", UUID.class),
                rs.getString("model_name"),
                rs.getString("model_code"),
                rs.getObject("model_year", Integer.class),
                rs.getObject("body_style_id", Integer.class),
                rs.getObject("fuel_type_id", Integer.class),
                rs.getObject("engine_capacity_cc", Integer.class),
                rs.getObject("power_hp", Integer.class),
                rs.getObject("torque_nm", Integer.class),
                rs.getObject("number_of_cylinders", Integer.class),
                rs.getObject("transmission_type_id", Integer.class),
                rs.getObject("drivetrain_type_id", Integer.class),
                rs.getObject("seating_capacity", Integer.class),
                rs.getObject("number_of_doors", Integer.class),
                rs.getBigDecimal("kerb_weight_kg"),
                rs.getBigDecimal("gvw_kg"),
                rs.getObject("wheelbase_mm", Integer.class),
                rs.getBigDecimal("fuel_efficiency_kmpl"),
                rs.getObject("launch_year", Integer.class),
                rs.getString("description"),
                rs.getString("image_url"),
                rs.getObject("is_active", Boolean.class),
                createdAtTs == null ? null : createdAtTs.toInstant(),
                updatedAtTs == null ? null : updatedAtTs.toInstant()
        );
    }
}
