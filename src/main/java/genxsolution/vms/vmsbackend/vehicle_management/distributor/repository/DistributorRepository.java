package genxsolution.vms.vmsbackend.vehicle_management.distributor.repository;

import genxsolution.vms.vmsbackend.vehicle_management.distributor.dto.DistributorUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.distributor.model.Distributor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class DistributorRepository {
    private final JdbcTemplate jdbcTemplate;

    public DistributorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Distributor> findAll() {
        return jdbcTemplate.query("""
                SELECT distributor_id, manufacturer_id, distributor_name, distributor_country, distributor_location, distributor_logo,
                       distributor_phonenumber, distributor_email, distributor_description, is_active, create_at, update_at
                FROM distributor
                ORDER BY distributor_name
                """, this::mapRow);
    }

    public Optional<Distributor> findById(UUID id) {
        return jdbcTemplate.query("""
                SELECT distributor_id, manufacturer_id, distributor_name, distributor_country, distributor_location, distributor_logo,
                       distributor_phonenumber, distributor_email, distributor_description, is_active, create_at, update_at
                FROM distributor
                WHERE distributor_id = ?
                """, this::mapRow, id).stream().findFirst();
    }

    public Distributor create(DistributorUpsertRequest r) {
        return jdbcTemplate.queryForObject("""
                INSERT INTO distributor (
                    manufacturer_id, distributor_name, distributor_country, distributor_location, distributor_logo,
                    distributor_phonenumber, distributor_email, distributor_description, is_active
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, COALESCE(?, TRUE))
                RETURNING distributor_id, manufacturer_id, distributor_name, distributor_country, distributor_location, distributor_logo,
                          distributor_phonenumber, distributor_email, distributor_description, is_active, create_at, update_at
                """, this::mapRow,
                r.manufacturerId(), r.distributorName(), r.distributorCountry(), r.distributorLocation(), r.distributorLogo(),
                r.distributorPhoneNumber(), r.distributorEmail(), r.distributorDescription(), r.isActive());
    }

    public Optional<Distributor> update(UUID id, DistributorUpsertRequest r) {
        return jdbcTemplate.query("""
                UPDATE distributor
                SET manufacturer_id = COALESCE(?, manufacturer_id),
                    distributor_name = COALESCE(?, distributor_name),
                    distributor_country = COALESCE(?, distributor_country),
                    distributor_location = COALESCE(?, distributor_location),
                    distributor_logo = COALESCE(?, distributor_logo),
                    distributor_phonenumber = COALESCE(?, distributor_phonenumber),
                    distributor_email = COALESCE(?, distributor_email),
                    distributor_description = COALESCE(?, distributor_description),
                    is_active = COALESCE(?, is_active)
                WHERE distributor_id = ?
                RETURNING distributor_id, manufacturer_id, distributor_name, distributor_country, distributor_location, distributor_logo,
                          distributor_phonenumber, distributor_email, distributor_description, is_active, create_at, update_at
                """, this::mapRow,
                r.manufacturerId(), r.distributorName(), r.distributorCountry(), r.distributorLocation(), r.distributorLogo(),
                r.distributorPhoneNumber(), r.distributorEmail(), r.distributorDescription(), r.isActive(), id
        ).stream().findFirst();
    }

    public boolean delete(UUID id) {
        return jdbcTemplate.update("DELETE FROM distributor WHERE distributor_id = ?", id) > 0;
    }

    private Distributor mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp createAtTs = rs.getTimestamp("create_at");
        Timestamp updateAtTs = rs.getTimestamp("update_at");
        return new Distributor(
                rs.getObject("distributor_id", UUID.class),
                rs.getObject("manufacturer_id", UUID.class),
                rs.getString("distributor_name"),
                rs.getString("distributor_country"),
                rs.getString("distributor_location"),
                rs.getString("distributor_logo"),
                rs.getString("distributor_phonenumber"),
                rs.getString("distributor_email"),
                rs.getString("distributor_description"),
                rs.getObject("is_active", Boolean.class),
                createAtTs == null ? null : createAtTs.toInstant(),
                updateAtTs == null ? null : updateAtTs.toInstant()
        );
    }
}
