package asia.cmg.f8.profile.database.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import asia.cmg.f8.profile.dto.UserPTiMatchDTO_V2;

@Entity
@Table(name = "user_ptimatch")
@SqlResultSetMapping(name = "UserPTiMatchDTOMapping", 
					classes = {
							@ConstructorResult(
									targetClass = UserPTiMatchDTO_V2.class,
									columns = {
											@ColumnResult(name = "eu_uuid"),
											@ColumnResult(name = "pt_uuid"),
											@ColumnResult(name = "average"),
											@ColumnResult(name = "personality"),
											@ColumnResult(name = "training_style"),
											@ColumnResult(name = "interest"),
											@ColumnResult(name = "pt_level"),
											@ColumnResult(name = "pt_booking_credit"),
									})
					})
@NamedNativeQueries({
	@NamedNativeQuery(name = "UserPtiMatchEntity.getByEuUuidAndPtLevel_V2", 
			query = "SELECT pti.eu_uuid, pti.pt_uuid, pti.average, pti.personality, pti.training_style, pti.interest, su.`level` as pt_level, lvl.pt_booking_credit "
					+ "FROM user_ptimatch pti JOIN session_users su ON pti.pt_uuid = su.uuid AND su.`level` in :levels "
					+ "                       JOIN `level` lvl ON su.`level` = lvl.code "
					+ "WHERE eu_uuid = :eu_uuid AND average > 0 AND su.doc_status = 'APPROVED' "
					+ "ORDER BY average DESC "
					+ "LIMIT :offset, :size",
			resultSetMapping = "UserPTiMatchDTOMapping"),

	@NamedNativeQuery(name = "UserPtiMatchEntity.getByEuUuid_V2", 
			query = "SELECT pti.eu_uuid, pti.pt_uuid, pti.average, pti.personality, pti.training_style, pti.interest, su.`level` as pt_level, lvl.pt_booking_credit "
					+ "FROM user_ptimatch pti JOIN session_users su ON pti.pt_uuid = su.uuid "
					+ "                       JOIN `level` lvl ON su.`level` = lvl.code "
					+ "WHERE eu_uuid = :eu_uuid AND average > 0 AND su.doc_status = 'APPROVED' "
					+ "LIMIT :offset, :size",
			resultSetMapping = "UserPTiMatchDTOMapping"),
	
	@NamedNativeQuery(name = "UserPtiMatchEntity.getByEuUuidAndPtSkills_V2",
			query = "SELECT pti.eu_uuid, pti.pt_uuid, pti.average, pti.personality, pti.training_style, pti.interest, su.`level` as pt_level, lvl.pt_booking_credit "
					+ "FROM user_ptimatch pti JOIN session_users su ON pti.pt_uuid = su.uuid "
					+ "                       JOIN `level` lvl ON su.`level` = lvl.code "
					+ "						  JOIN user_skills sk ON su.uuid = sk.user_uuid AND sk.skill_key IN :skills "
					+ "WHERE pti.eu_uuid = :eu_uuid AND average > 0 AND su.doc_status = 'APPROVED' "
					+ "ORDER BY average DESC "
					+ "LIMIT :offset, :size",
			resultSetMapping = "UserPTiMatchDTOMapping"),
	
	@NamedNativeQuery(name = "UserPtiMatchEntity.getByEuUuidAndPtEnableSubscribe_V2",
			query = "SELECT pti.eu_uuid, pti.pt_uuid, pti.average, pti.personality, pti.training_style, pti.interest, su.`level` as pt_level, lvl.pt_booking_credit "
					+ "FROM user_ptimatch pti JOIN session_users su ON pti.pt_uuid = su.uuid AND su.enable_subscribe = 1 "
					+ "                       JOIN `level` lvl ON su.`level` = lvl.code "
					+ "WHERE eu_uuid = :eu_uuid AND average > 0 "
					+ "ORDER BY average DESC "
					+ "LIMIT :offset, :size",
			resultSetMapping = "UserPTiMatchDTOMapping")
	
})

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
	@JsonIgnore
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
		return String.format("{\"id\": %s, \"eu_uuid\": %s, \"pt_uuid\": %s, \"average\": %s, \"personality\": %s, \"trainingStyle\": %s, \"interest\": %s}", 
				id, euUuid, ptUuid, average, personality, trainingStyle, interest);
	}
}
