package genxsolution.vms.vmsbackend.vehicle_management.manufacturer_category.repository;

import genxsolution.vms.vmsbackend.vehicle_management.manufacturer_category.dto.ManufacturerCategoryUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.manufacturer_category.model.ManufacturerCategory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ManufacturerCategoryRepository {
    private final JdbcTemplate jdbcTemplate;

    public ManufacturerCategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ManufacturerCategory> findAll() {
        return jdbcTemplate.query(
                "SELECT id, manufacturer_id, category_id FROM manufacturer_category ORDER BY manufacturer_id, category_id",
                this::mapRow
        );
    }

    public Optional<ManufacturerCategory> findById(UUID id) {
        return jdbcTemplate.query(
                "SELECT id, manufacturer_id, category_id FROM manufacturer_category WHERE id = ?",
                this::mapRow,
                id
        ).stream().findFirst();
    }

    public ManufacturerCategory create(ManufacturerCategoryUpsertRequest r) {
        String sql = """
                INSERT INTO manufacturer_category (manufacturer_id, category_id)
                VALUES (?, ?)
                RETURNING id, manufacturer_id, category_id
                """;
        return jdbcTemplate.queryForObject(sql, this::mapRow, r.manufacturerId(), r.categoryId());
    }

    public Optional<ManufacturerCategory> update(UUID id, ManufacturerCategoryUpsertRequest r) {
        String sql = """
                UPDATE manufacturer_category
                SET manufacturer_id = COALESCE(?, manufacturer_id),
                    category_id = COALESCE(?, category_id)
                WHERE id = ?
                RETURNING id, manufacturer_id, category_id
                """;
        return jdbcTemplate.query(sql, this::mapRow, r.manufacturerId(), r.categoryId(), id)
                .stream()
                .findFirst();
    }

    public boolean delete(UUID id) {
        return jdbcTemplate.update("DELETE FROM manufacturer_category WHERE id = ?", id) > 0;
    }

    private ManufacturerCategory mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ManufacturerCategory(
                rs.getObject("id", UUID.class),
                rs.getObject("manufacturer_id", UUID.class),
                rs.getObject("category_id", UUID.class)
        );
    }
}
