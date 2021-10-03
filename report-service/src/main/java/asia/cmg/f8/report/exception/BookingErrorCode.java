package asia.cmg.f8.report.exception;

import asia.cmg.f8.common.web.errorcode.ErrorCode;

import java.io.Serializable;

/**
 * Created on 12/16/16.
 */
public final class BookingErrorCode implements Serializable {

    private static final long serialVersionUID = -7876978248785470230L;

    public static final BookingErrorCode NOT_VALID_TIME_SLOT = new BookingErrorCode(4005, "TIME_SLOT_INVALID", "One of time slot is invalid.");
    public static final BookingErrorCode BOOK_IN_24_HOURS = new BookingErrorCode(4006, "TIME_SLOT_LESSER_THAN_24_HOURS", "One of time slot is lesser than 24 hours.");
    public static final BookingErrorCode BOOK_IN_PAST = new BookingErrorCode(4007, "TIME_SLOT_IN_THE_PAST", "One of time slot is past time.");
    public static final BookingErrorCode OVERLAP_BOOKING = new BookingErrorCode(4008, "TIME_SLOT_OVERLAPPED", "One of time slot is overlapping with booked or pending sessions.");
    public static final BookingErrorCode BOOK_NOT_IN_PT_AVAILABILITY = new BookingErrorCode(4009, "TIME_SLOT_NOT_AVAILABLE", "One of time slot is not in availabilities of trainer.");
    public static final BookingErrorCode BOOK_OVER_EXPIRED_DATE_ORDER = new BookingErrorCode(40010, "TIME_SLOT_OVER_EXPIRED_DATE", "One of time slot is over expired date of session package");
    public static final BookingErrorCode UNSUPPORTED = new BookingErrorCode(40011, "TIME_SLOT_NOT_ELIGIBLE", "One of time slot is not eligible to book.");
    public static final BookingErrorCode NOT_ENOUGH_AVAILABLE_SESSION = new BookingErrorCode(40012, "AVAILABLE_SESSIONS_NOT_ENOUGH", "Don't have enough available session to book.");
    public static final BookingErrorCode DEACTIVATED_USER = new BookingErrorCode(40013, "USER_NOT_ACTIVATED", "Trainer or User is deactivated user");
    public static final BookingErrorCode SESSION_INVALID = new BookingErrorCode(40012, "SESSION_INVALID", "Booked session is invalid");
    public static final BookingErrorCode TRANSFER_EXCEED_SESSION = new BookingErrorCode(40013,
            "TRANSFER_EXCEED_SESSION", "Number of transferred session is exceed total of sessions");
	public static final BookingErrorCode NOT_ENOUGH_CREDIT = new BookingErrorCode(40014, "NOT_ENOUGH_CREDIT", "Not enough credit");
	public static final BookingErrorCode WALLET_NOT_ACTIVE = new BookingErrorCode(40015, "WALLET_NOT_ACTIVE", "Wallet is not active");
	public static final BookingErrorCode NOT_ENOUGH_SLOT = new BookingErrorCode(40016, "NOT_ENOUGH_SLOT", "Not enough slot");
	public static final BookingErrorCode DISALLOW_CANCEL_BOOKING = new BookingErrorCode(40017, "DISALLOW_CANCEL_BOOKING", "Disallow cancel booking");
	public static final BookingErrorCode NOT_BOOKED_YET = new BookingErrorCode(40019, "NOT_BOOKED_YET", "Not booked yet before");
	public static final BookingErrorCode INVALID_CHECKIN_TIME = new BookingErrorCode(40020, "INVALID_CHECKIN_TIME", "Invalid check-in time");
	public static final BookingErrorCode CMS_NOT_RESPONSE = new BookingErrorCode(40021, "CMS_NOT_RESPONSE", "Could not get response from CMS");

    private final int code;
    private final String error;
    private final String detail;

    public BookingErrorCode(final int code, final String error, final String detail) {
        this.code = code;
        this.error = error;
        this.detail = detail;
    }

    public int getCode() {
        return code;
    }

    public String getError() {
        return error;
    }

    public String getDetail() {
        return detail;
    }

    /**
     * Until method to clone an existing {@link ErrorCode} with customized detail message.
     *
     * @param detail the new detail message.
     * @return new {@link ErrorCode}
     */
    public BookingErrorCode withDetail(final String detail) {
        return new BookingErrorCode(this.code, this.error, detail);
    }
}
