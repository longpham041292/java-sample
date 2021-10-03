package asia.cmg.f8.notification.enumeration;

public enum ENotificationEventName {
	LIKE_POST("post"),
	COMMENT_POST("post"),
	LIKE_COMMENT("post"),
	FOLLOW("profile"),
	CREATE_POST("post"),
	TRANSFER_SESSION("session"),
	CHANGE_SESSION_STATUS("session"),
	ORDER_SUBSCRIPTION("order"),
	ORDER_CREDIT("order"),
	APPROVE_DOC("document"),
	START_SESSION("session"),
	ORDER_COMLETED("order"),
	CHAT("chat"),
	LISA_ADMIN(""),
	PURCHASE_PACKAGE("order"),
	UPGRADE_WALLET("order"),
	EXPIRE_PACKAGE("order"),
	CLASS_STARTING_REMIND("session"),
	CLASS_EXPIRE_REMIND("session"),
	SESSION_PT_CONFIRM("session");

	private final String type;

	ENotificationEventName(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}
}
