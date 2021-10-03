package asia.cmg.f8.profile.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import asia.cmg.f8.common.spec.user.Media;
import asia.cmg.f8.profile.dto.UserProfileResponse.Builder;

public class CustomProfileDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String birthday;
	private String phone;
	private Set<String> skills;
	private String tagline;
	private Set<Media> covers;
	private String bio;
	
	public static class Builder {
		private String birthday;
		private String phone;
		private Set<String> skills;
		private String tagline;
		private Set<Media> covers;
		private String bio;
		
		private Builder() {
			// TODO Auto-generated constructor stub
		}
		
		public CustomProfileDTO build() {
			CustomProfileDTO profile = new CustomProfileDTO();
			
			profile.setBirthday(this.birthday);
			profile.setCovers(this.covers);
			profile.setPhone(phone);
			profile.setSkills(skills);
			profile.setTagline(tagline);
			profile.setBio(bio);
			
			return profile;
		}

		public Builder setBio(String bio) {
			this.bio = bio;
			return this;
		}

		public Builder setBirthday(String birthday) {
			this.birthday = birthday;
			return this;
		}

		public Builder setPhone(String phone) {
			this.phone = phone;
			return this;
		}

		public Builder setSkills(Set<String> skills) {
			this.skills = skills;
			return this;
		}

		public Builder setTagline(String tagline) {
			this.tagline = tagline;
			return this;
		}

		public Builder setCovers(Set<Media> covers) {
			this.covers = covers;
			return this;
		}
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Set<String> getSkills() {
		return skills;
	}
	public void setSkills(Set<String> skills) {
		this.skills = skills;
	}
	public String getTagline() {
		return tagline;
	}
	public void setTagline(String tagline) {
		this.tagline = tagline;
	}
	public Set<Media> getCovers() {
		return covers;
	}
	public void setCovers(Set<Media> covers) {
		this.covers = covers;
	}
}
