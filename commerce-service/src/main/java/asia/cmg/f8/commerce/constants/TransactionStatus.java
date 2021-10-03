package asia.cmg.f8.commerce.constants;

public enum TransactionStatus {
    SUCCESS("success", "Transaction success"),
    FAIL("fail", "Transaction fail"),
    IGNORE("ignore", "Transaction ignore"),
    INVALID_HASH("invalidhash", "Invalid hash");
    
    private String code;
    private String name;
    
    TransactionStatus(final String code, final String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
    
    
}
