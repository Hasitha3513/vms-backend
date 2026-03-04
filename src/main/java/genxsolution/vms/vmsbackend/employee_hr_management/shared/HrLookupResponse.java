package genxsolution.vms.vmsbackend.employee_hr_management.dto.dropdown;

import genxsolution.vms.vmsbackend.lookup.dto.LookupOptionDto;

import java.util.List;

public record HrLookupResponse(
        List<LookupOptionDto> employeeCategories,
        List<LookupOptionDto> genders,
        List<LookupOptionDto> employmentTypes,
        List<LookupOptionDto> employmentStatuses,
        List<LookupOptionDto> skillCategories,
        List<LookupOptionDto> skillLevels,
        List<LookupOptionDto> educationLevels,
        List<LookupOptionDto> documentTypes,
        List<LookupOptionDto> projectMemberRoles,
        List<LookupOptionDto> trainingTypes,
        List<LookupOptionDto> trainingStatuses,
        List<LookupOptionDto> complaintTypes,
        List<LookupOptionDto> complaintPriorities,
        List<LookupOptionDto> complaintStatuses,
        List<LookupOptionDto> performanceRatings,
        List<LookupOptionDto> attendanceStatuses,
        List<LookupOptionDto> overtimeTypes,
        List<LookupOptionDto> leaveApplicationStatuses
) {
}






