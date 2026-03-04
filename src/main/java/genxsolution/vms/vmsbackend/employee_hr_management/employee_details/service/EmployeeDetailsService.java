package genxsolution.vms.vmsbackend.employee_hr_management.employee_details.service;

import genxsolution.vms.vmsbackend.employee_hr_management.employee_details.repository.EmployeeDetailsRepository;
import genxsolution.vms.vmsbackend.employee_hr_management.exception.HrResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EmployeeDetailsService {

    private final EmployeeDetailsRepository repository;

    public EmployeeDetailsService(EmployeeDetailsRepository repository) {
        this.repository = repository;
    }

    public List<Map<String, Object>> listEmployeeEducations(UUID companyId, UUID employeeId) {
        if (employeeId == null) {
            return List.of();
        }
        return repository.listEmployeeEducations(companyId, employeeId);
    }

    public Map<String, Object> getEmployeeEducationOverview(UUID companyId, UUID employeeId) {
        Map<String, Object> employee = repository.getEmployeeOverview(companyId, employeeId)
                .orElseThrow(() -> new HrResourceNotFoundException("Employee", employeeId.toString()));
        List<Map<String, Object>> educations = repository.listEmployeeEducationOverviewByEmployee(companyId, employeeId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("employee", employee);
        response.put("educations", educations);
        response.put("totalEducations", educations.size());
        return response;
    }

    public Map<String, Object> getEmployeeEducationById(UUID id) {
        return repository.getEmployeeEducationById(id)
                .orElseThrow(() -> new HrResourceNotFoundException("EmployeeEducation", id.toString()));
    }

    public Map<String, Object> createEmployeeEducation(Map<String, Object> body) {
        return repository.createEmployeeEducation(body);
    }

    public Map<String, Object> updateEmployeeEducation(UUID id, Map<String, Object> body) {
        return repository.updateEmployeeEducation(id, body)
                .orElseThrow(() -> new HrResourceNotFoundException("EmployeeEducation", id.toString()));
    }

    public void deleteEmployeeEducation(UUID id) {
        if (!repository.deleteEmployeeEducation(id)) {
            throw new HrResourceNotFoundException("EmployeeEducation", id.toString());
        }
    }

    public List<Map<String, Object>> listEmployeeSkillAssessments(UUID companyId, UUID employeeId) {
        return repository.listEmployeeSkillAssessments(companyId, employeeId);
    }

    public Map<String, Object> getEmployeeSkillAssessmentById(UUID id) {
        return repository.getEmployeeSkillAssessmentById(id)
                .orElseThrow(() -> new HrResourceNotFoundException("EmployeeSkillAssessment", id.toString()));
    }

    public Map<String, Object> createEmployeeSkillAssessment(Map<String, Object> body) {
        return repository.createEmployeeSkillAssessment(body);
    }

    public Map<String, Object> updateEmployeeSkillAssessment(UUID id, Map<String, Object> body) {
        return repository.updateEmployeeSkillAssessment(id, body)
                .orElseThrow(() -> new HrResourceNotFoundException("EmployeeSkillAssessment", id.toString()));
    }

    public void deleteEmployeeSkillAssessment(UUID id) {
        if (!repository.deleteEmployeeSkillAssessment(id)) {
            throw new HrResourceNotFoundException("EmployeeSkillAssessment", id.toString());
        }
    }

    public List<Map<String, Object>> listEmployeeDocuments(UUID companyId, UUID employeeId) {
        return repository.listEmployeeDocuments(companyId, employeeId);
    }

    public Map<String, Object> getEmployeeDocumentById(UUID id) {
        return repository.getEmployeeDocumentById(id)
                .orElseThrow(() -> new HrResourceNotFoundException("EmployeeDocument", id.toString()));
    }

    public Map<String, Object> createEmployeeDocument(Map<String, Object> body) {
        return repository.createEmployeeDocument(body);
    }

    public Map<String, Object> updateEmployeeDocument(UUID id, Map<String, Object> body) {
        return repository.updateEmployeeDocument(id, body)
                .orElseThrow(() -> new HrResourceNotFoundException("EmployeeDocument", id.toString()));
    }

    public void deleteEmployeeDocument(UUID id) {
        if (!repository.deleteEmployeeDocument(id)) {
            throw new HrResourceNotFoundException("EmployeeDocument", id.toString());
        }
    }

    public List<Map<String, Object>> listProjectMembers(UUID companyId, UUID employeeId, UUID projectId) {
        return repository.listProjectMembers(companyId, employeeId, projectId);
    }

    public Map<String, Object> getProjectMemberById(UUID id) {
        return repository.getProjectMemberById(id)
                .orElseThrow(() -> new HrResourceNotFoundException("ProjectMember", id.toString()));
    }

    public Map<String, Object> createProjectMember(Map<String, Object> body) {
        return repository.createProjectMember(body);
    }

    public Map<String, Object> updateProjectMember(UUID id, Map<String, Object> body) {
        return repository.updateProjectMember(id, body)
                .orElseThrow(() -> new HrResourceNotFoundException("ProjectMember", id.toString()));
    }

    public void deleteProjectMember(UUID id) {
        if (!repository.deleteProjectMember(id)) {
            throw new HrResourceNotFoundException("ProjectMember", id.toString());
        }
    }

    public List<Map<String, Object>> listEmployeeTrainingRecords(UUID companyId, UUID employeeId) {
        return repository.listEmployeeTrainingRecords(companyId, employeeId);
    }

    public Map<String, Object> getEmployeeTrainingRecordById(UUID id) {
        return repository.getEmployeeTrainingRecordById(id)
                .orElseThrow(() -> new HrResourceNotFoundException("EmployeeTrainingRecord", id.toString()));
    }

    public Map<String, Object> createEmployeeTrainingRecord(Map<String, Object> body) {
        return repository.createEmployeeTrainingRecord(body);
    }

    public Map<String, Object> updateEmployeeTrainingRecord(UUID id, Map<String, Object> body) {
        return repository.updateEmployeeTrainingRecord(id, body)
                .orElseThrow(() -> new HrResourceNotFoundException("EmployeeTrainingRecord", id.toString()));
    }

    public void deleteEmployeeTrainingRecord(UUID id) {
        if (!repository.deleteEmployeeTrainingRecord(id)) {
            throw new HrResourceNotFoundException("EmployeeTrainingRecord", id.toString());
        }
    }

    public List<Map<String, Object>> listEmployeeComplaints(UUID companyId, UUID employeeId) {
        return repository.listEmployeeComplaints(companyId, employeeId);
    }

    public Map<String, Object> getEmployeeComplaintById(UUID id) {
        return repository.getEmployeeComplaintById(id)
                .orElseThrow(() -> new HrResourceNotFoundException("EmployeeComplaint", id.toString()));
    }

    public Map<String, Object> createEmployeeComplaint(Map<String, Object> body) {
        return repository.createEmployeeComplaint(body);
    }

    public Map<String, Object> updateEmployeeComplaint(UUID id, Map<String, Object> body) {
        return repository.updateEmployeeComplaint(id, body)
                .orElseThrow(() -> new HrResourceNotFoundException("EmployeeComplaint", id.toString()));
    }

    public void deleteEmployeeComplaint(UUID id) {
        if (!repository.deleteEmployeeComplaint(id)) {
            throw new HrResourceNotFoundException("EmployeeComplaint", id.toString());
        }
    }

    public List<Map<String, Object>> listEmployeePerformanceReviews(UUID companyId, UUID employeeId) {
        return repository.listEmployeePerformanceReviews(companyId, employeeId);
    }

    public Map<String, Object> getEmployeePerformanceReviewById(UUID id) {
        return repository.getEmployeePerformanceReviewById(id)
                .orElseThrow(() -> new HrResourceNotFoundException("EmployeePerformanceReview", id.toString()));
    }

    public Map<String, Object> createEmployeePerformanceReview(Map<String, Object> body) {
        return repository.createEmployeePerformanceReview(body);
    }

    public Map<String, Object> updateEmployeePerformanceReview(UUID id, Map<String, Object> body) {
        return repository.updateEmployeePerformanceReview(id, body)
                .orElseThrow(() -> new HrResourceNotFoundException("EmployeePerformanceReview", id.toString()));
    }

    public void deleteEmployeePerformanceReview(UUID id) {
        if (!repository.deleteEmployeePerformanceReview(id)) {
            throw new HrResourceNotFoundException("EmployeePerformanceReview", id.toString());
        }
    }
}
