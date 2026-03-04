package genxsolution.vms.vmsbackend.employee_hr_management.employee_details.controller;

import genxsolution.vms.vmsbackend.employee_hr_management.employee_details.service.EmployeeDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hr/employee-details")
public class EmployeeDetailsController {

    private final EmployeeDetailsService service;

    public EmployeeDetailsController(EmployeeDetailsService service) {
        this.service = service;
    }

    @GetMapping("/employee-educations")
    public List<Map<String, Object>> listEmployeeEducations(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String employeeId
    ) {
        return service.listEmployeeEducations(parseUuid(companyId), parseUuid(employeeId));
    }

    @GetMapping("/employee-overview/{employeeId}/educations")
    public Map<String, Object> getEmployeeEducationOverview(
            @PathVariable UUID employeeId,
            @RequestParam(required = false) String companyId
    ) {
        return service.getEmployeeEducationOverview(parseUuid(companyId), employeeId);
    }

    @GetMapping("/employee-educations/{id}")
    public Map<String, Object> getEmployeeEducationById(@PathVariable UUID id) {
        return service.getEmployeeEducationById(id);
    }

    @PostMapping("/employee-educations")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createEmployeeEducation(@RequestBody Map<String, Object> body) {
        return service.createEmployeeEducation(body);
    }

    @PutMapping("/employee-educations/{id}")
    public Map<String, Object> updateEmployeeEducation(@PathVariable UUID id, @RequestBody Map<String, Object> body) {
        return service.updateEmployeeEducation(id, body);
    }

    @DeleteMapping("/employee-educations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployeeEducation(@PathVariable UUID id) {
        service.deleteEmployeeEducation(id);
    }

    @GetMapping("/employee-skill-assessments")
    public List<Map<String, Object>> listEmployeeSkillAssessments(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) UUID employeeId
    ) {
        return service.listEmployeeSkillAssessments(companyId, employeeId);
    }

    @GetMapping("/employee-skill-assessments/{id}")
    public Map<String, Object> getEmployeeSkillAssessmentById(@PathVariable UUID id) {
        return service.getEmployeeSkillAssessmentById(id);
    }

    @PostMapping("/employee-skill-assessments")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createEmployeeSkillAssessment(@RequestBody Map<String, Object> body) {
        return service.createEmployeeSkillAssessment(body);
    }

    @PutMapping("/employee-skill-assessments/{id}")
    public Map<String, Object> updateEmployeeSkillAssessment(@PathVariable UUID id, @RequestBody Map<String, Object> body) {
        return service.updateEmployeeSkillAssessment(id, body);
    }

    @DeleteMapping("/employee-skill-assessments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployeeSkillAssessment(@PathVariable UUID id) {
        service.deleteEmployeeSkillAssessment(id);
    }

    @GetMapping("/employee-documents")
    public List<Map<String, Object>> listEmployeeDocuments(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) UUID employeeId
    ) {
        return service.listEmployeeDocuments(companyId, employeeId);
    }

    @GetMapping("/employee-documents/{id}")
    public Map<String, Object> getEmployeeDocumentById(@PathVariable UUID id) {
        return service.getEmployeeDocumentById(id);
    }

    @PostMapping("/employee-documents")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createEmployeeDocument(@RequestBody Map<String, Object> body) {
        return service.createEmployeeDocument(body);
    }

    @PutMapping("/employee-documents/{id}")
    public Map<String, Object> updateEmployeeDocument(@PathVariable UUID id, @RequestBody Map<String, Object> body) {
        return service.updateEmployeeDocument(id, body);
    }

    @DeleteMapping("/employee-documents/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployeeDocument(@PathVariable UUID id) {
        service.deleteEmployeeDocument(id);
    }

    @GetMapping("/project-members")
    public List<Map<String, Object>> listProjectMembers(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) UUID employeeId,
            @RequestParam(required = false) UUID projectId
    ) {
        return service.listProjectMembers(companyId, employeeId, projectId);
    }

    @GetMapping("/project-members/{id}")
    public Map<String, Object> getProjectMemberById(@PathVariable UUID id) {
        return service.getProjectMemberById(id);
    }

    @PostMapping("/project-members")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createProjectMember(@RequestBody Map<String, Object> body) {
        return service.createProjectMember(body);
    }

    @PutMapping("/project-members/{id}")
    public Map<String, Object> updateProjectMember(@PathVariable UUID id, @RequestBody Map<String, Object> body) {
        return service.updateProjectMember(id, body);
    }

    @DeleteMapping("/project-members/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProjectMember(@PathVariable UUID id) {
        service.deleteProjectMember(id);
    }

    @GetMapping("/employee-training-records")
    public List<Map<String, Object>> listEmployeeTrainingRecords(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) UUID employeeId
    ) {
        return service.listEmployeeTrainingRecords(companyId, employeeId);
    }

    @GetMapping("/employee-training-records/{id}")
    public Map<String, Object> getEmployeeTrainingRecordById(@PathVariable UUID id) {
        return service.getEmployeeTrainingRecordById(id);
    }

    @PostMapping("/employee-training-records")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createEmployeeTrainingRecord(@RequestBody Map<String, Object> body) {
        return service.createEmployeeTrainingRecord(body);
    }

    @PutMapping("/employee-training-records/{id}")
    public Map<String, Object> updateEmployeeTrainingRecord(@PathVariable UUID id, @RequestBody Map<String, Object> body) {
        return service.updateEmployeeTrainingRecord(id, body);
    }

    @DeleteMapping("/employee-training-records/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployeeTrainingRecord(@PathVariable UUID id) {
        service.deleteEmployeeTrainingRecord(id);
    }

    @GetMapping("/employee-complaints")
    public List<Map<String, Object>> listEmployeeComplaints(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) UUID employeeId
    ) {
        return service.listEmployeeComplaints(companyId, employeeId);
    }

    @GetMapping("/employee-complaints/{id}")
    public Map<String, Object> getEmployeeComplaintById(@PathVariable UUID id) {
        return service.getEmployeeComplaintById(id);
    }

    @PostMapping("/employee-complaints")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createEmployeeComplaint(@RequestBody Map<String, Object> body) {
        return service.createEmployeeComplaint(body);
    }

    @PutMapping("/employee-complaints/{id}")
    public Map<String, Object> updateEmployeeComplaint(@PathVariable UUID id, @RequestBody Map<String, Object> body) {
        return service.updateEmployeeComplaint(id, body);
    }

    @DeleteMapping("/employee-complaints/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployeeComplaint(@PathVariable UUID id) {
        service.deleteEmployeeComplaint(id);
    }

    @GetMapping("/employee-performance-reviews")
    public List<Map<String, Object>> listEmployeePerformanceReviews(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) UUID employeeId
    ) {
        return service.listEmployeePerformanceReviews(companyId, employeeId);
    }

    @GetMapping("/employee-performance-reviews/{id}")
    public Map<String, Object> getEmployeePerformanceReviewById(@PathVariable UUID id) {
        return service.getEmployeePerformanceReviewById(id);
    }

    @PostMapping("/employee-performance-reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createEmployeePerformanceReview(@RequestBody Map<String, Object> body) {
        return service.createEmployeePerformanceReview(body);
    }

    @PutMapping("/employee-performance-reviews/{id}")
    public Map<String, Object> updateEmployeePerformanceReview(@PathVariable UUID id, @RequestBody Map<String, Object> body) {
        return service.updateEmployeePerformanceReview(id, body);
    }

    @DeleteMapping("/employee-performance-reviews/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployeePerformanceReview(@PathVariable UUID id) {
        service.deleteEmployeePerformanceReview(id);
    }

    private UUID parseUuid(String value) {
        if (value == null) return null;
        String s = value.trim();
        if (s.isEmpty() || "undefined".equalsIgnoreCase(s) || "null".equalsIgnoreCase(s)) return null;
        try {
            return UUID.fromString(s);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
