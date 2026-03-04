package genxsolution.vms.vmsbackend.vehicle_management.exception;
public class VehicleResourceNotFoundException extends RuntimeException {
    public VehicleResourceNotFoundException(String resource, String id) {
        super(resource + " not found: " + id);
    }
}
