package asia.cmg.f8.communication.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ChatNotification {

	private String id;
	private String senderId;
	private String senderName;

	public ChatNotification(String id, String senderId, String senderName) {
		super();
		this.id = id;
		this.senderId = senderId;
		this.senderName = senderName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

}
