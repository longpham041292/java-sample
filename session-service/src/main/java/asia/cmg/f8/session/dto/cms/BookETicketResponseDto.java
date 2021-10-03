package asia.cmg.f8.session.dto.cms;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.session.entity.credit.CreditBookingSessionStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookETicketResponseDto {
	
	private long id; 
	
	@JsonProperty("studio_uuid")
	@Column(name = "studio_uuid")
	private String studioUuid;
	
	@JsonProperty("studio_name")
	@Column(name = "studio_name")
	private String studioName;
	
    @Enumerated(EnumType.ORDINAL)
    private CreditBookingSessionStatus status = CreditBookingSessionStatus.BOOKED;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStudioUuid() {
		return studioUuid;
	}

	public void setStudioUuid(String studioUuid) {
		this.studioUuid = studioUuid;
	}

	public String getStudioName() {
		return studioName;
	}

	public void setStudioName(String studioName) {
		this.studioName = studioName;
	}

	public CreditBookingSessionStatus getStatus() {
		return status;
	}

	public void setStatus(CreditBookingSessionStatus status) {
		this.status = status;
	}
    
    
}
