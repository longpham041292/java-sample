package asia.cmg.f8.commerce.dto.onepay;

public class PaymentOrderRequestDto {

	private String id;
	private String information;

	public PaymentOrderRequestDto(final String id, final String information) {
		this.id = id;
		this.information = information;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

}
