package genxsolution.vms.vmsbackend.authentication;

public final class AccessGuard {
    private AccessGuard() {}

    public static void requireAdmin() {
        AuthContext ctx = AuthContextHolder.get();
        if (ctx == null || (!ctx.superAdmin() && !ctx.companyAdmin())) {
            throw new IllegalArgumentException("Admin privileges required");
        }
    }
}






