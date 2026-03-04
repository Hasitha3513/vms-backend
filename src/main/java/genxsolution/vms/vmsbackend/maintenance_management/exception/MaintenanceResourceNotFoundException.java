package genxsolution.vms.vmsbackend.maintenance_management.exception;
public class MaintenanceResourceNotFoundException extends RuntimeException {
    public MaintenanceResourceNotFoundException(String resource, String id) {
        super(resource + " not found: " + id);
    }
}
