package genxsolution.vms.vmsbackend.vehicle_management.vehicle_model_variant.repository;

import genxsolution.vms.vmsbackend.vehicle_management.vehicle_model_variant.dto.VehicleModelVariantUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_model_variant.model.VehicleModelVariant;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class VehicleModelVariantRepository {
    private final JdbcTemplate jdbcTemplate;

    public VehicleModelVariantRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<VehicleModelVariant> findAll() {
        return jdbcTemplate.query(
                """
                SELECT variant_id, model_id, variant_name, variant_code, additional_features, price_ex_showroom, is_active, created_at
                FROM vehicle_model_variant
                ORDER BY variant_name
                """,
                this::mapRow
        );
    }

    public Optional<VehicleModelVariant> findById(UUID id) {
        return jdbcTemplate.query(
                """
                SELECT variant_id, model_id, variant_name, variant_code, additional_features, price_ex_showroom, is_active, created_at
                FROM vehicle_model_variant
                WHERE variant_id = ?
                """,
                this::mapRow,
                id
        ).stream().findFirst();
    }

    public VehicleModelVariant create(VehicleModelVariantUpsertRequest r) {
        String sql = """
                INSERT INTO vehicle_model_variant (model_id, variant_name, variant_code, additional_features, price_ex_showroom, is_active)
                VALUES (?, ?, ?, ?, ?, COALESCE(?, TRUE))
                RETURNING variant_id, model_id, variant_name, variant_code, additional_features, price_ex_showroom, is_active, created_at
                """;
        return jdbcTemplate.queryForObject(
                sql,
                this::mapRow,
                r.modelId(), r.variantName(), r.variantCode(), r.additionalFeatures(), r.priceExShowroom(), r.isActive()
        );
    }

    public Optional<VehicleModelVariant> update(UUID id, VehicleModelVariantUpsertRequest r) {
        String sql = """
                UPDATE vehicle_model_variant
                SET model_id = COALESCE(?, model_id),
                    variant_name = COALESCE(?, variant_name),
                    variant_code = COALESCE(?, variant_code),
                    additional_features = COALESCE(?, additional_features),
                    price_ex_showroom = COALESCE(?, price_ex_showroom),
                    is_active = COALESCE(?, is_active)
                WHERE variant_id = ?
                RETURNING variant_id, model_id, variant_name, variant_code, additional_features, price_ex_showroom, is_active, created_at
                """;
        return jdbcTemplate.query(
                sql,
                this::mapRow,
                r.modelId(), r.variantName(), r.variantCode(), r.additionalFeatures(), r.priceExShowroom(), r.isActive(), id
        ).stream().findFirst();
    }

    public boolean delete(UUID id) {
        return jdbcTemplate.update("DELETE FROM vehicle_model_variant WHERE variant_id = ?", id) > 0;
    }

    private VehicleModelVariant mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        return new VehicleModelVariant(
                rs.getObject("variant_id", UUID.class),
                rs.getObject("model_id", UUID.class),
                rs.getString("variant_name"),
                rs.getString("variant_code"),
                rs.getString("additional_features"),
                rs.getBigDecimal("price_ex_showroom"),
                rs.getObject("is_active", Boolean.class),
                createdAtTs == null ? null : createdAtTs.toInstant()
        );
    }
}
