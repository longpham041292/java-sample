package asia.cmg.f8.notification.database.entity;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.gson.Gson;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "notifications")
public class NotificationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "uuid", length = 50)
	private String uuid;
	
	@Column(name = "content", length = 1000)
	private String content;
	
	@Column(name = "title", length = 255)
	private String title;
	
	@Column(name = "receiver", length = 50)
	private String receiver;
	
	// For IOS devices
	@Column(name = "aps_payload", length = 5000)
	private String apsPayload;
	
	// For Android devices
	@Column(name = "firebase_payload", length = 5000)
	private String firebasePayload;
	
	@Column(name = "display", nullable = false)
	private boolean display = true;
	
	@Column(name = "sent", nullable = false)
	private boolean sent = false;
	
	@Column(name = "`read`", nullable = false)
	private boolean read = false;
	
	@Column(name = "sent_date")
	private Date sentDate;
	
	@Column(name = "event_name", length = 255)
	private String eventName;
	
	@CreationTimestamp
	@Column(name = "created_date", updatable = false)
	@JsonProperty("created_date")
	private Date createdDate;
	
	@UpdateTimestamp
	@Column(name = "modified_date")
	@JsonIgnore
	private Date modifiedDate;

	@Column(name = "tagged_accounts")
	@JsonProperty("tagged_accounts")
	private String taggedAccounts;

	@Column(name = "custom_data")
	@JsonProperty("custom_data")
	private String customData;

	@Column(name = "avatar")
	@JsonProperty("avatar")
	private String avatar;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getApsPayload() {
		return apsPayload;
	}

	public void setApsPayload(String apsPayload) {
		this.apsPayload = apsPayload;
	}

	public String getFirebasePayload() {
		return firebasePayload;
	}

	public void setFirebasePayload(String firebasePayload) {
		this.firebasePayload = firebasePayload;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public boolean isSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTaggedAccounts() {
		return taggedAccounts;
	}

	public void setTaggedAccounts(String taggedAccounts) {
		this.taggedAccounts = taggedAccounts;
	}

	public String getCustomData() {
		return customData;
	}

	public void setCustomData(String customData) {
		this.customData = customData;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Map<String, String> getCustomDataAsMap() {
		Gson gson = new Gson();
		return gson.fromJson(customData, Map.class);
	}
}
