package asia.cmg.f8.session.exception;

public class WalletNotEnoughException extends RuntimeException {

    private final BookingErrorCode error;

    private final Integer amount;
    
    private final Double money;

    public WalletNotEnoughException(final BookingErrorCode errorCode, final Integer amount, final Double money) {
        super(errorCode.getDetail());
        this.error = errorCode;
        this.amount = amount;
        this.money = money;
    }

    public BookingErrorCode errorCode() {
        return error;
    }

    public Integer amount() {
        return amount;
    }
    
    public Double money() {
    	return money;
    }
}
