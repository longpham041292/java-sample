package asia.cmg.f8.communication.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class ChatMessage {

	@Id
	private String id;
	private String chatId;
	private String senderId;
	private String recipientId;
	private String senderName;
	private String recipientName;
	private String content;
	private LocalDateTime timestamp;
	private MessageStatus status;

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

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getRecipientName() {
		return recipientName;
	}

	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public MessageStatus getStatus() {
		return status;
	}

	public void setStatus(MessageStatus status) {
		this.status = status;
	}

	private ChatMessage(Builder builder) {
		this.id = builder.id;
		this.chatId = builder.chatId;
		this.senderId = builder.senderId;
		this.recipientId = builder.recipientId;
		this.senderName = builder.senderName;
		this.recipientName = builder.recipientName;
		this.content = builder.content;
		this.timestamp = builder.timestamp;
		this.status = builder.status;
	}

	public static class Builder {
		private String id;
		private String chatId;
		private String senderId;
		private String recipientId;
		private String senderName;
		private String recipientName;
		private String content;
		private LocalDateTime timestamp;
		private MessageStatus status;

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

		public Builder senderName(String senderName) {
			this.senderName = senderName;
			return this;
		}

		public Builder recipientName(String recipientName) {
			this.recipientName = recipientName;
			return this;
		}

		public Builder content(String content) {
			this.content = content;
			return this;
		}

		public Builder timestamp(LocalDateTime timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public Builder timestamp(MessageStatus status) {
			this.status = status;
			return this;
		}

		public ChatMessage build() {
			return new ChatMessage(this);
		}
	}
}
