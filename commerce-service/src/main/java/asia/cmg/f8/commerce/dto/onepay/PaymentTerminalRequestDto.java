package asia.cmg.f8.commerce.dto.onepay;

public class PaymentTerminalRequestDto {
	
	private String id;
	
	private String type;
	
	private String ip;
	
	private String location;

	public PaymentTerminalRequestDto(String id, String type, String ip, String location){
		this.id = id;
		this.type = type;
		this.ip = ip;
		this.location = location;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
