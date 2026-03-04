package genxsolution.vms.vmsbackend.lookup.exception;

import java.util.List;

public class EnumTypeNotSupportedException extends RuntimeException {

    private final List<String> supportedEnumKeys;

    public EnumTypeNotSupportedException(String enumKey, List<String> supportedEnumKeys) {
        super("Enum key not supported: " + enumKey);
        this.supportedEnumKeys = supportedEnumKeys;
    }

    public List<String> getSupportedEnumKeys() {
        return supportedEnumKeys;
    }
}






