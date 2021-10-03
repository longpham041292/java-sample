package asia.cmg.f8.session.dto.cms;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CMSClassCourseDTO {
	
	private Long id;
	
	private String name;
	
	@JsonProperty("categories")
	private Set<String> categories;
	
	@JsonProperty("service_fee_percent")
	private Double serviceFeePercent;
	
	@JsonProperty("payable_no_show_fee_percent")
	private Double noShowFeePercent;

	public Set<String> getCategories() {
		return categories;
	}

	public void setCategories(Set<String> categories) {
		this.categories = categories;
	}

	public Double getServiceFeePercent() {
		return serviceFeePercent == null ? 0 : serviceFeePercent;
	}

	public void setServiceFeePercent(Double serviceFeePercent) {
		this.serviceFeePercent = serviceFeePercent;
	}

	public Double getNoShowFeePercent() {
		return noShowFeePercent == null ? 0 : noShowFeePercent;
	}

	public void setNoShowFeePercent(Double noShowFeePercent) {
		this.noShowFeePercent = noShowFeePercent;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
