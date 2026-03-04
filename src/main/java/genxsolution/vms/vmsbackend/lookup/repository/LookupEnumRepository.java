package genxsolution.vms.vmsbackend.lookup.repository;

import genxsolution.vms.vmsbackend.lookup.exception.EnumTypeNotSupportedException;
import genxsolution.vms.vmsbackend.lookup.model.LookupEnumRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Repository
public class LookupEnumRepository {

    private final JdbcTemplate jdbcTemplate;
    private final Map<String, EnumTableMeta> supportedEnums;

    public LookupEnumRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.supportedEnums = buildSupportedEnums();
    }

    public List<LookupEnumRecord> findByEnumKey(String enumKey, boolean activeOnly) {
        EnumTableMeta meta = getEnumMeta(enumKey);
        String sql = buildSql(meta, activeOnly);

        return jdbcTemplate.query(sql, (rs, rowNum) -> new LookupEnumRecord(
                rs.getInt("enum_id"),
                rs.getString("enum_code"),
                rs.getString("enum_name"),
                rs.getString("enum_description"),
                rs.getBoolean("enum_active")
        ));
    }

    public List<LookupEnumRecord> findAdminRecords(String enumKey) {
        EnumTableMeta meta = getEnumMeta(enumKey);
        return jdbcTemplate.query(buildAdminSelectSql(meta) + " ORDER BY " + meta.nameColumn(), (rs, rowNum) -> new LookupEnumRecord(
                rs.getInt("enum_id"),
                rs.getString("enum_code"),
                rs.getString("enum_name"),
                rs.getString("enum_description"),
                rs.getBoolean("enum_active")
        ));
    }

    public LookupEnumRecord findAdminRecordById(String enumKey, Integer id) {
        EnumTableMeta meta = getEnumMeta(enumKey);
        String sql = buildAdminSelectSql(meta) + " WHERE " + meta.idColumn() + " = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new LookupEnumRecord(
                rs.getInt("enum_id"),
                rs.getString("enum_code"),
                rs.getString("enum_name"),
                rs.getString("enum_description"),
                rs.getBoolean("enum_active")
        ), id).stream().findFirst().orElse(null);
    }

    public LookupEnumRecord createAdminRecord(String enumKey, String code, String name, String description, Boolean active) {
        EnumTableMeta meta = getEnumMeta(enumKey);
        boolean sameCodeAndNameColumn = meta.codeColumn().equals(meta.nameColumn());
        StringBuilder cols = new StringBuilder();
        StringBuilder vals = new StringBuilder();
        new AppendPair(cols, vals).add(meta.codeColumn(), "?");
        if (!sameCodeAndNameColumn) new AppendPair(cols, vals).add(meta.nameColumn(), "?");
        if (meta.descriptionColumn() != null) new AppendPair(cols, vals).add(meta.descriptionColumn(), "?");
        if (meta.activeColumn() != null) new AppendPair(cols, vals).add(meta.activeColumn(), "?");

        String sql = "INSERT INTO " + meta.tableName() + " (" + cols + ") VALUES (" + vals + ") " + buildReturningClause(meta);
        Object[] args = buildWriteArgs(meta, code, name, description, active);
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new LookupEnumRecord(
                rs.getInt("enum_id"),
                rs.getString("enum_code"),
                rs.getString("enum_name"),
                rs.getString("enum_description"),
                rs.getBoolean("enum_active")
        ), args);
    }

    public LookupEnumRecord updateAdminRecord(String enumKey, Integer id, String code, String name, String description, Boolean active) {
        EnumTableMeta meta = getEnumMeta(enumKey);
        boolean sameCodeAndNameColumn = meta.codeColumn().equals(meta.nameColumn());
        StringBuilder set = new StringBuilder();
        appendSet(set, meta.codeColumn());
        if (!sameCodeAndNameColumn) appendSet(set, meta.nameColumn());
        if (meta.descriptionColumn() != null) appendSet(set, meta.descriptionColumn());
        if (meta.activeColumn() != null) appendSet(set, meta.activeColumn());

        String sql = "UPDATE " + meta.tableName() + " SET " + set + " WHERE " + meta.idColumn() + " = ? " + buildReturningClause(meta);
        Object[] writeArgs = buildWriteArgs(meta, code, name, description, active);
        Object[] args = new Object[writeArgs.length + 1];
        System.arraycopy(writeArgs, 0, args, 0, writeArgs.length);
        args[writeArgs.length] = id;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new LookupEnumRecord(
                rs.getInt("enum_id"),
                rs.getString("enum_code"),
                rs.getString("enum_name"),
                rs.getString("enum_description"),
                rs.getBoolean("enum_active")
        ), args).stream().findFirst().orElse(null);
    }

    public boolean deleteAdminRecord(String enumKey, Integer id) {
        EnumTableMeta meta = getEnumMeta(enumKey);
        return jdbcTemplate.update("DELETE FROM " + meta.tableName() + " WHERE " + meta.idColumn() + " = ?", id) > 0;
    }

    public Map<String, EnumTableMeta> listSupportedEnums() {
        return supportedEnums;
    }

    public EnumTableMeta getEnumMeta(String enumKey) {
        String normalizedKey = normalizeEnumKey(enumKey);
        EnumTableMeta meta = supportedEnums.get(normalizedKey);
        if (meta == null) {
            throw new EnumTypeNotSupportedException(
                    enumKey,
                    supportedEnums.keySet().stream().toList()
            );
        }
        return meta;
    }

    private String buildSql(EnumTableMeta meta, boolean activeOnly) {
        String descriptionSql = meta.descriptionColumn() == null
                ? "NULL AS enum_description"
                : meta.descriptionColumn() + " AS enum_description";
        String activeSql = meta.activeColumn() == null
                ? "TRUE AS enum_active"
                : meta.activeColumn() + " AS enum_active";

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
                .append(meta.idColumn()).append(" AS enum_id, ")
                .append(meta.codeColumn()).append(" AS enum_code, ")
                .append(meta.nameColumn()).append(" AS enum_name, ")
                .append(descriptionSql).append(", ")
                .append(activeSql).append(" ")
                .append("FROM ").append(meta.tableName()).append(" ");

        if (activeOnly && meta.activeColumn() != null) {
            sql.append("WHERE ").append(meta.activeColumn()).append(" = TRUE ");
        }

        sql.append("ORDER BY ").append(meta.nameColumn());
        return sql.toString();
    }

    private String buildAdminSelectSql(EnumTableMeta meta) {
        String descriptionSql = meta.descriptionColumn() == null
                ? "NULL AS enum_description"
                : meta.descriptionColumn() + " AS enum_description";
        String activeSql = meta.activeColumn() == null
                ? "TRUE AS enum_active"
                : meta.activeColumn() + " AS enum_active";

        return "SELECT "
                + meta.idColumn() + " AS enum_id, "
                + meta.codeColumn() + " AS enum_code, "
                + meta.nameColumn() + " AS enum_name, "
                + descriptionSql + ", "
                + activeSql + " "
                + "FROM " + meta.tableName();
    }

    private String buildReturningClause(EnumTableMeta meta) {
        String descriptionSql = meta.descriptionColumn() == null
                ? "NULL AS enum_description"
                : meta.descriptionColumn() + " AS enum_description";
        String activeSql = meta.activeColumn() == null
                ? "TRUE AS enum_active"
                : meta.activeColumn() + " AS enum_active";
        return "RETURNING "
                + meta.idColumn() + " AS enum_id, "
                + meta.codeColumn() + " AS enum_code, "
                + meta.nameColumn() + " AS enum_name, "
                + descriptionSql + ", "
                + activeSql + " ";
    }

    private Object[] buildWriteArgs(EnumTableMeta meta, String code, String name, String description, Boolean active) {
        java.util.List<Object> args = new java.util.ArrayList<>();
        boolean sameCodeAndNameColumn = meta.codeColumn().equals(meta.nameColumn());
        if (sameCodeAndNameColumn) {
            String mergedValue = (name != null && !name.isBlank()) ? name : code;
            args.add(mergedValue);
        } else {
            args.add(code);
            args.add(name);
        }
        if (meta.descriptionColumn() != null) args.add(description);
        if (meta.activeColumn() != null) args.add(active != null ? active : Boolean.TRUE);
        return args.toArray();
    }

    private void appendSet(StringBuilder sb, String column) {
        if (sb.length() > 0) sb.append(", ");
        sb.append(column).append(" = ?");
    }

    private static final class AppendPair {
        private final StringBuilder cols;
        private final StringBuilder vals;
        private AppendPair(StringBuilder cols, StringBuilder vals) {
            this.cols = cols;
            this.vals = vals;
        }
        void add(String col, String valExpr) {
            if (cols.length() > 0) {
                cols.append(", ");
                vals.append(", ");
            }
            cols.append(col);
            vals.append(valExpr);
        }
    }

    private Map<String, EnumTableMeta> buildSupportedEnums() {
        Map<String, EnumTableMeta> enums = new LinkedHashMap<>();

        // First batch for dropdown APIs. Add remaining enum tables here in next parts.
        register(enums, "company-type", "company_type", "Company Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "project-type", "project_type", "Project Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "project-status", "project_status", "Project Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "employee-category", "employee_category", "Employee Category", "category_id", "category_code", "category_name", "description", "is_active");
        register(enums, "employment-type", "employment_type", "Employment Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "employment-status", "employment_status", "Employment Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "gender", "gender", "Gender", "gender_id", "gender_code", "gender_name", null, "is_active");
        register(enums, "skill-category", "skill_category", "Skill Category", "category_id", "category_code", "category_name", "description", "is_active");
        register(enums, "skill-level", "skill_level", "Skill Level", "level_id", "level_code", "level_name", "description", "is_active");
        register(enums, "training-type", "training_type", "Training Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "training-status", "training_status", "Training Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "complaint-type", "complaint_type", "Complaint Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "complaint-priority", "complaint_priority", "Complaint Priority", "priority_id", "priority_code", "priority_name", "description", "is_active");
        register(enums, "complaint-status", "complaint_status", "Complaint Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "performance-rating", "performance_rating", "Performance Rating", "rating_id", "rating_code", "rating_name", "description", "is_active");
        register(enums, "attendance-status", "attendance_status", "Attendance Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "overtime-type", "overtime_type", "Overtime Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "leave-application-status", "leave_application_status", "Leave Application Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "advance_status", "advance_status", "Advance Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "payroll_status", "payroll_status", "Payroll Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "payroll_payment_method", "payroll_payment_method", "Payroll Payment Method", "method_id", "method_code", "method_name", "description", "is_active");
        register(enums, "payroll_deduction_type", "payroll_deduction_type", "Payroll Deduction Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "voucher_status", "voucher_status", "Voucher Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "ml_job_status", "ml_job_status", "ML Job Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "component_type", "component_type", "Component Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "vehicle-category-type", "vehicle_category_type", "Vehicle Category Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "number_plate_type", "number_plate_type", "Number Plate Type", "plate_type_id", "plate_name", "plate_name", "description", null);
        register(enums, "permit_type", "permit_type", "Permit Type", "permit_type_id", "permit_name", "permit_name", "description", null);
        register(enums, "body_style", "body_style", "Body Style", "style_id", "style_name", "style_name", "description", null);
        register(enums, "insurance_type", "insurance_type", "Insurance Type", "ins_type_id", "type_name", "type_name", "description", null);
        register(enums, "purchase_type", "purchase_type", "Purchase Type", "purchase_type_id", "type_name", "type_name", "description", null);
        register(enums, "seller_type", "seller_type", "Seller Type", "seller_type_id", "type_name", "type_name", "description", null);
        register(enums, "finance_status", "finance_status", "Finance Status", "status_id", "status_name", "status_name", "description", null);
        register(enums, "inspection_result", "inspection_result", "Inspection Result", "result_id", "result_name", "result_name", "description", null);
        register(enums, "vehicle_status", "vehicle_status", "Vehicle Status", "status_id", "status_name", "status_name", "description", null);
        register(enums, "fuel-type", "fuel_type", "Fuel Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "transmission-type", "transmission_type", "Transmission Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "drivetrain_type", "drivetrain_type", "Drivetrain Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "engine_type", "engine_type", "Engine Type", "enginetype_id", "enginetype_name", "enginetype_name", "description", null);
        register(enums, "engine_manufacture", "engine_manufacture", "Engine Manufacture", "enginemanufacture_id", "enginemanufacture_name", "enginemanufacture_name", "description", null);
        register(enums, "ownership_type", "ownership_type", "Ownership Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "usage_type", "usage_type", "Usage Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "undercarriage_type", "undercarriage_type", "Undercarriage Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "operational_status", "operational_status", "Operational Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "assignment_status", "assignment_status", "Assignment Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "assignment_type", "assignment_type", "Assignment Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "consumption_method", "consumption_method", "Consumption Method", "method_id", "method_code", "method_name", "description", "is_active");
        register(enums, "cost_type", "cost_type", "Cost Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "work_type", "work_type", "Work Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "ai_model_type", "ai_model_type", "AI Model Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "prediction_type", "prediction_type", "Prediction Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "risk_level", "risk_level", "Risk Level", "level_id", "level_code", "level_name", "description", "is_active");
        register(enums, "maintenance_strategy_type", "maintenance_strategy_type", "Maintenance Strategy Type", "strategy_id", "strategy_code", "strategy_name", "description", "is_active");
        register(enums, "maintenance_program_type", "maintenance_program_type", "Maintenance Program Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "maintenance_plan_type", "maintenance_plan_type", "Maintenance Plan Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "maintenance_plan_status", "maintenance_plan_status", "Maintenance Plan Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "maintenance_plan_item_status", "maintenance_plan_item_status", "Maintenance Plan Item Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "maintenance_assignment_status", "maintenance_assignment_status", "Maintenance Assignment Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "maintenance_category", "maintenance_category", "Maintenance Category", "category_id", "category_code", "category_name", "description", "is_active");
        register(enums, "maintenance_schedule_status", "maintenance_schedule_status", "Maintenance Schedule Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "maintenance_record_status", "maintenance_record_status", "Maintenance Record Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "inventory_category_type", "inventory_category_type", "Inventory Category Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "inventory_transaction_type", "inventory_transaction_type", "Inventory Transaction Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "inventory_alert_type", "inventory_alert_type", "Inventory Alert Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "alert_level", "alert_level", "Alert Level", "level_id", "level_code", "level_name", "description", "is_active");
        register(enums, "supplier_type", "supplier_type", "Supplier Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "payment_mode", "payment_mode", "Payment Mode", "mode_id", "mode_code", "mode_name", "description", "is_active");
        register(enums, "expense_category", "expense_category", "Expense Category", "category_id", "category_code", "category_name", "description", "is_active");
        register(enums, "quotation_status", "quotation_status", "Quotation Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "approval_status", "approval_status", "Approval Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "purchase_request_status", "purchase_request_status", "Purchase Request Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "purchase_order_status", "purchase_order_status", "Purchase Order Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "goods_receipt_status", "goods_receipt_status", "Goods Receipt Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "material_request_status", "material_request_status", "Material Request Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "customer_type", "customer_type", "Customer Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "customer_status", "customer_status", "Customer Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "rate_type", "rate_type", "Rate Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "work_order_type", "work_order_type", "Work Order Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "work_order_status", "work_order_status", "Work Order Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "fuel_payment_method", "fuel_payment_method", "Fuel Payment Method", "method_id", "method_code", "method_name", "description", "is_active");
        register(enums, "fuel_approval_status", "fuel_approval_status", "Fuel Approval Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "hire_basis", "hire_basis", "Hire Basis", "basis_id", "basis_code", "basis_name", "description", "is_active");
        register(enums, "billing_cycle", "billing_cycle", "Billing Cycle", "cycle_id", "cycle_code", "cycle_name", "description", "is_active");
        register(enums, "fuel_price_source", "fuel_price_source", "Fuel Price Source", "source_id", "source_code", "source_name", "description", "is_active");
        register(enums, "hire_bill_status", "hire_bill_status", "Hire Bill Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "bill_component_type", "bill_component_type", "Bill Component Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "deduction_basis", "deduction_basis", "Deduction Basis", "basis_id", "basis_code", "basis_name", "description", "is_active");
        register(enums, "tyre_status", "tyre_status", "Tyre Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "wheel_position", "wheel_position", "Wheel Position", "position_id", "position_code", "position_name", "description", "is_active");
        register(enums, "wear_pattern", "wear_pattern", "Wear Pattern", "pattern_id", "pattern_code", "pattern_name", "description", "is_active");
        register(enums, "tyre_action", "tyre_action", "Tyre Action", "action_id", "action_code", "action_name", "description", "is_active");
        register(enums, "tyre_repair_type", "tyre_repair_type", "Tyre Repair Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "rotation_scheme", "rotation_scheme", "Rotation Scheme", "scheme_id", "scheme_code", "scheme_name", "description", "is_active");
        register(enums, "removal_reason", "removal_reason", "Removal Reason", "reason_id", "reason_code", "reason_name", "description", "is_active");
        register(enums, "battery_status", "battery_status", "Battery Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "battery_test_result", "battery_test_result", "Battery Test Result", "result_id", "result_code", "result_name", "description", "is_active");
        register(enums, "warranty_claim_status", "warranty_claim_status", "Warranty Claim Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "service_order_status", "service_order_status", "Service Order Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "revenue_type", "revenue_type", "Revenue Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "login_status", "login_status", "Login Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "issue_to_type", "issue_to_type", "Issue To Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "breakdown_type", "breakdown_type", "Breakdown Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "breakdown_severity", "breakdown_severity", "Breakdown Severity", "severity_id", "severity_code", "severity_name", "description", "is_active");
        register(enums, "breakdown_status", "breakdown_status", "Breakdown Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "repair_type", "repair_type", "Repair Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "repair_category", "repair_category", "Repair Category", "category_id", "category_code", "category_name", "description", "is_active");
        register(enums, "repair_location", "repair_location", "Repair Location", "location_id", "location_code", "location_name", "description", "is_active");
        register(enums, "repair_job_status", "repair_job_status", "Repair Job Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "filter_status", "filter_status", "Filter Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "outsourced_repair_status", "outsourced_repair_status", "Outsourced Repair Status", "status_id", "status_code", "status_name", "description", "is_active");
        register(enums, "data_scope_type", "data_scope_type", "Data Scope Type", "scope_id", "scope_code", "scope_name", "description", "is_active");
        register(enums, "education_level", "education_level", "Education Level", "level_id", "level_code", "level_name", "description", "is_active");
        register(enums, "document_type_enum", "document_type_enum", "Document Type", "type_id", "type_code", "type_name", "description", "is_active");
        register(enums, "project_member_role", "project_member_role", "Project Member Role", "role_id", "role_code", "role_name", "description", "is_active");

        return Map.copyOf(enums);
    }

    private void register(
            Map<String, EnumTableMeta> enums,
            String key,
            String tableName,
            String displayName,
            String idColumn,
            String codeColumn,
            String nameColumn,
            String descriptionColumn,
            String activeColumn
    ) {
        enums.put(normalizeEnumKey(key), new EnumTableMeta(
                tableName,
                displayName,
                idColumn,
                codeColumn,
                nameColumn,
                descriptionColumn,
                activeColumn
        ));
    }

    private String normalizeEnumKey(String key) {
        if (key == null) return "";
        String normalized = key.trim()
                .toLowerCase(Locale.ROOT)
                .replace('-', '_');
        if ("training_typ".equals(normalized)) {
            return "training_type";
        }
        return normalized;
    }

    public record EnumTableMeta(
            String tableName,
            String displayName,
            String idColumn,
            String codeColumn,
            String nameColumn,
            String descriptionColumn,
            String activeColumn
    ) {
    }
}






