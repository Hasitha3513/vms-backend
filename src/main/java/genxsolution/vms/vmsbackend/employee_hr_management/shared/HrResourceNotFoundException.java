package genxsolution.vms.vmsbackend.employee_hr_management.exception;

public class HrResourceNotFoundException extends RuntimeException {
    public HrResourceNotFoundException(String resource, String id) {
        super(resource + " not found: " + id);
    }
}






