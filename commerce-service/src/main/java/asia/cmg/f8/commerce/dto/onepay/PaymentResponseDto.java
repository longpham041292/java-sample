package asia.cmg.f8.commerce.dto.onepay;

public class PaymentResponseDto {
	
	private String id;
	
	private String state;
	
	private LinkDto links;
	
	private PaymentAuthorizationResponseDto authorization;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public LinkDto getLinks() {
		return links;
	}

	public void setLinks(LinkDto links) {
		this.links = links;
	}

	public PaymentAuthorizationResponseDto getAuthorization() {
		return authorization;
	}

	public void setAuthorization(PaymentAuthorizationResponseDto authorization) {
		this.authorization = authorization;
	}
	
}
