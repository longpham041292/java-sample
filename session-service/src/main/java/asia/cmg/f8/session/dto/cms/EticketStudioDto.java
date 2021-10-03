package asia.cmg.f8.session.dto.cms;

public class EticketStudioDto {

	private String id;

	private String name;
	
	private String address;
	
	private String logo_link;
	
	private String image_link;

	public String getImage_link() {
		return image_link;
	}

	public void setImage_link(String image_link) {
		this.image_link = image_link;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLogo_link() {
		return logo_link;
	}

	public void setLogo_link(String logo_link) {
		this.logo_link = logo_link;
	}
}
