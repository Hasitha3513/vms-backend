package genxsolution.vms.vmsbackend.vehicle_management.vehicle_insurance.repository;

import genxsolution.vms.vmsbackend.vehicle_management.vehicle_insurance.dto.VehicleInsuranceUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_insurance.dto.VehicleInsuranceSupplierOptionResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_insurance.model.VehicleInsurance;
import org.springframework.jdbc.BadSqlGrammarException;
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
public class VehicleInsuranceRepository {
    private final JdbcTemplate jdbcTemplate;

    public VehicleInsuranceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<VehicleInsurance> findAll(UUID companyId, UUID companyVehicleId) {
        StringBuilder sql = new StringBuilder("SELECT " + selectColumns() + " " + fromJoinClause() + " WHERE 1=1 ");
        if (companyId != null) sql.append(" AND vi.company_id = ? ");
        if (companyVehicleId != null) sql.append(" AND vi.companyvehicle_id = ? ");
        sql.append(" ORDER BY vi.updated_at DESC, vi.created_at DESC");

        if (companyId != null && companyVehicleId != null) return jdbcTemplate.query(sql.toString(), this::mapRow, companyId, companyVehicleId);
        if (companyId != null) return jdbcTemplate.query(sql.toString(), this::mapRow, companyId);
        if (companyVehicleId != null) return jdbcTemplate.query(sql.toString(), this::mapRow, companyVehicleId);
        return jdbcTemplate.query(sql.toString(), this::mapRow);
    }

    public Optional<VehicleInsurance> findById(UUID id) {
        return jdbcTemplate.query("SELECT " + selectColumns() + " " + fromJoinClause() + " WHERE vi.insurance_id = ?", this::mapRow, id)
                .stream().findFirst();
    }

    public VehicleInsurance create(VehicleInsuranceUpsertRequest r) {
        String sql = """
                INSERT INTO vehicle_insurance (
                    vehicle_id, company_id, companyvehicle_id, insurance_company, policy_number,
                    insurance_type_id, policy_start_date, policy_expiry_date, idv_amount, premium_amount,
                    payment_mode, payment_date, agent_name, agent_contact, agent_email,
                    nominee_name, add_on_covers, ncb_percent, claim_count, last_claim_date,
                    last_claim_amount, renewal_reminder_days, insurance_status, notes, is_current
                ) VALUES (
                    ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?,
                    ?, COALESCE(?, 30), COALESCE(?, 'Active'), ?, COALESCE(?, TRUE)
                )
                RETURNING insurance_id
                """;

        UUID id = jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getObject("insurance_id", UUID.class),
                r.vehicleId(), r.companyId(), r.companyVehicleId(), r.insuranceCompany(), r.policyNumber(),
                r.insuranceTypeId(), r.policyStartDate(), r.policyExpiryDate(), r.idvAmount(), r.premiumAmount(),
                r.paymentMode(), r.paymentDate(), r.agentName(), r.agentContact(), r.agentEmail(),
                r.nomineeName(), r.addOnCovers(), r.ncbPercent(), r.claimCount(), r.lastClaimDate(),
                r.lastClaimAmount(), r.renewalReminderDays(), r.insuranceStatus(), r.notes(), r.isCurrent()
        ).stream().findFirst().orElseThrow(() -> new IllegalStateException("Failed to create Vehicle Insurance"));

        return findById(id).orElseThrow(() -> new IllegalStateException("Created Vehicle Insurance could not be loaded"));
    }

    public Optional<VehicleInsurance> update(UUID id, VehicleInsuranceUpsertRequest r) {
        String sql = """
                UPDATE vehicle_insurance
                SET vehicle_id = COALESCE(?, vehicle_id),
                    company_id = COALESCE(?, company_id),
                    companyvehicle_id = COALESCE(?, companyvehicle_id),
                    insurance_company = COALESCE(?, insurance_company),
                    policy_number = COALESCE(?, policy_number),
                    insurance_type_id = COALESCE(?, insurance_type_id),
                    policy_start_date = COALESCE(?, policy_start_date),
                    policy_expiry_date = COALESCE(?, policy_expiry_date),
                    idv_amount = COALESCE(?, idv_amount),
                    premium_amount = COALESCE(?, premium_amount),
                    payment_mode = COALESCE(?, payment_mode),
                    payment_date = COALESCE(?, payment_date),
                    agent_name = COALESCE(?, agent_name),
                    agent_contact = COALESCE(?, agent_contact),
                    agent_email = COALESCE(?, agent_email),
                    nominee_name = COALESCE(?, nominee_name),
                    add_on_covers = COALESCE(?, add_on_covers),
                    ncb_percent = COALESCE(?, ncb_percent),
                    claim_count = COALESCE(?, claim_count),
                    last_claim_date = COALESCE(?, last_claim_date),
                    last_claim_amount = COALESCE(?, last_claim_amount),
                    renewal_reminder_days = COALESCE(?, renewal_reminder_days),
                    insurance_status = COALESCE(?, insurance_status),
                    notes = COALESCE(?, notes),
                    is_current = COALESCE(?, is_current)
                WHERE insurance_id = ?
                RETURNING insurance_id
                """;

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getObject("insurance_id", UUID.class),
                r.vehicleId(), r.companyId(), r.companyVehicleId(), r.insuranceCompany(), r.policyNumber(),
                r.insuranceTypeId(), r.policyStartDate(), r.policyExpiryDate(), r.idvAmount(), r.premiumAmount(),
                r.paymentMode(), r.paymentDate(), r.agentName(), r.agentContact(), r.agentEmail(),
                r.nomineeName(), r.addOnCovers(), r.ncbPercent(), r.claimCount(), r.lastClaimDate(),
                r.lastClaimAmount(), r.renewalReminderDays(), r.insuranceStatus(), r.notes(), r.isCurrent(),
                id
        ).stream().findFirst().flatMap(this::findById);
    }

    public boolean delete(UUID id) {
        return jdbcTemplate.update("DELETE FROM vehicle_insurance WHERE insurance_id = ?", id) > 0;
    }

    public boolean companyVehicleBelongsToCompany(UUID companyVehicleId, UUID companyId) {
        List<Integer> rows = jdbcTemplate.query(
                "SELECT 1 FROM company_vehicles WHERE companyvehicle_id = ? AND company_id = ? LIMIT 1",
                (rs, i) -> rs.getInt(1),
                companyVehicleId,
                companyId
        );
        return !rows.isEmpty();
    }

    public Optional<UUID> findVehicleIdByCompanyVehicleId(UUID companyVehicleId) {
        return jdbcTemplate.query(
                """
                SELECT v.vehicle_id
                FROM company_vehicles cv
                LEFT JOIN vehicle v
                  ON (
                       v.chassis_number = cv.chassis_number
                    OR (
                         cv.registration_number IS NOT NULL
                         AND v.registration_number = cv.registration_number
                       )
                  )
                WHERE cv.companyvehicle_id = ?
                ORDER BY v.updated_at DESC NULLS LAST, v.created_at DESC NULLS LAST
                LIMIT 1
                """,
                (rs, i) -> rs.getObject("vehicle_id", UUID.class),
                companyVehicleId
        ).stream().findFirst();
    }

    public Optional<UUID> findCompanyIdByCompanyVehicleId(UUID companyVehicleId) {
        return jdbcTemplate.query(
                "SELECT company_id FROM company_vehicles WHERE companyvehicle_id = ?",
                (rs, i) -> rs.getObject("company_id", UUID.class),
                companyVehicleId
        ).stream().findFirst();
    }

    public Optional<VehicleInsurance> findLatestByCompanyVehicleId(UUID companyVehicleId) {
        return jdbcTemplate.query(
                "SELECT " + selectColumns() + " " + fromJoinClause() + " WHERE vi.companyvehicle_id = ? ORDER BY vi.updated_at DESC, vi.created_at DESC LIMIT 1",
                this::mapRow,
                companyVehicleId
        ).stream().findFirst();
    }

    public int clearCurrentForCompanyVehicle(UUID companyVehicleId, UUID exceptInsuranceId) {
        return jdbcTemplate.update(
                """
                UPDATE vehicle_insurance
                SET is_current = FALSE
                WHERE companyvehicle_id = ?
                  AND (? IS NULL OR insurance_id <> ?)
                """,
                companyVehicleId,
                exceptInsuranceId,
                exceptInsuranceId
        );
    }

    public List<VehicleInsuranceSupplierOptionResponse> findInsuranceSupplierOptions(UUID companyId) {
        try {
            return jdbcTemplate.query(
                    """
                    SELECT s.supplier_id, s.supplier_code, s.supplier_name
                    FROM supplier s
                    WHERE COALESCE(s.is_active, TRUE) = TRUE
                      AND (? IS NULL OR s.company_id = ? OR s.company_id IS NULL)
                    ORDER BY s.supplier_name ASC
                    """,
                    (rs, rowNum) -> new VehicleInsuranceSupplierOptionResponse(
                            rs.getObject("supplier_id", UUID.class),
                            rs.getString("supplier_code"),
                            rs.getString("supplier_name")
                    ),
                    companyId,
                    companyId
            );
        } catch (BadSqlGrammarException ex) {
            return jdbcTemplate.query(
                    """
                    SELECT s.supplier_id, s.supplier_code, s.supplier_name
                    FROM supplier s
                    WHERE COALESCE(s.is_active, TRUE) = TRUE
                    ORDER BY s.supplier_name ASC
                    """,
                    (rs, rowNum) -> new VehicleInsuranceSupplierOptionResponse(
                            rs.getObject("supplier_id", UUID.class),
                            rs.getString("supplier_code"),
                            rs.getString("supplier_name")
                    )
            );
        }
    }

    private VehicleInsurance mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        Date policyStartDate = rs.getDate("policy_start_date");
        Date policyExpiryDate = rs.getDate("policy_expiry_date");
        Date paymentDate = rs.getDate("payment_date");
        Date lastClaimDate = rs.getDate("last_claim_date");

        return new VehicleInsurance(
                rs.getObject("insurance_id", UUID.class),
                rs.getObject("vehicle_id", UUID.class),
                rs.getObject("company_id", UUID.class),
                rs.getObject("companyvehicle_id", UUID.class),
                rs.getString("insurance_company"),
                rs.getString("policy_number"),
                rs.getObject("insurance_type_id", Integer.class),
                policyStartDate == null ? null : policyStartDate.toLocalDate(),
                policyExpiryDate == null ? null : policyExpiryDate.toLocalDate(),
                rs.getBigDecimal("idv_amount"),
                rs.getBigDecimal("premium_amount"),
                rs.getString("payment_mode"),
                paymentDate == null ? null : paymentDate.toLocalDate(),
                rs.getString("agent_name"),
                rs.getString("agent_contact"),
                rs.getString("agent_email"),
                rs.getString("nominee_name"),
                rs.getString("add_on_covers"),
                rs.getBigDecimal("ncb_percent"),
                rs.getObject("claim_count", Integer.class),
                lastClaimDate == null ? null : lastClaimDate.toLocalDate(),
                rs.getBigDecimal("last_claim_amount"),
                rs.getObject("renewal_reminder_days", Integer.class),
                rs.getString("insurance_status"),
                rs.getString("notes"),
                rs.getObject("is_current", Boolean.class),
                createdAtTs == null ? null : createdAtTs.toInstant(),
                updatedAtTs == null ? null : updatedAtTs.toInstant(),
                rs.getString("company_name"),
                rs.getString("companyvehicle_key_number"),
                rs.getString("companyvehicle_registration_number"),
                rs.getString("companyvehicle_chassis_number")
        );
    }

    private String selectColumns() {
        return """
                vi.insurance_id,
                vi.vehicle_id,
                vi.company_id,
                vi.companyvehicle_id,
                vi.insurance_company,
                vi.policy_number,
                vi.insurance_type_id,
                vi.policy_start_date,
                vi.policy_expiry_date,
                vi.idv_amount,
                vi.premium_amount,
                vi.payment_mode,
                vi.payment_date,
                vi.agent_name,
                vi.agent_contact,
                vi.agent_email,
                vi.nominee_name,
                vi.add_on_covers,
                vi.ncb_percent,
                vi.claim_count,
                vi.last_claim_date,
                vi.last_claim_amount,
                vi.renewal_reminder_days,
                vi.insurance_status,
                vi.notes,
                vi.is_current,
                vi.created_at,
                vi.updated_at,
                c.company_name,
                cv.key_number AS companyvehicle_key_number,
                cv.registration_number AS companyvehicle_registration_number,
                cv.chassis_number AS companyvehicle_chassis_number
                """;
    }

    private String fromJoinClause() {
        return """
                FROM vehicle_insurance vi
                LEFT JOIN company c ON c.company_id = vi.company_id
                LEFT JOIN company_vehicles cv ON cv.companyvehicle_id = vi.companyvehicle_id
                """;
    }
}
