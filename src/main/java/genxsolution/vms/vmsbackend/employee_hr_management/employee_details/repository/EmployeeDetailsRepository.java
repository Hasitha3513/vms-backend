package genxsolution.vms.vmsbackend.employee_hr_management.employee_details.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EmployeeDetailsRepository {

    private final JdbcTemplate jdbcTemplate;

    public EmployeeDetailsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> listEmployeeEducations(UUID companyId, UUID employeeId) {
        return listEmployeeEducationOverviewByEmployee(companyId, employeeId);
    }

    public Optional<Map<String, Object>> getEmployeeOverview(UUID companyId, UUID employeeId) {
        String sql = """
                SELECT
                    e.employee_id,
                    e.company_id,
                    e.company_code,
                    e.employee_code,
                    e.first_name,
                    e.last_name,
                    (e.first_name || ' ' || e.last_name) AS full_name,
                    e.work_email,
                    e.mobile_phone,
                    e.job_title,
                    e.hire_date,
                    e.created_at
                FROM employee e
                WHERE e.employee_id = ?
                """;
        List<Object> args = new ArrayList<>();
        args.add(employeeId);
        if (companyId != null) {
            sql += " AND e.company_id = ?";
            args.add(companyId);
        }
        return queryOne(sql, args.toArray());
    }

    public List<Map<String, Object>> listEmployeeEducationOverviewByEmployee(UUID companyId, UUID employeeId) {
        StringBuilder sql = new StringBuilder("""
                SELECT
                    ee.education_id,
                    ee.company_id,
                    ee.company_code,
                    ee.employee_id,
                    e.employee_code,
                    e.first_name,
                    e.last_name,
                    (e.first_name || ' ' || e.last_name) AS full_name,
                    ee.level_id,
                    el.level_code,
                    el.level_name,
                    ee.institution_name,
                    ee.field_of_study,
                    ee.degree_name,
                    ee.start_date,
                    ee.end_date,
                    ee.is_completed,
                    ee.grade_gpa,
                    ee.notes,
                    ee.created_at
                FROM employee_education ee
                INNER JOIN employee e ON e.employee_id = ee.employee_id
                LEFT JOIN education_level el ON el.level_id = ee.level_id
                WHERE ee.employee_id = ?
                """);
        List<Object> args = new ArrayList<>();
        args.add(employeeId);
        if (companyId != null) {
            sql.append(" AND ee.company_id = ?");
            args.add(companyId);
        }
        sql.append(" ORDER BY ee.end_date DESC NULLS LAST, ee.created_at DESC");
        return jdbcTemplate.queryForList(sql.toString(), args.toArray());
    }

    public Optional<Map<String, Object>> getEmployeeEducationById(UUID id) {
        return queryOne("SELECT * FROM employee_education WHERE education_id = ?", id);
    }

    public Map<String, Object> createEmployeeEducation(Map<String, Object> body) {
        String sql = """
                INSERT INTO employee_education (
                    company_id, company_code, employee_id, level_id, institution_name, field_of_study, degree_name,
                    start_date, end_date, is_completed, grade_gpa, notes
                ) VALUES (?, ?, ?, CAST(? AS INTEGER), ?, ?, ?, CAST(? AS DATE), CAST(? AS DATE), COALESCE(CAST(? AS BOOLEAN), FALSE), ?, ?)
                RETURNING *
                """;
        return jdbcTemplate.queryForMap(sql,
                reqUuid(body, "companyId", "Company is required"),
                reqString(body, "companyCode", "Company code is required"),
                reqUuid(body, "employeeId", "Employee is required"),
                asInteger(body.get("levelId")),
                reqString(body, "institutionName", "Institution name is required"),
                asString(body.get("fieldOfStudy")),
                asString(body.get("degreeName")),
                asString(body.get("startDate")),
                asString(body.get("endDate")),
                asBoolean(body.get("isCompleted")),
                asString(body.get("gradeGpa")),
                asString(body.get("notes"))
        );
    }

    public Optional<Map<String, Object>> updateEmployeeEducation(UUID id, Map<String, Object> body) {
        String sql = """
                UPDATE employee_education SET
                    level_id = COALESCE(CAST(? AS INTEGER), level_id),
                    institution_name = COALESCE(?, institution_name),
                    field_of_study = COALESCE(?, field_of_study),
                    degree_name = COALESCE(?, degree_name),
                    start_date = COALESCE(CAST(? AS DATE), start_date),
                    end_date = COALESCE(CAST(? AS DATE), end_date),
                    is_completed = COALESCE(CAST(? AS BOOLEAN), is_completed),
                    grade_gpa = COALESCE(?, grade_gpa),
                    notes = COALESCE(?, notes)
                WHERE education_id = ?
                RETURNING *
                """;
        return queryOne(sql,
                asInteger(body.get("levelId")),
                asString(body.get("institutionName")),
                asString(body.get("fieldOfStudy")),
                asString(body.get("degreeName")),
                asString(body.get("startDate")),
                asString(body.get("endDate")),
                asBoolean(body.get("isCompleted")),
                asString(body.get("gradeGpa")),
                asString(body.get("notes")),
                id
        );
    }

    public boolean deleteEmployeeEducation(UUID id) {
        return jdbcTemplate.update("DELETE FROM employee_education WHERE education_id = ?", id) > 0;
    }

    public List<Map<String, Object>> listEmployeeSkillAssessments(UUID companyId, UUID employeeId) {
        StringBuilder sql = new StringBuilder("""
                SELECT
                    esa.assessment_id,
                    esa.company_id,
                    esa.company_code,
                    esa.employee_id,
                    esa.skill_id,
                    es.skill_name,
                    esa.assessment_date,
                    esa.skill_level_id,
                    sl.level_name AS skill_level_name,
                    esa.proficiency
                FROM employee_skill_assessment esa
                LEFT JOIN employee_skill es ON es.skill_id = esa.skill_id
                LEFT JOIN skill_level sl ON sl.level_id = esa.skill_level_id
                WHERE 1=1
                """);
        List<Object> args = new ArrayList<>();
        if (companyId != null) {
            sql.append(" AND esa.company_id = ?");
            args.add(companyId);
        }
        if (employeeId != null) {
            sql.append(" AND esa.employee_id = ?");
            args.add(employeeId);
        }
        sql.append(" ORDER BY esa.assessment_date DESC, esa.created_at DESC");
        return jdbcTemplate.queryForList(sql.toString(), args.toArray());
    }

    public Optional<Map<String, Object>> getEmployeeSkillAssessmentById(UUID id) {
        return queryOne("SELECT * FROM employee_skill_assessment WHERE assessment_id = ?", id);
    }

    public Map<String, Object> createEmployeeSkillAssessment(Map<String, Object> body) {
        String sql = """
                INSERT INTO employee_skill_assessment (
                    company_id, company_code, employee_id, skill_id, assessment_date, skill_level_id, proficiency, assessed_by, notes
                ) VALUES (?, ?, ?, ?, CAST(? AS DATE), CAST(? AS INTEGER), CAST(? AS INTEGER), ?, ?)
                RETURNING *
                """;
        return jdbcTemplate.queryForMap(sql,
                reqUuid(body, "companyId", "Company is required"),
                reqString(body, "companyCode", "Company code is required"),
                reqUuid(body, "employeeId", "Employee is required"),
                reqUuid(body, "skillId", "Skill is required"),
                reqString(body, "assessmentDate", "Assessment date is required"),
                asInteger(body.get("skillLevelId")),
                asInteger(body.get("proficiency")),
                asUuid(body.get("assessedBy")),
                asString(body.get("notes"))
        );
    }

    public Optional<Map<String, Object>> updateEmployeeSkillAssessment(UUID id, Map<String, Object> body) {
        String sql = """
                UPDATE employee_skill_assessment SET
                    skill_id = COALESCE(?, skill_id),
                    assessment_date = COALESCE(CAST(? AS DATE), assessment_date),
                    skill_level_id = COALESCE(CAST(? AS INTEGER), skill_level_id),
                    proficiency = COALESCE(CAST(? AS INTEGER), proficiency),
                    assessed_by = COALESCE(?, assessed_by),
                    notes = COALESCE(?, notes)
                WHERE assessment_id = ?
                RETURNING *
                """;
        return queryOne(sql,
                asUuid(body.get("skillId")),
                asString(body.get("assessmentDate")),
                asInteger(body.get("skillLevelId")),
                asInteger(body.get("proficiency")),
                asUuid(body.get("assessedBy")),
                asString(body.get("notes")),
                id
        );
    }

    public boolean deleteEmployeeSkillAssessment(UUID id) {
        return jdbcTemplate.update("DELETE FROM employee_skill_assessment WHERE assessment_id = ?", id) > 0;
    }

    public List<Map<String, Object>> listEmployeeDocuments(UUID companyId, UUID employeeId) {
        return listByCompanyAndEmployee("employee_document", "uploaded_at DESC", companyId, employeeId);
    }

    public Optional<Map<String, Object>> getEmployeeDocumentById(UUID id) {
        return queryOne("SELECT * FROM employee_document WHERE document_id = ?", id);
    }

    public Map<String, Object> createEmployeeDocument(Map<String, Object> body) {
        String sql = """
                INSERT INTO employee_document (
                    company_id, company_code, employee_id, document_type, document_name, document_number, file_path,
                    file_size_bytes, mime_type, issue_date, expiry_date, is_verified, verified_by, notes
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, COALESCE(?, FALSE), ?, ?)
                RETURNING *
                """;
        return jdbcTemplate.queryForMap(sql,
                reqUuid(body, "companyId", "Company is required"),
                reqString(body, "companyCode", "Company code is required"),
                reqUuid(body, "employeeId", "Employee is required"),
                reqString(body, "documentType", "Document type is required"),
                reqString(body, "documentName", "Document name is required"),
                asString(body.get("documentNumber")),
                reqString(body, "filePath", "File path is required"),
                asLong(body.get("fileSizeBytes")),
                asString(body.get("mimeType")),
                asString(body.get("issueDate")),
                asString(body.get("expiryDate")),
                asBoolean(body.get("isVerified")),
                asUuid(body.get("verifiedBy")),
                asString(body.get("notes"))
        );
    }

    public Optional<Map<String, Object>> updateEmployeeDocument(UUID id, Map<String, Object> body) {
        String sql = """
                UPDATE employee_document SET
                    document_type = COALESCE(?, document_type),
                    document_name = COALESCE(?, document_name),
                    document_number = COALESCE(?, document_number),
                    file_path = COALESCE(?, file_path),
                    file_size_bytes = COALESCE(?, file_size_bytes),
                    mime_type = COALESCE(?, mime_type),
                    issue_date = COALESCE(?, issue_date),
                    expiry_date = COALESCE(?, expiry_date),
                    is_verified = COALESCE(?, is_verified),
                    verified_by = COALESCE(?, verified_by),
                    notes = COALESCE(?, notes)
                WHERE document_id = ?
                RETURNING *
                """;
        return queryOne(sql,
                asString(body.get("documentType")),
                asString(body.get("documentName")),
                asString(body.get("documentNumber")),
                asString(body.get("filePath")),
                asLong(body.get("fileSizeBytes")),
                asString(body.get("mimeType")),
                asString(body.get("issueDate")),
                asString(body.get("expiryDate")),
                asBoolean(body.get("isVerified")),
                asUuid(body.get("verifiedBy")),
                asString(body.get("notes")),
                id
        );
    }

    public boolean deleteEmployeeDocument(UUID id) {
        return jdbcTemplate.update("DELETE FROM employee_document WHERE document_id = ?", id) > 0;
    }

    public List<Map<String, Object>> listProjectMembers(UUID companyId, UUID employeeId, UUID projectId) {
        StringBuilder sql = new StringBuilder("""
                SELECT * FROM project_member
                WHERE 1=1
                """);
        List<Object> args = new ArrayList<>();
        if (companyId != null) {
            sql.append(" AND company_id = ?");
            args.add(companyId);
        }
        if (employeeId != null) {
            sql.append(" AND employee_id = ?");
            args.add(employeeId);
        }
        if (projectId != null) {
            sql.append(" AND project_id = ?");
            args.add(projectId);
        }
        sql.append(" ORDER BY created_at DESC");
        return jdbcTemplate.queryForList(sql.toString(), args.toArray());
    }

    public Optional<Map<String, Object>> getProjectMemberById(UUID id) {
        return queryOne("SELECT * FROM project_member WHERE member_id = ?", id);
    }

    public Map<String, Object> createProjectMember(Map<String, Object> body) {
        String sql = """
                INSERT INTO project_member (
                    company_id, company_code, project_id, employee_id, role_id, joined_date, left_date, is_active
                ) VALUES (?, ?, ?, ?, ?, COALESCE(?, CURRENT_DATE), ?, COALESCE(?, TRUE))
                RETURNING *
                """;
        return jdbcTemplate.queryForMap(sql,
                reqUuid(body, "companyId", "Company is required"),
                reqString(body, "companyCode", "Company code is required"),
                reqUuid(body, "projectId", "Project is required"),
                reqUuid(body, "employeeId", "Employee is required"),
                asInteger(body.get("roleId")),
                asString(body.get("joinedDate")),
                asString(body.get("leftDate")),
                asBoolean(body.get("isActive"))
        );
    }

    public Optional<Map<String, Object>> updateProjectMember(UUID id, Map<String, Object> body) {
        String sql = """
                UPDATE project_member SET
                    role_id = COALESCE(?, role_id),
                    joined_date = COALESCE(?, joined_date),
                    left_date = COALESCE(?, left_date),
                    is_active = COALESCE(?, is_active)
                WHERE member_id = ?
                RETURNING *
                """;
        return queryOne(sql,
                asInteger(body.get("roleId")),
                asString(body.get("joinedDate")),
                asString(body.get("leftDate")),
                asBoolean(body.get("isActive")),
                id
        );
    }

    public boolean deleteProjectMember(UUID id) {
        return jdbcTemplate.update("DELETE FROM project_member WHERE member_id = ?", id) > 0;
    }

    public List<Map<String, Object>> listEmployeeTrainingRecords(UUID companyId, UUID employeeId) {
        return listByCompanyAndEmployee("employee_training_record", "training_date DESC, created_at DESC", companyId, employeeId);
    }

    public Optional<Map<String, Object>> getEmployeeTrainingRecordById(UUID id) {
        return queryOne("SELECT * FROM employee_training_record WHERE training_record_id = ?", id);
    }

    public Map<String, Object> createEmployeeTrainingRecord(Map<String, Object> body) {
        String sql = """
                INSERT INTO employee_training_record (
                    company_id, company_code, employee_id, training_id, training_date, completion_date,
                    status_id, score, certificate_number, notes
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING *
                """;
        return jdbcTemplate.queryForMap(sql,
                reqUuid(body, "companyId", "Company is required"),
                reqString(body, "companyCode", "Company code is required"),
                reqUuid(body, "employeeId", "Employee is required"),
                reqUuid(body, "trainingId", "Training is required"),
                reqString(body, "trainingDate", "Training date is required"),
                asString(body.get("completionDate")),
                asInteger(body.get("statusId")),
                asBigDecimal(body.get("score")),
                asString(body.get("certificateNumber")),
                asString(body.get("notes"))
        );
    }

    public Optional<Map<String, Object>> updateEmployeeTrainingRecord(UUID id, Map<String, Object> body) {
        String sql = """
                UPDATE employee_training_record SET
                    training_id = COALESCE(?, training_id),
                    training_date = COALESCE(?, training_date),
                    completion_date = COALESCE(?, completion_date),
                    status_id = COALESCE(?, status_id),
                    score = COALESCE(?, score),
                    certificate_number = COALESCE(?, certificate_number),
                    notes = COALESCE(?, notes)
                WHERE training_record_id = ?
                RETURNING *
                """;
        return queryOne(sql,
                asUuid(body.get("trainingId")),
                asString(body.get("trainingDate")),
                asString(body.get("completionDate")),
                asInteger(body.get("statusId")),
                asBigDecimal(body.get("score")),
                asString(body.get("certificateNumber")),
                asString(body.get("notes")),
                id
        );
    }

    public boolean deleteEmployeeTrainingRecord(UUID id) {
        return jdbcTemplate.update("DELETE FROM employee_training_record WHERE training_record_id = ?", id) > 0;
    }

    public List<Map<String, Object>> listEmployeeComplaints(UUID companyId, UUID employeeId) {
        return listByCompanyAndEmployee("employee_complaint", "complaint_date DESC, created_at DESC", companyId, employeeId);
    }

    public Optional<Map<String, Object>> getEmployeeComplaintById(UUID id) {
        return queryOne("SELECT * FROM employee_complaint WHERE complaint_id = ?", id);
    }

    public Map<String, Object> createEmployeeComplaint(Map<String, Object> body) {
        String sql = """
                INSERT INTO employee_complaint (
                    company_id, company_code, employee_id, complaint_date, complaint_type_id, subject, description,
                    priority_id, status_id, assigned_to, resolution, resolved_date
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING *
                """;
        return jdbcTemplate.queryForMap(sql,
                reqUuid(body, "companyId", "Company is required"),
                reqString(body, "companyCode", "Company code is required"),
                reqUuid(body, "employeeId", "Employee is required"),
                reqString(body, "complaintDate", "Complaint date is required"),
                asInteger(body.get("complaintTypeId")),
                reqString(body, "subject", "Subject is required"),
                reqString(body, "description", "Description is required"),
                asInteger(body.get("priorityId")),
                asInteger(body.get("statusId")),
                asUuid(body.get("assignedTo")),
                asString(body.get("resolution")),
                asString(body.get("resolvedDate"))
        );
    }

    public Optional<Map<String, Object>> updateEmployeeComplaint(UUID id, Map<String, Object> body) {
        String sql = """
                UPDATE employee_complaint SET
                    complaint_date = COALESCE(?, complaint_date),
                    complaint_type_id = COALESCE(?, complaint_type_id),
                    subject = COALESCE(?, subject),
                    description = COALESCE(?, description),
                    priority_id = COALESCE(?, priority_id),
                    status_id = COALESCE(?, status_id),
                    assigned_to = COALESCE(?, assigned_to),
                    resolution = COALESCE(?, resolution),
                    resolved_date = COALESCE(?, resolved_date)
                WHERE complaint_id = ?
                RETURNING *
                """;
        return queryOne(sql,
                asString(body.get("complaintDate")),
                asInteger(body.get("complaintTypeId")),
                asString(body.get("subject")),
                asString(body.get("description")),
                asInteger(body.get("priorityId")),
                asInteger(body.get("statusId")),
                asUuid(body.get("assignedTo")),
                asString(body.get("resolution")),
                asString(body.get("resolvedDate")),
                id
        );
    }

    public boolean deleteEmployeeComplaint(UUID id) {
        return jdbcTemplate.update("DELETE FROM employee_complaint WHERE complaint_id = ?", id) > 0;
    }

    public List<Map<String, Object>> listEmployeePerformanceReviews(UUID companyId, UUID employeeId) {
        return listByCompanyAndEmployee("employee_performance_review", "review_date DESC, created_at DESC", companyId, employeeId);
    }

    public Optional<Map<String, Object>> getEmployeePerformanceReviewById(UUID id) {
        return queryOne("SELECT * FROM employee_performance_review WHERE review_id = ?", id);
    }

    public Map<String, Object> createEmployeePerformanceReview(Map<String, Object> body) {
        String sql = """
                INSERT INTO employee_performance_review (
                    company_id, company_code, employee_id, review_date, reviewer_id, performance_score,
                    attendance_score, productivity_score, safety_score, overall_rating_id, strengths,
                    areas_for_improvement, goals, next_review_date
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING *
                """;
        return jdbcTemplate.queryForMap(sql,
                reqUuid(body, "companyId", "Company is required"),
                reqString(body, "companyCode", "Company code is required"),
                reqUuid(body, "employeeId", "Employee is required"),
                reqString(body, "reviewDate", "Review date is required"),
                reqUuid(body, "reviewerId", "Reviewer is required"),
                asBigDecimal(body.get("performanceScore")),
                asBigDecimal(body.get("attendanceScore")),
                asBigDecimal(body.get("productivityScore")),
                asBigDecimal(body.get("safetyScore")),
                asInteger(body.get("overallRatingId")),
                asString(body.get("strengths")),
                asString(body.get("areasForImprovement")),
                asString(body.get("goals")),
                asString(body.get("nextReviewDate"))
        );
    }

    public Optional<Map<String, Object>> updateEmployeePerformanceReview(UUID id, Map<String, Object> body) {
        String sql = """
                UPDATE employee_performance_review SET
                    review_date = COALESCE(?, review_date),
                    reviewer_id = COALESCE(?, reviewer_id),
                    performance_score = COALESCE(?, performance_score),
                    attendance_score = COALESCE(?, attendance_score),
                    productivity_score = COALESCE(?, productivity_score),
                    safety_score = COALESCE(?, safety_score),
                    overall_rating_id = COALESCE(?, overall_rating_id),
                    strengths = COALESCE(?, strengths),
                    areas_for_improvement = COALESCE(?, areas_for_improvement),
                    goals = COALESCE(?, goals),
                    next_review_date = COALESCE(?, next_review_date)
                WHERE review_id = ?
                RETURNING *
                """;
        return queryOne(sql,
                asString(body.get("reviewDate")),
                asUuid(body.get("reviewerId")),
                asBigDecimal(body.get("performanceScore")),
                asBigDecimal(body.get("attendanceScore")),
                asBigDecimal(body.get("productivityScore")),
                asBigDecimal(body.get("safetyScore")),
                asInteger(body.get("overallRatingId")),
                asString(body.get("strengths")),
                asString(body.get("areasForImprovement")),
                asString(body.get("goals")),
                asString(body.get("nextReviewDate")),
                id
        );
    }

    public boolean deleteEmployeePerformanceReview(UUID id) {
        return jdbcTemplate.update("DELETE FROM employee_performance_review WHERE review_id = ?", id) > 0;
    }

    private List<Map<String, Object>> listByCompanyAndEmployee(String table, String orderBy, UUID companyId, UUID employeeId) {
        StringBuilder sql = new StringBuilder("SELECT * FROM " + table + " WHERE 1=1");
        List<Object> args = new ArrayList<>();
        if (companyId != null) {
            sql.append(" AND company_id = ?");
            args.add(companyId);
        }
        if (employeeId != null) {
            sql.append(" AND employee_id = ?");
            args.add(employeeId);
        }
        sql.append(" ORDER BY ").append(orderBy);
        return jdbcTemplate.queryForList(sql.toString(), args.toArray());
    }

    private Optional<Map<String, Object>> queryOne(String sql, Object... args) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, args);
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    private UUID reqUuid(Map<String, Object> body, String key, String message) {
        UUID value = asUuid(body.get(key));
        if (value == null) throw new IllegalArgumentException(message);
        return value;
    }

    private String reqString(Map<String, Object> body, String key, String message) {
        String value = asString(body.get(key));
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value;
    }

    private UUID asUuid(Object value) {
        if (value == null) return null;
        if (value instanceof UUID uuid) return uuid;
        String text = value.toString().trim();
        if (text.isEmpty()) return null;
        if ("undefined".equalsIgnoreCase(text) || "null".equalsIgnoreCase(text)) return null;
        return UUID.fromString(text);
    }

    private Integer asInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Integer i) return i;
        if (value instanceof Number n) return n.intValue();
        String text = value.toString().trim();
        if (text.isEmpty()) return null;
        return Integer.parseInt(text);
    }

    private Long asLong(Object value) {
        if (value == null) return null;
        if (value instanceof Long l) return l;
        if (value instanceof Number n) return n.longValue();
        String text = value.toString().trim();
        if (text.isEmpty()) return null;
        return Long.parseLong(text);
    }

    private BigDecimal asBigDecimal(Object value) {
        if (value == null) return null;
        if (value instanceof BigDecimal b) return b;
        if (value instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        String text = value.toString().trim();
        if (text.isEmpty()) return null;
        return new BigDecimal(text);
    }

    private Boolean asBoolean(Object value) {
        if (value == null) return null;
        if (value instanceof Boolean b) return b;
        String text = value.toString().trim();
        if (text.isEmpty()) return null;
        return "true".equalsIgnoreCase(text) || "1".equals(text);
    }

    private String asString(Object value) {
        if (value == null) return null;
        String text = value.toString().trim();
        return text.isEmpty() ? null : text;
    }
}
