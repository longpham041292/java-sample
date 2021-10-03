package asia.cmg.f8.profile.domain.entity.home;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "trending_event")
public class TrendingEventEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private String image;
	private String thumbnail;
	private String title;
	
	@Column(name = "content", columnDefinition = "TEXT")
	private String content;
	
	@JsonProperty("short_content")
	@Column(name = "short_content", length = 2048)
	private String shortContent;
	
	@JsonProperty("content_type")
	@Column(name = "content_type")
	private String contentType;
	
	@Column(name = "`order`", nullable = false)
	private int order;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "section_id", nullable = true)
	private HomeSectionEntity section;
	
	@Column(name = "language", length = 50)
	private String language;
	
	@Column(name = "url", length = 2048)
	private String url;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "function_id")
	@JsonProperty("function")
	private TrendingEventActionEntity action;
	
	@CreationTimestamp
	@Column(name = "created_date", updatable = false)
	@JsonIgnore
	private Date createdDate;
	
	@UpdateTimestamp
	@Column(name = "modified_date")
	@JsonIgnore
	private Date modifiedDate;
	
	@Column(name = "activated", columnDefinition = "boolean default true")
	private Boolean activated = Boolean.TRUE;
	
	public Boolean getActivated() {
		return activated;
	}

	public void setActivated(Boolean activated) {
		this.activated = activated;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getShortContent() {
		return shortContent;
	}

	public void setShortContent(String shortContent) {
		this.shortContent = shortContent;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
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

	public HomeSectionEntity getSection() {
		return section;
	}

	public void setSection(HomeSectionEntity section) {
		this.section = section;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public TrendingEventActionEntity getAction() {
		return action;
	}

	public void setAction(TrendingEventActionEntity action) {
		this.action = action;
	}
}
