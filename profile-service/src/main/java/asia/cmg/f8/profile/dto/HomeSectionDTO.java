package asia.cmg.f8.profile.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.profile.domain.entity.home.HomeSectionEntity;
import asia.cmg.f8.profile.domain.entity.home.TrendingEventEntity;

public class HomeSectionDTO<T> implements Serializable {

	public static final long serialVersionUID = 1L;
	
	private final String VN_LOCALE = "vi";
	private final String EN_LOCALE = "en";
	
	@JsonProperty("section_id")
	public int sectionId;
	
	@JsonProperty("section_code")
	public int sectionCode;
	
	@JsonProperty("section_name")
	public String sectionName;
	
	@JsonProperty("section_type")
	public String sectionType;
	
	public int order;
	
	public List<T> items;
	
	public HomeSectionDTO() {
	}
	
	public HomeSectionDTO(HomeSectionEntity sectionEntity, List<T> data, String language) {
		this.items = data;
		this.order = sectionEntity.getOrder();
		this.sectionCode = sectionEntity.getSectionCode();
		this.sectionId = sectionEntity.getId();
		this.sectionName = VN_LOCALE.compareToIgnoreCase(language) == 0 ? sectionEntity.getSectionNameVI() : sectionEntity.getSectionNameEN();
		this.sectionType = sectionEntity.getSectionType().name();
	}
}
