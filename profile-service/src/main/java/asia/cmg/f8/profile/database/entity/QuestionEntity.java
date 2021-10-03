package asia.cmg.f8.profile.database.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.profile.domain.entity.QuestionType;

@Entity
@Table(name = "question")
public class QuestionEntity implements Serializable {
	
	public static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    
	@Column(name = "question_type")
	@Enumerated(EnumType.STRING)
    public QuestionType questionType;
	
	@Column(name = "`key`", length = 255)
    public String key;
	
	@Column(name = "title", length = 255)
    public String title;
	
	@Column(length = 1024)
    public String description;
	
	@Column(name = "filtered")
    public Boolean filtered;
	
	@Column(name = "required")
    public Boolean required;
	
	@Column(name = "hide")
    public Boolean hide;
	
	@Column(name = "weight")
    public Integer weight;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "question", targetEntity = QuestionOptionEntity.class)
    private List<QuestionOptionEntity> options = new ArrayList<QuestionOptionEntity>();
    
    @Column(name = "`sequence`")
    public Integer sequence;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "used_for", length = 50)
    public UserType usedFor;
    
    @Column(name = "tags", length = 1024)
    public String tags;
    
    @Column(name = "limit_option_selection")
    public Integer limitedUserSelection;
    
    @Column(name = "language", length = 50)
    public String language;

	public List<QuestionOptionEntity> getOptions() {
		return options;
	}

	public void setOptions(List<QuestionOptionEntity> options) {
		this.options = options;
		options.forEach(option -> {
			option.setQuestion(this);
		});
	}
	
	public void setOption(QuestionOptionEntity option) {
		this.options.add(option);
		option.setQuestion(this);
	}
}
