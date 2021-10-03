package asia.cmg.f8.common.web.errorcode;

import java.io.Serializable;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 11/2/16.
 */
public final class ErrorCode implements Serializable {

	public static final ErrorCode SUCCESS = new ErrorCode(0, "SUCCESS", "Success");
	public static final ErrorCode FAILED = new ErrorCode(1, "FAILED", "Failed");
	
    /**
     * Common error codes.
     */
    public static final ErrorCode REQUEST_INVALID = new ErrorCode(4001, "REQUEST_DATA_IS_INVALID", "Request data is invalid");
    public static final ErrorCode OPERATOR_NOT_FOUND = new ErrorCode(4002, "RESOURCE_IS_NOT_FOUND", "Resource is not found");
    public static final ErrorCode INTERNAL_SERVICE_ERROR = new ErrorCode(5001, "INTERNAL_SERVICE_ERROR", "Internal service error");
    public static final ErrorCode INTERNAL_SERVER_ERROR = new ErrorCode(5002, "INTERNAL_SERVER_ERROR", "Internal server error");
    public static final ErrorCode FORBIDDEN = new ErrorCode(5003, "UNAUTHORIZED", "You are not authorized to do this action");

    /**
     * Entity does not exist error codes
     */
    public static final ErrorCode DOCUMENT_NOT_EXISTING = new ErrorCode(1002, "DOCUMENT_DOES_NOT_EXIST", "Documents do not existing");
    public static final ErrorCode CONVERSATION_NOT_EXIST = new ErrorCode(2002, "CONVERSATION_DOES_NOT_EXIST", "Conversation does not exist");
    public static final ErrorCode USER_NOT_EXIST = new ErrorCode(2003, "USER_DOES_NOT_EXIST", "User does not exist");
    public static final ErrorCode INVALID_POST = new ErrorCode(2004, "INVALID_POST", "The action cannot be done because the post is deleted");
    public static final ErrorCode INVALID_COMMENT = new ErrorCode(2005, "INVALID_COMMENT", "The action cannot be done because the comment is deleted");
    public static final ErrorCode INVALID_STATUS = new ErrorCode(2006, "INVALID_STATUS", "The status is invalid");

    /**
     * Entity is existed error codes
     */
    public static final ErrorCode ENTRY_NOT_EXIST = new ErrorCode(4004, "ENTITY_NOT_EXIST", "Entity not existed");
    public static final ErrorCode ENTRY_EXIST = new ErrorCode(4005, "ENTITY_IS_EXISTED", "Entity is existed");
    public static final ErrorCode ACTIVE_CONTRACT_EXIST = new ErrorCode(4006, "ACTIVE_CONTRACT_IS_EXISTED", "Active contract existed with current PT");
    public static final ErrorCode DB_ERROR_CREATE = new ErrorCode(4007, "DB_ERROR_CREATE", "Create record DB error");
	public static final ErrorCode DB_ERROR_UPDATE = new ErrorCode(4008, "DB_ERROR_UPDATE", "Update record DB error");
	public static final ErrorCode DB_ERROR_DELETE = new ErrorCode(4009, "DB_ERROR_DELETE", "Delete record DB error");
	public static final ErrorCode UG_ERROR_CREATE = new ErrorCode(4010, "UG_ERROR_CREATE", "Create record on usergrid error");
	public static final ErrorCode UG_ERROR_UPDATE = new ErrorCode(4011, "UG_ERROR_UPDATE", "Update record on usergrid error");
	public static final ErrorCode UG_ERROR_DELETE = new ErrorCode(4012, "UG_ERROR_DELETE", "Delete record on usergrid error");

    /**
     * Duplicated error codes
     */
    public static final ErrorCode RATING_DUPLICATE = new ErrorCode(3001, "RATING_IS_DUPLICATED", "Rating is duplicated");
    public static final ErrorCode CONVERSATION_DUPLICATE = new ErrorCode(2001, "CONVERSATION_IS_DUPLICATED", "Conversation is duplicated");

    public static final ErrorCode DOCUMENT_APPROVED_ALREADY = new ErrorCode(1001, "DOCUMENT_HAVE_BEEN_APPROVED", "Documents have been approved already");
    
    public static final ErrorCode INVALID_USER_CODE = new ErrorCode(9004, "REQUEST_DATA_IS_INVALID", "Invalid usercode");
    public static final ErrorCode DUPLICATE_USER_CODE = new ErrorCode(9005, "REQUEST_DATA_IS_INVALID", "Duplicate usercode");

    private static final long serialVersionUID = -3519702048844690464L;

    @JsonProperty("code")
    private final int code;
    @JsonProperty("error")
    @Nullable
    private final String error;
    @JsonProperty("detail")
    @Nullable
    private final String detail;

    public ErrorCode(final int code, final String error, final String detail) {
        this.code = code;
        this.error = error;
        this.detail = detail;
    }

    public ErrorCode(final ErrorCode error) {
        this.code = error.code;
        this.error = error.error;
        this.detail = error.detail;
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
    public ErrorCode withDetail(final String detail) {
        return new ErrorCode(this.code, this.error, detail);
    }

    public ErrorCode withError(final String error, final String detail) {
        return new ErrorCode(this.code, error, detail);
    }
}
