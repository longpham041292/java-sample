package asia.cmg.f8.common.security.util;

/**
 * Created on 3/2/17.
 */
public final class SecurityConstants {

    public static final String ADMIN_ROLE = "admin";
    public static final String ADMIN_ROLE_PRE_AUTHORIZE_EXPRESSION = "hasRole('admin')";
    public static final String PT_ROLE_PRE_AUTHORIZE_EXPRESSION = "hasRole('trainer')";
    public static final String CMS_ADMIN_ROLE_PRE_AUTHORIZE_EXPRESSION = "hasRole('cms')";
    public static final String FINANCE_ROLE_PRE_AUTHORIZE_EXPRESSION = "hasRole('finance')";
    public static final String SALES_ROLE_PRE_AUTHORIZE_EXPRESSION = "hasRole('sales')";
    public static final String SALESADMIN_ROLE_PRE_AUTHORIZE_EXPRESSION = "hasRole('sales-admin')";

    private SecurityConstants() {
        // empty
    }
}
