package asia.cmg.f8.session.dto;

public class UserMessageRequest {

	private String languageCode;

	private String message;

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(final String languageCode) {
		this.languageCode = languageCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

}
