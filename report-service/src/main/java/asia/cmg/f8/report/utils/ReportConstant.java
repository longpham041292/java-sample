package asia.cmg.f8.report.utils;

public final class ReportConstant {

    private ReportConstant() {
    }

    public static final int INCREASE = 1;
    public static final int DECREASE = -1;
    public static final int RESET = 0;

    public static final int YESTERDAY = -1;
    public static final int LAST_WEEK = -7;
    public static final int LAST_TWO_WEEKS = -14;
    public static final int LAST_MONTH = -30;
    public static final int LAST_TWO_MONTHS = -60;

    public static final String COUNTER_PREFIX = "session_";
    public static final String COUNTER_SUFFIX = "_counter";

    public static final String ORDER_COMPLETE_COUNTER = "order_complete_counter";
    public static final String TOTAL_SESSION_COUNTER = "order_total_session_counter";

    public static final String AVG_PACKAGE_PURCHAGE = "avg_package_purchase";

}
