package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BasicUserInfoWithProfile {

	
	   @JsonProperty("user_uuid")
	    private String uuid;

	    @JsonProperty("name")
	    private String name;
	    
	    @JsonProperty("username")
	    private String username;

	    @JsonProperty("picture")
	    private String picture;
	    
	    private Profile profile;

	 public BasicUserInfoWithProfile(){
		 
	 }   
	 
     public BasicUserInfoWithProfile(final String uuid,final String name, final  String username,final String picture ,final Profile profile){
		
    	 this.uuid = uuid;
    	 this.name = name;
    	 this.username = username;
    	 this.picture = picture;
    	 this.profile = profile;
    	 
	 } 
	    
		public String getUuid() {
			return uuid;
		}

		public void setUuid(final String uuid) {
			this.uuid = uuid;
		}

		public String getName() {
			return name;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(final String username) {
			this.username = username;
		}

		public String getPicture() {
			return picture;
		}

		public void setPicture(final String picture) {
			this.picture = picture;
		}

		public Profile getProfile() {
			return profile;
		}

		public void setProfile(final Profile profile) {
			this.profile = profile;
		}
	    
	    
}
