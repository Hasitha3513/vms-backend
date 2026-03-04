package genxsolution.vms.vmsbackend.vehicle_management.vehicle_category.repository;

import genxsolution.vms.vmsbackend.vehicle_management.vehicle_category.dto.VehicleCategoryUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_category.model.VehicleCategory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class VehicleCategoryRepository {
    private final JdbcTemplate jdbcTemplate;

    public VehicleCategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<VehicleCategory> findAll() {
        String sql = "SELECT category_id, category_name, category_code, category_type_id, description, icon_url, is_active, created_at, updated_at FROM vehicle_category ORDER BY category_name";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    public List<VehicleCategory> findOptions(String q, boolean activeOnly, int limit) {
        String normalized = q == null ? null : q.trim();
        String sql = """
                SELECT category_id, category_name, category_code, category_type_id, description, icon_url, is_active, created_at, updated_at
                FROM vehicle_category
                WHERE (? IS NULL OR category_name ILIKE '%' || ? || '%')
                  AND (? = FALSE OR COALESCE(is_active, TRUE) = TRUE)
                ORDER BY category_name
                LIMIT ?
                """;
        return jdbcTemplate.query(sql, this::mapRow, normalized, normalized, activeOnly, limit);
    }

    public Optional<VehicleCategory> findById(UUID id) {
        return jdbcTemplate.query(
                        "SELECT category_id, category_name, category_code, category_type_id, description, icon_url, is_active, created_at, updated_at FROM vehicle_category WHERE category_id = ?",
                        this::mapRow,
                        id
                )
                .stream()
                .findFirst();
    }

    public VehicleCategory create(VehicleCategoryUpsertRequest r) {
        String sql = """
                INSERT INTO vehicle_category (category_name, category_code, category_type_id, description, icon_url, is_active)
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING category_id, category_name, category_code, category_type_id, description, icon_url, is_active, created_at, updated_at
                """;
        boolean isActive = r.isActive() == null || r.isActive();
        return jdbcTemplate.queryForObject(
                sql,
                this::mapRow,
                r.categoryName(),
                r.categoryCode(),
                r.categoryTypeId(),
                r.description(),
                r.iconUrl(),
                isActive
        );
    }

    public Optional<VehicleCategory> update(UUID id, VehicleCategoryUpsertRequest r) {
        String sql = """
                UPDATE vehicle_category
                SET category_name = COALESCE(?, category_name),
                    category_code = COALESCE(?, category_code),
                    category_type_id = COALESCE(?, category_type_id),
                    description = COALESCE(?, description),
                    icon_url = COALESCE(?, icon_url),
                    is_active = COALESCE(?, is_active)
                WHERE category_id = ?
                RETURNING category_id, category_name, category_code, category_type_id, description, icon_url, is_active, created_at, updated_at
                """;
        return jdbcTemplate.query(
                        sql,
                        this::mapRow,
                        r.categoryName(),
                        r.categoryCode(),
                        r.categoryTypeId(),
                        r.description(),
                        r.iconUrl(),
                        r.isActive(),
                        id
                )
                .stream()
                .findFirst();
    }

    public boolean delete(UUID id) {
        return jdbcTemplate.update("DELETE FROM vehicle_category WHERE category_id = ?", id) > 0;
    }

    private VehicleCategory mapRow(ResultSet rs, int n) throws SQLException {
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        return new VehicleCategory(
                rs.getObject("category_id", UUID.class),
                rs.getString("category_name"),
                rs.getString("category_code"),
                rs.getObject("category_type_id", Integer.class),
                rs.getString("description"),
                rs.getString("icon_url"),
                rs.getObject("is_active", Boolean.class),
                createdAtTs == null ? null : createdAtTs.toInstant(),
                updatedAtTs == null ? null : updatedAtTs.toInstant()
        );
    }
}
