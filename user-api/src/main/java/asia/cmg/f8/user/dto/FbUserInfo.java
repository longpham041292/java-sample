package asia.cmg.f8.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FbUserInfo {
	@JsonProperty(value = "name")
    private String name;
	@JsonProperty(value = "id")
    private String id;
	@JsonProperty(value = "email")
    private String email;

	
    public FbUserInfo() {
		super();
	}

	public FbUserInfo(String name, String id, String email) {
		super();
		this.name = name;
		this.id = id;
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }
}