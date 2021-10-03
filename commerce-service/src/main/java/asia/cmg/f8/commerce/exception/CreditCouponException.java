package asia.cmg.f8.commerce.exception;

import asia.cmg.f8.common.web.errorcode.ErrorCode;

public class CreditCouponException extends RuntimeException {

	private final ErrorCode errorCode;

	public CreditCouponException(final ErrorCode errorCode) {
		super(errorCode.getDetail());
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

}
