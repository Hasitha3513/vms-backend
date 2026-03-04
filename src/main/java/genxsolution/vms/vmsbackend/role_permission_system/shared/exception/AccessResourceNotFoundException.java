package genxsolution.vms.vmsbackend.role_permission_system.shared.exception;

public class AccessResourceNotFoundException extends RuntimeException {
    public AccessResourceNotFoundException(String resource, String id) {
        super(resource + " not found: " + id);
    }
}








