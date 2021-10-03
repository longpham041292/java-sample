package asia.cmg.f8.report.entity.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "user_skills")
public class UserSkillsEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	@JsonIgnore
	private BasicUserEntity user;
	
	@Column(name = "user_uuid")
	@JsonIgnore
	private String userUuid;
	
	@Column(name = "skill_key")
	private String skillKey;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BasicUserEntity getUser() {
		return user;
	}

	public void setUser(BasicUserEntity user) {
		this.user = user;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public String getSkillKey() {
		return skillKey;
	}

	public void setSkillKey(String skillKey) {
		this.skillKey = skillKey;
	}
}
