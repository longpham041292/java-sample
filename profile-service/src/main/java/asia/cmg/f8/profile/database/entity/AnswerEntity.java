package asia.cmg.f8.profile.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "answer", uniqueConstraints = @UniqueConstraint(name = "owner_question_answer_UNQ", columnNames = {"owner_uuid", "question_key", "option_key"}))
public class AnswerEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "question_key", length = 50)
	private String questionKey;
	
	@Column(name = "option_key", length = 50)
	private String optionKey;
	
	@Column(name = "owner_uuid", length = 50)
	private String ownerUuid;
	
	@Column(name = "user_type", length = 50)
	private String userType;

	public String getQuestionKey() {
		return questionKey;
	}

	public void setQuestionKey(String questionKey) {
		this.questionKey = questionKey;
	}

	public String getOptionKey() {
		return optionKey;
	}

	public void setOptionKey(String optionKey) {
		this.optionKey = optionKey;
	}

	public String getOwnerUuid() {
		return ownerUuid;
	}

	public void setOwnerUuid(String ownerUuid) {
		this.ownerUuid = ownerUuid;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}
}
