package asia.cmg.f8.notification.database.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.notification.enumeration.EPhoneType;

@Entity
@Table(name = "device", uniqueConstraints = @UniqueConstraint(columnNames = {"device_id", "user_uuid"}))
public class DeviceEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "uuid", length = 50)
	private String uuid;
	
	@Column(name = "notifier_id", length = 255)
	private String notifierId;
	
	@Column(name = "notifier_name")
	private String notifierName;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "phone_type", length = 50)
	private EPhoneType phoneType;
	
	@Column(name = "device_id", length = 50)
	private String deviceId;
	
	@Column(name = "activated")
	private boolean activated = true;
	
	@Column(name = "user_uuid")
	private String userUuid;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = DeviceGroupEntity.class)
	@JoinColumn(name = "device_group_id")
	private DeviceGroupEntity deviceGroup;
	
	@CreationTimestamp
	@Column(name = "created_date", updatable = false)
	@JsonProperty("created_date")
	private Date createdDate;
	
	@UpdateTimestamp
	@Column(name = "modified_date")
	@JsonIgnore
	private Date modifiedDate;

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

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

	public String getNotifierId() {
		return notifierId;
	}

	public void setNotifierId(String notifierId) {
		this.notifierId = notifierId;
	}

	public String getNotifierName() {
		return notifierName;
	}

	public void setNotifierName(String notifierName) {
		this.notifierName = notifierName;
	}

	public EPhoneType getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(EPhoneType phoneType) {
		this.phoneType = phoneType;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public DeviceGroupEntity getDeviceGroup() {
		return deviceGroup;
	}

	public void setDeviceGroup(DeviceGroupEntity deviceGroup) {
		this.deviceGroup = deviceGroup;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}
}
