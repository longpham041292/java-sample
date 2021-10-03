package asia.cmg.f8.session.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "user_ptimatch")
public class UserPtiMatchEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "eu_uuid", length = 50)
	private String euUuid;

	@Column(name = "pt_uuid", length = 50)
	private String ptUuid;

	@Column(name = "average")
	private Double average = 0d;

	@Column(name = "personality")
	private Double personality = 0d;

	@Column(name = "training_style")
	private Double trainingStyle = 0d;

	@Column(name = "interest")
	private Double interest = 0d;

	@CreationTimestamp
	@Column(name = "created_date", updatable = false)
	@JsonProperty("created_date")
	private Date createdDate;

	@UpdateTimestamp
	@Column(name = "modified_date")
	@JsonIgnore
	private Date modifiedDate;

	@Column(name = "modified_count")
	@JsonIgnore
	private Integer modifiedCount = 0;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEuUuid() {
		return euUuid;
	}

	public void setEuUuid(String euUuid) {
		this.euUuid = euUuid;
	}

	public String getPtUuid() {
		return ptUuid;
	}

	public void setPtUuid(String ptUuid) {
		this.ptUuid = ptUuid;
	}

	public Double getAverage() {
		return average;
	}

	public void setAverage(Double average) {
		this.average = average;
	}

	public Double getPersonality() {
		return personality;
	}

	public void setPersonality(Double personality) {
		this.personality = personality;
	}

	public Double getTrainingStyle() {
		return trainingStyle;
	}

	public void setTrainingStyle(Double trainingStyle) {
		this.trainingStyle = trainingStyle;
	}

	public Double getInterest() {
		return interest;
	}

	public void setInterest(Double interest) {
		this.interest = interest;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Integer getModifiedCount() {
		return modifiedCount;
	}

	public void setModifiedCount(Integer modifiedCount) {
		this.modifiedCount = modifiedCount;
	}

	public String toString() {
		return String.format(
				"{\"id\": %s, \"eu_uuid\": %s, \"pt_uuid\": %s, \"average\": %s, \"personality\": %s, \"trainingStyle\": %s, \"interest\": %s}",
				id, euUuid, ptUuid, average, personality, trainingStyle, interest);
	}
}
