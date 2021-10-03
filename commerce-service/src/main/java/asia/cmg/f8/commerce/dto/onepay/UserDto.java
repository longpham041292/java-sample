package asia.cmg.f8.commerce.dto.onepay;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDto {

	@JsonProperty(value = "id")
	private String id;

	@JsonProperty(value = "group_id")
	private String groupId;

	@JsonProperty(value = "ref_id")
	private String refId;

	@JsonProperty(value = "first_name")
	private String firstName;

	@JsonProperty(value = "last_name") 
	private String lastName;

	@JsonProperty(value = "mobile")
	private String mobile;

	@JsonProperty(value = "email")
	private String email;

	@JsonProperty(value = "address")
	private UserAddressDto address;

	@JsonProperty(value = "state")
	private String state;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public UserAddressDto getAddress() {
		return address;
	}

	public void setAddress(UserAddressDto address) {
		this.address = address;
	}

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
}
