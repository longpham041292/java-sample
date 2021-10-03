package asia.cmg.f8.session.exception;

import asia.cmg.f8.session.dto.CreditBookingResponse;

/**
 * Created on 1/17/17.
 */
public class BookingValidationException extends RuntimeException {

    private final BookingErrorCode error;
    
    private CreditBookingResponse<?> data;

    public BookingValidationException(final BookingErrorCode errorCode) {
        super(errorCode.getDetail());
        this.error = errorCode;
    }
    
    public BookingValidationException(final BookingErrorCode errorCode, final CreditBookingResponse<?> data) {
        super(errorCode.getDetail());
        this.error = errorCode;
        this.data = data;
    }

    public BookingErrorCode errorCode() {
        return error;
    }

	public CreditBookingResponse<?> getData() {
		return data;
	}

	public void setData(CreditBookingResponse<?> data) {
		this.data = data;
	}
    
}
