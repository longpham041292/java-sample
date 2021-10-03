package asia.cmg.f8.profile.domain.entity.home;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "home_section")
public class HomeSectionEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@Column(name = "section_code", length = 50)
	@JsonIgnore
	private int sectionCode;
	
	@Column(name = "section_name_vi", length = 255)
	@JsonProperty("section_name_vi")
	private String sectionNameVI;
	
	@Column(name = "section_name_en", length = 255)
	@JsonProperty("section_name_en")
	private String sectionNameEN;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "section_type")
	@JsonProperty("section_type")
	private ESectionType sectionType;
	
	@Column(name = "`order`", nullable = false)
	private int order;
	
	@Column(name = "account_uuid")
	@JsonIgnore(value = true)
	private String accountUuid;
		
	@Column(name = "max_item")
	@JsonIgnore(value = true)
	private int maxItem;
	
	@Column(name = "activated", columnDefinition = "tinyint(1) default 1")
	@JsonIgnore(value = true)
	private Boolean activated;
	
	@Column(name = "section_data")
	@JsonIgnore(value = true)
	private String sectionData;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ESectionType getSectionType() {
		return sectionType;
	}

	public void setSectionType(ESectionType sectionType) {
		this.sectionType = sectionType;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getSectionCode() {
		return sectionCode;
	}

	public void setSectionCode(int sectionCode) {
		this.sectionCode = sectionCode;
	}

	public String getSectionNameVI() {
		return sectionNameVI;
	}

	public void setSectionNameVI(String sectionNameVI) {
		this.sectionNameVI = sectionNameVI;
	}

	public String getSectionNameEN() {
		return sectionNameEN;
	}

	public void setSectionNameEN(String sectionNameEN) {
		this.sectionNameEN = sectionNameEN;
	}

	public String getAccountUuid() {
		return accountUuid;
	}

	public void setAccountUuid(String accountUuid) {
		this.accountUuid = accountUuid;
	}

	public int getMaxItem() {
		return maxItem;
	}

	public void setMaxItem(int maxItem) {
		this.maxItem = maxItem;
	}

	public Boolean getActivated() {
		return activated;
	}

	public void setActivated(Boolean activated) {
		this.activated = activated;
	}

	public String getSectionData() {
		return sectionData;
	}

	public void setSectionData(String sectionData) {
		this.sectionData = sectionData;
	}
}
