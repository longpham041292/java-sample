package asia.cmg.f8.commerce.dto;

import asia.cmg.f8.commerce.constants.TransactionStatus;

public class TransactionStatusInfo {

    private TransactionStatus status;
    private String responseCode;
    private String detail;
    private String userUuid;

    public TransactionStatusInfo(final TransactionStatus status, final String detail) {
        super();
        this.status = status;
        this.detail = detail;
    }

	public TransactionStatusInfo(final TransactionStatus status, final String responseCode, final String detail) {
		super();
		this.status = status;
		this.detail = detail;
		this.responseCode = responseCode;
	}

    public TransactionStatus getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }

    public void setStatus(final TransactionStatus status) {
        this.status = status;
    }

    public void setDetail(final String detail) {
        this.detail = detail;
    }

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}
}
