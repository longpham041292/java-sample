package asia.cmg.f8.session.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
public class BasicUserEntity extends AbstractEntity {

	@Column(name = "email", nullable = false, length = 255)
	private String email;

	@Column(name = "full_name", length = 255)
	private String fullName;

	@Column(name = "avatar", length = 1024)
	private String avatar;

	@Column(name = "username", length = 255)
	private String userName;

	@Column(name = "city")
	private String city;

	@Column(name = "country")
	private String country;

	@Column(name = "activated")
	private Boolean activated;

	@Column(name = "phone")
	private String phone;

	@Column(name = "user_type", length = 5)
	private String userType;

	@Column(name = "join_date")
	private LocalDateTime joinDate;

	@Column(name = "doc_status")
	@Enumerated(EnumType.STRING)
	private DocumentStatusType docStatus;

	@Column(name = "doc_approved_date")
	private LocalDateTime docApprovedDate;

	@Column(name = "level")
	private String level;

	@Column(name = "user_code")
	private String usercode;

	@Column(name = "club_code")
	private String clubcode;

	@Column(name = "email_validated")
	private Boolean emailValidated;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user", targetEntity = UserSkillsEntity.class)
	private List<UserSkillsEntity> skills = new ArrayList<UserSkillsEntity>();

	@Column(name = "enable_subscribe", nullable = false, columnDefinition = "tinyint(1) default 0")
	private Boolean enableSubscribe = false;

	@Column(name = "extend_user_type", length = 50)
	@JsonProperty("extend_user_type")
	private String extendUserType;

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

	@Column(name = "verify_phone", columnDefinition = "tinyint(1) not null default 0")
	@JsonProperty("verify_phone")
	private Boolean verifyPhone = false;

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

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public String getExtendUserType() {
		return extendUserType;
	}

	public void setExtendUserType(String extendUserType) {
		this.extendUserType = extendUserType;
	}

	public List<UserSkillsEntity> getSkills() {
		return skills;
	}

	public void setSkills(List<UserSkillsEntity> skills) {
		this.skills = skills;
		skills.forEach(skill -> {
			skill.setUser(this);
			skill.setUserUuid(this.getUuid());
		});
	}

	public Boolean getEnableSubscribe() {
		return enableSubscribe;
	}

	public void setEnableSubscribe(Boolean enableSubscribe) {
		this.enableSubscribe = enableSubscribe;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(final String fullName) {
		this.fullName = fullName;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(final String avatar) {
		this.avatar = avatar;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(final String userName) {
		this.userName = userName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(final String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(final String country) {
		this.country = country;
	}

	public Boolean isActivated() {
		return activated;
	}

	public void setActivated(final Boolean activated) {
		this.activated = activated;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(final String phone) {
		this.phone = phone;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(final String userType) {
		this.userType = userType;
	}

	public LocalDateTime getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(final LocalDateTime joinDate) {
		this.joinDate = joinDate;
	}

	public DocumentStatusType getDocStatus() {
		return docStatus;
	}

	public void setDocStatus(final DocumentStatusType docStatus) {
		this.docStatus = docStatus;
	}

	public LocalDateTime getDocApprovedDate() {
		return docApprovedDate;
	}

	public void setDocApprovedDate(final LocalDateTime docApprovedDate) {
		this.docApprovedDate = docApprovedDate;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(final String level) {
		this.level = level;
	}

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(final String usercode) {
		this.usercode = usercode;
	}

	public String getClubcode() {
		return clubcode;
	}

	public void setClubcode(final String clubcode) {
		this.clubcode = clubcode;
	}

	public Boolean getEmailValidated() {
		return emailValidated;
	}

	public void setEmailValidated(final Boolean emailValidated) {
		this.emailValidated = emailValidated;
	}

	public Boolean getVerifyPhone() {
		return verifyPhone;
	}

	public void setVerifyPhone(Boolean verifyPhone) {
		this.verifyPhone = verifyPhone;
	}

}
