package asia.cmg.f8.communication.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ChatRoom {

	private String id;
	private String chatId;
	private String senderId;
	private String recipientId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(String recipientId) {
		this.recipientId = recipientId;
	}

	private ChatRoom(Builder builder) {
		this.id = builder.id;
		this.chatId = builder.chatId;
		this.senderId = builder.senderId;
		this.recipientId = builder.recipientId;
	}

	public static class Builder {
		private String id;
		private String chatId;
		private String senderId;
		private String recipientId;

		public Builder() {
		}

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder chatId(String chatId) {
			this.chatId = chatId;
			return this;
		}

		public Builder senderId(String senderId) {
			this.senderId = senderId;
			return this;
		}

		public Builder recipientId(String recipientId) {
			this.recipientId = recipientId;
			return this;
		}

		public ChatRoom build() {
			return new ChatRoom(this);
		}

	}

}
