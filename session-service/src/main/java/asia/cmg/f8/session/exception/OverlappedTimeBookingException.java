package asia.cmg.f8.session.exception;

import asia.cmg.f8.session.dto.CreditBookingResponse;

public class OverlappedTimeBookingException extends RuntimeException {

	private final BookingErrorCode error;
	
	private CreditBookingResponse<?> data;

	public OverlappedTimeBookingException(final BookingErrorCode errorCode, final CreditBookingResponse<?> data) {
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
}
