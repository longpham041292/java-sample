package asia.cmg.f8.commerce.constants;

public final class PaymentConstant {

    private PaymentConstant() {
    }

    public static final String ID = "id";
    public static final String VN_GATEWAY_LOCALE = "vn";
    public static final String VN_LOCALE = "vi";
    public static final String PAYMENT_PROVIDER = "ONEPAY";
    public static final String CONFIRM_RESPONSE = "responsecode=%s&desc=confirm-success";
    public static final String NO_VALUE_RETURN = "No Value Returned";
    public static final String HASH_VALID = "CORRECT";
    public static final String HASH_INVALID = "INVALID HASH";
    public static final String TRANSACTION_EXIST = "Y";
    public static final String TRANSACTION_NOT_EXIST = "N";

    public static final String VCP_MERCHANT = "vpc_Merchant";
    public static final String VCP_ACCESS_CODE = "vpc_AccessCode";
    public static final String VCP_MERCHANT_TRANSACTION_REF = "vpc_MerchTxnRef";
    public static final String VCP_ORDER_INFO = "vpc_OrderInfo";
    public static final String VCP_AMOUNT = "vpc_Amount";
    public static final String VCP_RETURN_URL = "vpc_ReturnURL";
    public static final String VCP_VERSION = "vpc_Version";
    public static final String VCP_COMMAND = "vpc_Command";
    public static final String VCP_LOCALE = "vpc_Locale";
    public static final String VCP_CURRENCY = "vpc_Currency";
    public static final String VCP_SECURE_HASH = "vpc_SecureHash";
    public static final String VCP_USER = "vpc_User";
    public static final String VPC_AVS_STREET1 = "vpc_AVS_Street01";
    public static final String VPC_STATE = "vpc_AVS_StateProv";
    public static final String VPC_CITY = "vpc_AVS_City";
    public static final String VPC_COUNTRY_CODE = "vpc_AVS_Country";

    @SuppressWarnings("squid:S2068")
    public static final String VCP_PWD = "vpc_Password";

    public static final String VCP_TICKET_NO = "vpc_TicketNo";
    public static final String TITLE = "Title";
    public static final String AGAIN_LINK = "AgainLink";

    public static final String VCP_TRANSACTION_RESPONSE_CODE = "vpc_TxnResponseCode";
    public static final String VCP_TRANSACTION_NO = "vpc_TransactionNo";
    public static final String VCP_MESSAGE = "vpc_Message";
    public static final String VCP_DREXISTS = "vpc_DRExists";

    public static final String BACKEND_SUCCESS_CODE = "1";
    public static final String BACKEND_FAIL_CODE = "0";
    
    public static final String STATE_APPROVED = "approved";
    
    public static final String CREDIT_TOP_DESC = "Topup credit wallet";
    public static final String CREDIT_CHECKIN_CLUB_DESC = "Subtract credit wallet - Checkin club";
    public static final String CREDIT_EXPIRED = "Subtract credit wallet - Expired credits";
    public static final String CREDIT_BOOKING_SESSION = "Subtract credit wallet - Booking session";
}
