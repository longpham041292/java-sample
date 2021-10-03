package asia.cmg.f8.commerce.constants;

public enum TransactionCommand {

    PAY("pay"),
    QUERY("queryDR");
    
    private String code;
    
    TransactionCommand(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
