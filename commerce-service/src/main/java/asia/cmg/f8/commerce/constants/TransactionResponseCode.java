package asia.cmg.f8.commerce.constants;

public enum TransactionResponseCode {

    SUCCESS("0"),
    FAIL("#0"),
    PENDING("300");

    private String code;

    TransactionResponseCode(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
