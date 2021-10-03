package asia.cmg.f8.profile.database.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.common.spec.user.DocumentStatusType;

/**
 * Created on 11/28/16.
 */
@Entity
@Table(name = "session_users")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BasicUserEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private String uuid;

	@Column(name = "email", nullable = false, length = 255)
	private String email;

	@Column(name = "username", length = 255)
	private String userName;

	@Column(name = "full_name", length = 255)
	private String fullName;

	@Column(name = "avatar", length = 1024)
	private String avatar;

	@Column(name = "city")
	private String city;

	@Column(name = "country")
	private String country;

	@Column(name = "join_date")
	private LocalDateTime joinDate;

	@Column(name = "phone")
	private String phone;

	@Column(name = "doc_status")
	@Enumerated(EnumType.STRING)
	private DocumentStatusType docStatus;

	@Column(name = "enable_subscribe", nullable = false, columnDefinition = "tinyint(1) default 0")
	private Boolean enableSubscribe = false;

	@Column(name = "user_type", length = 5)
	private String userType;

	@Column(name = "extend_user_type", length = 50)
	@JsonProperty("extend_user_type")
	private String extendUserType;

	@Column(name = "activated")
	private Boolean activated;

	@Column(name = "gender", columnDefinition = "int not null default 0")
	@JsonProperty("gender")
	private Integer gender;

	@Column(name = "number_of_rate", columnDefinition = "int not null default 0")
	@JsonProperty("number_of_rate")
	private Integer numberOfRate = 0;

	@Column(name = "average_star", columnDefinition = "double not null default 0")
	@JsonProperty("average_star")
	private Double averageStar = 0d;

	@Column(name = "total_star", columnDefinition = "double not null default 0")
	@JsonProperty("total_star")
	private Double totalStar = 0d;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(name = "modified_date")
	private LocalDateTime modifiedDate;

	@Column(name = "level")
	@JsonProperty("level")
	private String level;

	public Boolean getActivated() {
		return activated;
	}

	public void setActivated(Boolean activated) {
		this.activated = activated;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getExtendUserType() {
		return extendUserType;
	}

	public void setExtendUserType(String extendUserType) {
		this.extendUserType = extendUserType;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getEnableSubscribe() {
		return enableSubscribe;
	}

	public void setEnableSubscribe(Boolean enableSubscribe) {
		this.enableSubscribe = enableSubscribe;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(final String fullName) {
		this.fullName = fullName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(final String phone) {
		this.phone = phone;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public LocalDateTime getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(LocalDateTime joinDate) {
		this.joinDate = joinDate;
	}

	public DocumentStatusType getDocStatus() {
		return docStatus;
	}

	public void setDocStatus(DocumentStatusType docStatus) {
		this.docStatus = docStatus;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public Integer getNumberOfRate() {
		return numberOfRate;
	}

	public void setNumberOfRate(Integer numberOfRate) {
		this.numberOfRate = numberOfRate;
	}

	public Double getAverageStar() {
		return averageStar;
	}

	public void setAverageStar(Double averageStar) {
		this.averageStar = averageStar;
	}

	public Double getTotalStar() {
		return totalStar;
	}

	public void setTotalStar(Double totalStar) {
		this.totalStar = totalStar;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(LocalDateTime modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

}
