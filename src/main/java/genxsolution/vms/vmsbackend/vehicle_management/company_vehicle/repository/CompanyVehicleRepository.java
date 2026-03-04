package genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.repository;

import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.dto.CompanyVehicleUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.dto.CompanyVehicleCurrentOwnershipOptionResponse;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.dto.CompanyVehicleOwnershipTypeOptionResponse;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.dto.CompanyVehicleTypeCountResponse;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.model.CompanyVehicle;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CompanyVehicleRepository {
    private final JdbcTemplate jdbcTemplate;

    public CompanyVehicleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CompanyVehicle> findAll(UUID companyId) {
        String sql = "SELECT " + selectColumns() + " FROM company_vehicles " +
                (companyId == null ? "" : "WHERE company_id = ? ") +
                "ORDER BY updated_at DESC, created_at DESC";
        return companyId == null ? jdbcTemplate.query(sql, this::mapRow) : jdbcTemplate.query(sql, this::mapRow, companyId);
    }

    public Optional<CompanyVehicle> findById(UUID id) {
        return jdbcTemplate.query("SELECT " + selectColumns() + " FROM company_vehicles WHERE companyvehicle_id = ?", this::mapRow, id)
                .stream().findFirst();
    }

    public Optional<CompanyVehicle> findFirst(UUID companyId) {
        String sql = "SELECT " + selectColumns() + " FROM company_vehicles " +
                (companyId == null ? "" : "WHERE company_id = ? ") +
                "ORDER BY updated_at DESC, created_at DESC LIMIT 1";
        return (companyId == null ? jdbcTemplate.query(sql, this::mapRow) : jdbcTemplate.query(sql, this::mapRow, companyId))
                .stream().findFirst();
    }

    public CompanyVehicle create(CompanyVehicleUpsertRequest r) {
        String sql = """
                INSERT INTO company_vehicles (
                    company_id, company_code, company_project, company_branch, company_department,
                    companyvehicle_type, companyvehicle_model,
                    companyvehicle_category, companyvehicle_manufacture,
                    registration_number, chassis_number, engine_number, key_number, vehicle_image,
                    manufacture_year, color, fuel_type_id, transmission_type_id, number_plate_type_id,
                    body_style_id, seating_capacity, undercarriage_type, engine_type, engine_manufacture,
                    initial_odometer_km, current_odometer_km, total_engine_hours,
                    consumption_method_id, rated_efficiency_kmpl, rated_consumption_lph,
                    ownership_type_id, current_ownership, previous_owners_count, manufacture_id, distributor_id,
                    vehicle_condition, operational_status_id, vehicle_status_id,
                    notes, is_active, created_by, updated_by
                )
                VALUES (
                    ?, ?, ?, ?, ?,
                    ?, ?,
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
                r.companyId(), r.companyCode(), r.companyProject(), r.companyBranch(), r.companyDepartment(),
                r.companyVehicleType(), r.companyVehicleModel(),
                r.categoryId(), r.companyVehicleManufacture(),
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

    public Optional<CompanyVehicle> update(UUID id, CompanyVehicleUpsertRequest r) {
        String sql = """
                UPDATE company_vehicles
                SET company_id = COALESCE(?, company_id),
                    company_code = COALESCE(?, company_code),
                    company_project = COALESCE(?, company_project),
                    company_branch = COALESCE(?, company_branch),
                    company_department = COALESCE(?, company_department),
                    companyvehicle_type = COALESCE(?, companyvehicle_type),
                    companyvehicle_model = COALESCE(?, companyvehicle_model),
                    companyvehicle_category = COALESCE(?, companyvehicle_category),
                    companyvehicle_manufacture = COALESCE(?, companyvehicle_manufacture),
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
                WHERE companyvehicle_id = ?
                RETURNING %s
                """.formatted(selectColumns());

        return jdbcTemplate.query(sql, this::mapRow,
                r.companyId(), r.companyCode(), r.companyProject(), r.companyBranch(), r.companyDepartment(),
                r.companyVehicleType(), r.companyVehicleModel(),
                r.categoryId(), r.companyVehicleManufacture(),
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
        return jdbcTemplate.update("DELETE FROM company_vehicles WHERE companyvehicle_id = ?", id) > 0;
    }

    public Optional<UUID> findProjectBranchForCompany(UUID projectId, UUID companyId) {
        return jdbcTemplate.query(
                "SELECT branch_id FROM project WHERE project_id = ? AND company_id = ?",
                (rs, i) -> rs.getObject("branch_id", UUID.class),
                projectId,
                companyId
        ).stream().findFirst();
    }

    public boolean departmentBelongsToCompany(UUID departmentId, UUID companyId) {
        List<Integer> rows = jdbcTemplate.query(
                "SELECT 1 FROM department WHERE department_id = ? AND company_id = ? LIMIT 1",
                (rs, i) -> rs.getInt(1),
                departmentId,
                companyId
        );
        return !rows.isEmpty();
    }

    public boolean departmentBelongsToBranch(UUID departmentId, UUID branchId) {
        List<Integer> rows = jdbcTemplate.query(
                "SELECT 1 FROM department WHERE department_id = ? AND branch_id = ? LIMIT 1",
                (rs, i) -> rs.getInt(1),
                departmentId,
                branchId
        );
        return !rows.isEmpty();
    }

    public Optional<String> findCompanyTypeIdentifierCode(UUID companyId, UUID typeId) {
        return jdbcTemplate.query(
                """
                SELECT idtype_code AS type_identifier
                FROM companyvehicleidtype
                WHERE idtype_com = ?
                  AND idtype_typeid = ?
                  AND is_active = TRUE
                ORDER BY updated_at DESC
                LIMIT 1
                """,
                (rs, i) -> rs.getString("type_identifier"),
                companyId,
                typeId
        ).stream().filter(Objects::nonNull).findFirst();
    }

    public List<String> findIdentificationCodesByCompanyAndType(UUID companyId, UUID typeId) {
        return jdbcTemplate.query(
                """
                SELECT key_number
                FROM company_vehicles
                WHERE company_id = ?
                  AND companyvehicle_type = ?
                  AND key_number IS NOT NULL
                """,
                (rs, i) -> rs.getString("key_number"),
                companyId,
                typeId
        );
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

    public Optional<Integer> findOwnershipTypeIdByCode(String typeCode) {
        return jdbcTemplate.query(
                """
                SELECT type_id
                FROM ownership_type
                WHERE UPPER(REPLACE(REPLACE(REPLACE(COALESCE(type_code, ''), ' ', ''), '_', ''), '-', '')) =
                      UPPER(REPLACE(REPLACE(REPLACE(COALESCE(?, ''), ' ', ''), '_', ''), '-', ''))
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

    public Optional<String> findCompanyNameById(UUID companyId) {
        return jdbcTemplate.query(
                "SELECT company_name FROM company WHERE company_id = ?",
                (rs, i) -> rs.getString("company_name"),
                companyId
        ).stream().findFirst();
    }

    public List<CompanyVehicleOwnershipTypeOptionResponse> findOwnedAndLeasedOwnershipTypes() {
        return jdbcTemplate.query(
                """
                SELECT type_id, type_code, type_name
                FROM ownership_type
                WHERE COALESCE(is_active, TRUE) = TRUE
                  AND UPPER(REPLACE(REPLACE(REPLACE(COALESCE(type_code, ''), ' ', ''), '_', ''), '-', ''))
                      IN ('OWNED', 'COMPANYOWNED', 'PERSONALOWNED', 'LEASED')
                ORDER BY
                  CASE
                    WHEN UPPER(REPLACE(REPLACE(REPLACE(COALESCE(type_code, ''), ' ', ''), '_', ''), '-', '')) IN ('COMPANYOWNED', 'OWNED') THEN 0
                    WHEN UPPER(REPLACE(REPLACE(REPLACE(COALESCE(type_code, ''), ' ', ''), '_', ''), '-', '')) = 'PERSONALOWNED' THEN 1
                    WHEN UPPER(REPLACE(REPLACE(REPLACE(COALESCE(type_code, ''), ' ', ''), '_', ''), '-', '')) = 'LEASED' THEN 2
                    ELSE 9
                  END,
                  type_name ASC
                """,
                (rs, i) -> new CompanyVehicleOwnershipTypeOptionResponse(
                        rs.getObject("type_id", Integer.class),
                        rs.getString("type_code"),
                        rs.getString("type_name")
                )
        );
    }

    public List<CompanyVehicleCurrentOwnershipOptionResponse> findLeasedCurrentOwnershipOptions(UUID companyId) {
        String sql = """
                SELECT s.supplier_id, s.supplier_code, s.supplier_name
                FROM supplier s
                LEFT JOIN supplier_type st ON st.type_id = s.supplier_type_id
                WHERE COALESCE(s.is_active, TRUE) = TRUE
                  AND (?::uuid IS NULL OR s.company_id = ?::uuid)
                  AND UPPER(COALESCE(st.type_code, '')) NOT LIKE '%INSURANCE%'
                  AND LOWER(COALESCE(st.type_name, '')) NOT LIKE '%insurance%'
                  AND LOWER(COALESCE(s.supplier_name, '')) NOT LIKE '%insurance%'
                  AND (
                    UPPER(COALESCE(st.type_code, '')) IN ('LEASING', 'LEASING_COMPANY', 'LEASING_PROVIDER', 'FINANCE', 'FINANCE_COMPANY', 'FINANCE_PROVIDER', 'FINACE', 'FINACE_COMPANY', 'FINACE_PROVIDER')
                    OR LOWER(COALESCE(st.type_name, '')) LIKE '%leasing%'
                    OR LOWER(COALESCE(st.type_name, '')) LIKE '%finance%'
                    OR LOWER(COALESCE(st.type_name, '')) LIKE '%finace%'
                    OR LOWER(COALESCE(s.supplier_name, '')) LIKE '%leasing%'
                    OR LOWER(COALESCE(s.supplier_name, '')) LIKE '%finance%'
                    OR LOWER(COALESCE(s.supplier_name, '')) LIKE '%finace%'
                  )
                ORDER BY s.supplier_name ASC
                """;
        return jdbcTemplate.query(
                sql,
                (rs, i) -> new CompanyVehicleCurrentOwnershipOptionResponse(
                        rs.getObject("supplier_id", UUID.class),
                        rs.getString("supplier_code"),
                        rs.getString("supplier_name")
                ),
                companyId,
                companyId
        );
    }

    public Optional<CompanyVehicleCurrentOwnershipOptionResponse> findOwnedCurrentOwnershipOption(UUID companyId) {
        return jdbcTemplate.query(
                """
                SELECT company_id, company_code, company_name
                FROM company
                WHERE company_id = ?
                LIMIT 1
                """,
                (rs, i) -> new CompanyVehicleCurrentOwnershipOptionResponse(
                        rs.getObject("company_id", UUID.class),
                        rs.getString("company_code"),
                        rs.getString("company_name")
                ),
                companyId
        ).stream().findFirst();
    }

    public boolean leasedCurrentOwnershipExists(UUID companyId, String ownerName) {
        String sql = """
                SELECT 1
                FROM supplier s
                LEFT JOIN supplier_type st ON st.type_id = s.supplier_type_id
                WHERE COALESCE(s.is_active, TRUE) = TRUE
                  AND (?::uuid IS NULL OR s.company_id = ?::uuid)
                  AND LOWER(s.supplier_name) = LOWER(?)
                  AND UPPER(COALESCE(st.type_code, '')) NOT LIKE '%INSURANCE%'
                  AND LOWER(COALESCE(st.type_name, '')) NOT LIKE '%insurance%'
                  AND LOWER(COALESCE(s.supplier_name, '')) NOT LIKE '%insurance%'
                  AND (
                    UPPER(COALESCE(st.type_code, '')) IN ('LEASING', 'LEASING_COMPANY', 'LEASING_PROVIDER', 'FINANCE', 'FINANCE_COMPANY', 'FINANCE_PROVIDER', 'FINACE', 'FINACE_COMPANY', 'FINACE_PROVIDER')
                    OR LOWER(COALESCE(st.type_name, '')) LIKE '%leasing%'
                    OR LOWER(COALESCE(st.type_name, '')) LIKE '%finance%'
                    OR LOWER(COALESCE(st.type_name, '')) LIKE '%finace%'
                    OR LOWER(COALESCE(s.supplier_name, '')) LIKE '%leasing%'
                    OR LOWER(COALESCE(s.supplier_name, '')) LIKE '%finance%'
                    OR LOWER(COALESCE(s.supplier_name, '')) LIKE '%finace%'
                  )
                LIMIT 1
                """;
        List<Integer> rows = jdbcTemplate.query(sql, (rs, i) -> rs.getInt(1), companyId, companyId, ownerName);
        return !rows.isEmpty();
    }

    public long[] overview(UUID companyId) {
        return jdbcTemplate.queryForObject(
                """
                SELECT
                    COUNT(*) AS total_vehicles,
                    COUNT(*) FILTER (WHERE COALESCE(is_active, FALSE) = TRUE) AS active_vehicles,
                    COUNT(*) FILTER (WHERE COALESCE(is_active, FALSE) = FALSE) AS inactive_vehicles,
                    COUNT(DISTINCT companyvehicle_type) AS total_types,
                    COUNT(DISTINCT companyvehicle_manufacture) AS total_brands,
                    (
                        SELECT COUNT(*)
                        FROM vehicle_insurance vi
                        WHERE (?::uuid IS NULL OR vi.company_id = ?::uuid)
                    ) AS total_insurance_records,
                    (
                        SELECT COUNT(*)
                        FROM vehicle_fitness_certificate vf
                        WHERE (?::uuid IS NULL OR vf.company_id = ?::uuid)
                    ) AS total_fitness_records,
                    (
                        SELECT COUNT(*)
                        FROM vehicle_puc vp
                        WHERE (?::uuid IS NULL OR vp.company_id = ?::uuid)
                    ) AS total_emission_records
                FROM company_vehicles
                WHERE (?::uuid IS NULL OR company_id = ?::uuid)
                """,
                (rs, rowNum) -> new long[] {
                        rs.getLong("total_vehicles"),
                        rs.getLong("active_vehicles"),
                        rs.getLong("inactive_vehicles"),
                        rs.getLong("total_types"),
                        rs.getLong("total_brands"),
                        rs.getLong("total_insurance_records"),
                        rs.getLong("total_fitness_records"),
                        rs.getLong("total_emission_records")
                },
                companyId,
                companyId,
                companyId,
                companyId,
                companyId,
                companyId,
                companyId,
                companyId
        );
    }

    public List<CompanyVehicleTypeCountResponse> overviewByType(UUID companyId) {
        return jdbcTemplate.query(
                """
                SELECT
                    cv.companyvehicle_type AS type_id,
                    COALESCE(vt.type_name, 'Unknown') AS type_name,
                    COUNT(*) AS vehicle_count
                FROM company_vehicles cv
                LEFT JOIN vehicle_type vt ON vt.type_id = cv.companyvehicle_type
                WHERE (?::uuid IS NULL OR cv.company_id = ?::uuid)
                GROUP BY cv.companyvehicle_type, vt.type_name
                ORDER BY vehicle_count DESC, type_name ASC
                """,
                (rs, i) -> new CompanyVehicleTypeCountResponse(
                        rs.getObject("type_id", UUID.class),
                        rs.getString("type_name"),
                        rs.getLong("vehicle_count")
                ),
                companyId,
                companyId
        );
    }

    private CompanyVehicle mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        return new CompanyVehicle(
                rs.getObject("companyvehicle_id", UUID.class),
                rs.getObject("company_id", UUID.class),
                rs.getString("company_code"),
                rs.getObject("company_project", UUID.class),
                rs.getObject("company_branch", UUID.class),
                rs.getObject("company_department", UUID.class),
                rs.getObject("companyvehicle_type", UUID.class),
                rs.getObject("companyvehicle_model", UUID.class),
                rs.getString("companyvehicle_model_name"),
                rs.getObject("companyvehicle_category", UUID.class),
                rs.getObject("companyvehicle_manufacture", UUID.class),
                rs.getString("companyvehicle_manufacture_brand"),
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
                rs.getString("current_ownership"),
                rs.getObject("previous_owners_count", Integer.class),
                rs.getObject("manufacture_id", UUID.class),
                rs.getObject("distributor_id", UUID.class),
                rs.getString("vehicle_condition"),
                rs.getObject("operational_status_id", Integer.class),
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
                companyvehicle_id,
                company_id,
                company_code,
                company_project,
                company_branch,
                company_department,
                companyvehicle_type,
                companyvehicle_model,
                (
                    SELECT vm.model_name
                    FROM vehicle_model vm
                    WHERE vm.model_id = companyvehicle_model
                ) AS companyvehicle_model_name,
                companyvehicle_category,
                companyvehicle_manufacture,
                (
                    SELECT vmfr.manufacturer_brand
                    FROM vehicle_manufacturer vmfr
                    WHERE vmfr.manufacturer_id = companyvehicle_manufacture
                ) AS companyvehicle_manufacture_brand,
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
                current_ownership,
                previous_owners_count,
                manufacture_id,
                distributor_id,
                vehicle_condition,
                operational_status_id,
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
