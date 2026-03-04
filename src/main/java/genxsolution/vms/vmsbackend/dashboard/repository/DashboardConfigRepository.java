package genxsolution.vms.vmsbackend.dashboard.repository;

import genxsolution.vms.vmsbackend.dashboard.dto.DashboardConfigUpsertRequest;
import genxsolution.vms.vmsbackend.dashboard.model.DashboardConfig;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class DashboardConfigRepository {
    private final JdbcTemplate jdbcTemplate;
    public DashboardConfigRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public List<DashboardConfig> findAll() {
        return jdbcTemplate.query("SELECT config_id, company_id, company_code, user_id, role_id, widget_name, widget_type, widget_title, position_x, position_y, width, height, is_visible, refresh_interval, config_data, created_at, updated_at FROM dashboard_config ORDER BY config_id", this::mapRow);
    }
    public Optional<DashboardConfig> findById(UUID id) {
        return jdbcTemplate.query("SELECT config_id, company_id, company_code, user_id, role_id, widget_name, widget_type, widget_title, position_x, position_y, width, height, is_visible, refresh_interval, config_data, created_at, updated_at FROM dashboard_config WHERE config_id = ?", this::mapRow, id).stream().findFirst();
    }
    public List<DashboardConfig> findByUserId(UUID userId) {
        return jdbcTemplate.query("SELECT config_id, company_id, company_code, user_id, role_id, widget_name, widget_type, widget_title, position_x, position_y, width, height, is_visible, refresh_interval, config_data, created_at, updated_at FROM dashboard_config WHERE user_id = ? ORDER BY updated_at DESC", this::mapRow, userId);
    }
    public List<DashboardConfig> findEffectiveByUser(UUID userId, String companyCode, boolean includeGlobal) {
        StringBuilder sql = new StringBuilder("""
                SELECT config_id, company_id, company_code, user_id, role_id, widget_name, widget_type, widget_title,
                       position_x, position_y, width, height, is_visible, refresh_interval, config_data, created_at, updated_at
                FROM dashboard_config dc
                WHERE (
                    dc.user_id = ?
                    OR dc.role_id IN (
                        SELECT ur.role_id
                        FROM user_role ur
                        WHERE ur.user_id = ?
                          AND ur.is_active = TRUE
                          AND (ur.expires_at IS NULL OR ur.expires_at > ?)
                    )
                    OR (dc.user_id IS NULL AND dc.role_id IS NULL)
                )
                AND (
                    dc.company_code = ?
                """);
        if (includeGlobal) {
            sql.append(" OR dc.company_code IS NULL ");
        }
        sql.append("""
                )
                ORDER BY
                    CASE
                        WHEN dc.user_id = ? THEN 3
                        WHEN dc.role_id IN (
                            SELECT ur.role_id
                            FROM user_role ur
                            WHERE ur.user_id = ?
                              AND ur.is_active = TRUE
                              AND (ur.expires_at IS NULL OR ur.expires_at > ?)
                        ) THEN 2
                        ELSE 1
                    END DESC,
                    dc.updated_at DESC
                """);
        Timestamp now = Timestamp.from(Instant.now());
        List<Object> args = new ArrayList<>();
        args.add(userId);
        args.add(userId);
        args.add(now);
        args.add(companyCode);
        args.add(userId);
        args.add(userId);
        args.add(now);
        return jdbcTemplate.query(sql.toString(), this::mapRow, args.toArray());
    }
    public DashboardConfig create(DashboardConfigUpsertRequest r) {
        String sql = "INSERT INTO dashboard_config (company_id, company_code, user_id, role_id, widget_name, widget_type, widget_title, position_x, position_y, width, height, is_visible, refresh_interval, config_data) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING config_id, company_id, company_code, user_id, role_id, widget_name, widget_type, widget_title, position_x, position_y, width, height, is_visible, refresh_interval, config_data, created_at, updated_at";
        return jdbcTemplate.queryForObject(sql, this::mapRow, r.company_id(), r.company_code(), r.user_id(), r.role_id(), r.widget_name(), r.widget_type(), r.widget_title(), r.position_x(), r.position_y(), r.width(), r.height(), r.is_visible(), r.refresh_interval(), r.config_data());
    }
    public Optional<DashboardConfig> update(UUID id, DashboardConfigUpsertRequest r) {
        String sql = """
                UPDATE dashboard_config
                SET company_id = COALESCE(?, company_id),
                    company_code = COALESCE(?, company_code),
                    user_id = COALESCE(?, user_id),
                    role_id = COALESCE(?, role_id),
                    widget_name = COALESCE(?, widget_name),
                    widget_type = COALESCE(?, widget_type),
                    widget_title = COALESCE(?, widget_title),
                    position_x = COALESCE(?, position_x),
                    position_y = COALESCE(?, position_y),
                    width = COALESCE(?, width),
                    height = COALESCE(?, height),
                    is_visible = COALESCE(?, is_visible),
                    refresh_interval = COALESCE(?, refresh_interval),
                    config_data = COALESCE(?, config_data)
                WHERE config_id = ?
                RETURNING config_id, company_id, company_code, user_id, role_id, widget_name, widget_type, widget_title, position_x, position_y, width, height, is_visible, refresh_interval, config_data, created_at, updated_at
                """;
        return jdbcTemplate.query(sql, this::mapRow, r.company_id(), r.company_code(), r.user_id(), r.role_id(), r.widget_name(), r.widget_type(), r.widget_title(), r.position_x(), r.position_y(), r.width(), r.height(), r.is_visible(), r.refresh_interval(), r.config_data(), id).stream().findFirst();
    }
    public boolean delete(UUID id) { return jdbcTemplate.update("DELETE FROM dashboard_config WHERE config_id = ?", id) > 0; }
    private DashboardConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new DashboardConfig(
                rs.getObject("config_id", java.util.UUID.class),
                rs.getObject("company_id", java.util.UUID.class),
                rs.getString("company_code"),
                rs.getObject("user_id", java.util.UUID.class),
                rs.getObject("role_id", java.util.UUID.class),
                rs.getString("widget_name"),
                rs.getString("widget_type"),
                rs.getString("widget_title"),
                rs.getObject("position_x", java.lang.Integer.class),
                rs.getObject("position_y", java.lang.Integer.class),
                rs.getObject("width", java.lang.Integer.class),
                rs.getObject("height", java.lang.Integer.class),
                rs.getObject("is_visible", java.lang.Boolean.class),
                rs.getObject("refresh_interval", java.lang.Integer.class),
                rs.getString("config_data"),
                rs.getTimestamp("created_at") == null ? null : rs.getTimestamp("created_at").toInstant(),
                rs.getTimestamp("updated_at") == null ? null : rs.getTimestamp("updated_at").toInstant()
        );
    }
}








