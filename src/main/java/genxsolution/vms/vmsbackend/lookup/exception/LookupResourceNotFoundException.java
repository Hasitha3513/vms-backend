package genxsolution.vms.vmsbackend.lookup.exception;

public class LookupResourceNotFoundException extends RuntimeException {
    public LookupResourceNotFoundException(String resource, String id) {
        super(resource + " not found: " + id);
    }
}

