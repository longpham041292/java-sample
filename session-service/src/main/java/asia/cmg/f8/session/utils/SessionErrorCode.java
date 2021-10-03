package asia.cmg.f8.session.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.common.web.errorcode.ErrorCode;
public enum SessionErrorCode {

	INVALID_SESSION_EVENT_ID(10000, "INVALID_SESSION_EVENT_ID", "Invalid session event id"),
	INVALID_CLUB_ID(10001, "INVALID_CLUB_ID", "Invalid club id"),
	INVALID_SESSION_ID(10002, "INVALID_SESSION_ID", "Invalid session id"),
	ILLEGAL_SESSION_EVENT_STATUS(10003, "ILLEGAL_SESSION_STATUS", "Unsupport for this session event status");
	
	@JsonProperty("code")
    private final int code;
	
    @JsonProperty("error")
    private final String error;
    
    @JsonProperty("detail")
    private final String detail;
    
    SessionErrorCode(final int code, final String error, final String detail) {
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
    
    public ErrorCode toErrorCode() {
    	return new ErrorCode(this.code, this.error, this.detail);
    }
}
