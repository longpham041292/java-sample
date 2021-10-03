package asia.cmg.f8.report.entity.database;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "question_option")
public class QuestionOptionEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer id;
	
	@Column(name = "`key`", length = 100)
	public String key;
	
	@Column(length = 2048)
	public String text;
	
	@Column(length = 50)
	public String type; // 'icon' or 'text'
	
	@Column(length = 1024)
	public String iconUrl;

	@Column(name = "`sequence`")
	public Integer sequence;
	
	@Column(name = "filtered")
	public Boolean filtered;
	
	@Column(name = "weight")
	public Integer weight;
	
	@ManyToOne
	@JoinColumn(name = "question_id", nullable = false)
	@JsonIgnore
	private QuestionEntity question;
	
	public QuestionEntity getQuestion() {
		return question;
	}

	public void setQuestion(QuestionEntity question) {
		this.question = question;
	}
}
