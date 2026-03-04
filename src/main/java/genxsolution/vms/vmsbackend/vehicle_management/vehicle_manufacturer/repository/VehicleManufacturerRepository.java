package genxsolution.vms.vmsbackend.vehicle_management.vehicle_manufacturer.repository;

import genxsolution.vms.vmsbackend.vehicle_management.vehicle_manufacturer.dto.VehicleManufacturerUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_manufacturer.model.VehicleManufacturer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Repository
public class VehicleManufacturerRepository {
    private final JdbcTemplate jdbcTemplate;

    public VehicleManufacturerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<VehicleManufacturer> findAll() {
        return jdbcTemplate.query(
                "SELECT manufacturer_id, manufacturer_name, manufacturer_code, manufacturer_brand, country, logo_url, website, support_phone, support_email, description, is_active, created_at, updated_at FROM vehicle_manufacturer ORDER BY manufacturer_name, country",
                this::mapRow
        );
    }

    public Optional<VehicleManufacturer> findById(UUID id) {
        return jdbcTemplate.query(
                        "SELECT manufacturer_id, manufacturer_name, manufacturer_code, manufacturer_brand, country, logo_url, website, support_phone, support_email, description, is_active, created_at, updated_at FROM vehicle_manufacturer WHERE manufacturer_id = ?",
                        this::mapRow,
                        id
                )
                .stream()
                .findFirst();
    }

    public VehicleManufacturer create(VehicleManufacturerUpsertRequest r) {
        String sql = """
                INSERT INTO vehicle_manufacturer (manufacturer_name, manufacturer_code, manufacturer_brand, country, logo_url, website, support_phone, support_email, description, is_active)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, COALESCE(?, TRUE))
                RETURNING manufacturer_id, manufacturer_name, manufacturer_code, manufacturer_brand, country, logo_url, website, support_phone, support_email, description, is_active, created_at, updated_at
                """;

        String manufacturerName = required("manufacturerName", r.manufacturerName(), 100);
        String manufacturerCode = required("manufacturerCode", r.manufacturerCode(), 50).toUpperCase(Locale.ROOT);
        String manufacturerBrand = optional("manufacturerBrand", r.manufacturerBrand(), 100);
        String country = required("country", r.country(), 100);
        String logoUrl = optional("logoUrl", r.logoUrl(), null);
        String website = optional("website", r.website(), 200);
        String supportPhone = optional("supportPhone", r.supportPhone(), 20);
        String supportEmail = optional("supportEmail", r.supportEmail(), 100);
        String description = optional("description", r.description(), null);
        ensureNameCountryUnique(manufacturerName, country, null);

        return jdbcTemplate.queryForObject(
                sql,
                this::mapRow,
                manufacturerName,
                manufacturerCode,
                manufacturerBrand,
                country,
                logoUrl,
                website,
                supportPhone,
                supportEmail,
                description,
                r.isActive()
        );
    }

    public Optional<VehicleManufacturer> update(UUID id, VehicleManufacturerUpsertRequest r) {
        String sql = """
                UPDATE vehicle_manufacturer
                SET manufacturer_name = COALESCE(?, manufacturer_name),
                    manufacturer_code = COALESCE(?, manufacturer_code),
                    manufacturer_brand = COALESCE(?, manufacturer_brand),
                    country = COALESCE(?, country),
                    logo_url = COALESCE(?, logo_url),
                    website = COALESCE(?, website),
                    support_phone = COALESCE(?, support_phone),
                    support_email = COALESCE(?, support_email),
                    description = COALESCE(?, description),
                    is_active = COALESCE(?, is_active)
                WHERE manufacturer_id = ?
                RETURNING manufacturer_id, manufacturer_name, manufacturer_code, manufacturer_brand, country, logo_url, website, support_phone, support_email, description, is_active, created_at, updated_at
                """;

        Optional<VehicleManufacturer> existing = findById(id);
        if (existing.isEmpty()) return Optional.empty();

        String manufacturerName = optional("manufacturerName", r.manufacturerName(), 100);
        String manufacturerCode = optional("manufacturerCode", r.manufacturerCode(), 50);
        if (manufacturerCode != null) manufacturerCode = manufacturerCode.toUpperCase(Locale.ROOT);
        String manufacturerBrand = optional("manufacturerBrand", r.manufacturerBrand(), 100);
        String country = optional("country", r.country(), 100);
        String logoUrl = optional("logoUrl", r.logoUrl(), null);
        String website = optional("website", r.website(), 200);
        String supportPhone = optional("supportPhone", r.supportPhone(), 20);
        String supportEmail = optional("supportEmail", r.supportEmail(), 100);
        String description = optional("description", r.description(), null);
        String resolvedName = manufacturerName != null ? manufacturerName : existing.get().manufacturerName();
        String resolvedCountry = country != null ? country : existing.get().country();
        ensureNameCountryUnique(resolvedName, resolvedCountry, id);

        return jdbcTemplate.query(
                        sql,
                        this::mapRow,
                        manufacturerName,
                        manufacturerCode,
                        manufacturerBrand,
                        country,
                        logoUrl,
                        website,
                        supportPhone,
                        supportEmail,
                        description,
                        r.isActive(),
                        id
                )
                .stream()
                .findFirst();
    }

    public boolean delete(UUID id) {
        return jdbcTemplate.update("DELETE FROM vehicle_manufacturer WHERE manufacturer_id = ?", id) > 0;
    }

    private static String required(String field, String raw, Integer maxLen) {
        String value = optional(field, raw, maxLen);
        if (value == null) throw new IllegalArgumentException(field + " is required");
        return value;
    }

    private static String optional(String field, String raw, Integer maxLen) {
        if (raw == null) return null;
        String value = raw.trim();
        if (value.isEmpty()) return null;
        if (maxLen != null && value.length() > maxLen) {
            throw new IllegalArgumentException(field + " exceeds max length " + maxLen);
        }
        return value;
    }

    private void ensureNameCountryUnique(String manufacturerName, String country, UUID excludeId) {
        Integer exists;
        if (excludeId == null) {
            exists = jdbcTemplate.queryForObject(
                    """
                    SELECT COUNT(1)
                    FROM vehicle_manufacturer
                    WHERE lower(trim(manufacturer_name)) = lower(trim(?))
                      AND lower(trim(country)) = lower(trim(?))
                    """,
                    Integer.class,
                    manufacturerName,
                    country
            );
        } else {
            exists = jdbcTemplate.queryForObject(
                    """
                    SELECT COUNT(1)
                    FROM vehicle_manufacturer
                    WHERE lower(trim(manufacturer_name)) = lower(trim(?))
                      AND lower(trim(country)) = lower(trim(?))
                      AND manufacturer_id <> ?
                    """,
                    Integer.class,
                    manufacturerName,
                    country,
                    excludeId
            );
        }
        if (exists != null && exists > 0) {
            throw new IllegalArgumentException("Manufacturer name already exists for selected country");
        }
    }

    private VehicleManufacturer mapRow(ResultSet rs, int n) throws SQLException {
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        return new VehicleManufacturer(
                rs.getObject("manufacturer_id", UUID.class),
                rs.getString("manufacturer_name"),
                rs.getString("manufacturer_code"),
                rs.getString("manufacturer_brand"),
                rs.getString("country"),
                rs.getString("logo_url"),
                rs.getString("website"),
                rs.getString("support_phone"),
                rs.getString("support_email"),
                rs.getString("description"),
                rs.getObject("is_active", Boolean.class),
                createdAtTs == null ? null : createdAtTs.toInstant(),
                updatedAtTs == null ? null : updatedAtTs.toInstant()
        );
    }
}
