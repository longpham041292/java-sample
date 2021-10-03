package asia.cmg.f8.profile.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import asia.cmg.f8.common.spec.user.ExtendUserType;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.profile.database.entity.UserPtiMatchEntity;
import asia.cmg.f8.profile.domain.entity.UserPTiMatch;
import asia.cmg.f8.profile.domain.entity.UserStatus;

public class UserProfileResponse {

	private String uuid;
	private String name;
	private String username;
	private UserType userType;
	
	@JsonProperty("extend_user_type")
	private ExtendUserType extendUserType;
	
	private String picture;
	private String level;
	private Boolean activated;
	private Boolean approved;
	private Boolean following;
	private Double rated;
	private UserStatus status;
	private CustomProfileDTO profile;
	
	@JsonProperty("enable_subscribe")
	private Boolean enableSubscribe;

	@JsonProperty("questions_ptimatch")
	private List<QuestionPTiMatch> questionsPTIMatch;
	
	@JsonProperty("user_ptimatch")
	private UserPtiMatchEntity userPTiMatch;

	public static class Builder {

		private String uuid;
		private String name;
		private String username;
		private UserType userType;
		private ExtendUserType extendUserType;
		private String picture;
		private String level;
		private Boolean activated;
		private Boolean approved;
		private Boolean following;
		private Double rated;
		private List<QuestionPTiMatch> questionsPTIMatch;
		private UserStatus status;
		private CustomProfileDTO profile;
		private Boolean enableSubscribe;
		private UserPtiMatchEntity userPTiMatch;

		private Builder() {
			// TODO Auto-generated constructor stub
		}

		public UserProfileResponse build() {
			UserProfileResponse userProfile = new UserProfileResponse();

			userProfile.setUuid(uuid);
			userProfile.setName(name);
			userProfile.setUsername(username);
			userProfile.setUserType(userType);
			userProfile.setExtendUserType(extendUserType);
			userProfile.setPicture(picture);
			userProfile.setLevel(level);
			userProfile.setActivated(activated);
			userProfile.setApproved(approved);
			userProfile.setFollowing(following);
			userProfile.setRated(rated);
			userProfile.setQuestionsPTIMatch(questionsPTIMatch);
			userProfile.setStatus(status);
			userProfile.setProfile(profile);
			userProfile.setEnableSubscribe(enableSubscribe);
			userProfile.setUserPTiMatch(userPTiMatch);

			return userProfile;
		}
		
		public Builder setUserPTiMatch(UserPtiMatchEntity userPTiMatch) {
			this.userPTiMatch = userPTiMatch;
			return this;
		}
		
		public Builder setEnableSubscribe(Boolean enableSubscribe) {
			this.enableSubscribe = enableSubscribe;
			return this;
		}

		public Builder setStatus(UserStatus status) {
			this.status = status;
			return this;
		}

		public Builder setProfile(CustomProfileDTO profile) {
			this.profile = profile;
			return this;
		}

		public Builder isActivated(Boolean isActivated) {
			this.activated = isActivated;
			return this;
		}

		public Builder setUserType(UserType userType) {
			this.userType = userType;
			return this;
		}
		
		public Builder setExtendUserType(ExtendUserType extendUserType) {
			this.extendUserType = extendUserType;
			return this;
		}
		
		public Builder setQuestionsPTIMatch(List<QuestionPTiMatch> questionsPTIMatch) {
			this.questionsPTIMatch = questionsPTIMatch;
			return this;
		}
		
		public Builder setRated(Double rated) {
			this.rated = rated;
			return this;
		}

		public Builder isFollowing(Boolean isFollowing) {
			this.following = isFollowing;
			return this;
		}

		public Builder setApproved(Boolean approved) {
			this.approved = approved;
			return this;
		}

		public Builder setLevel(String level) {
			this.level = level;
			return this;
		}

		public Builder setPicture(String picture) {
			this.picture = picture;
			return this;
		}

		public Builder setUuid(String uuid) {
			this.uuid = uuid;
			return this;
		}

		public Builder setName(String name) {
			this.name = name;
			return this;
		}

		public Builder setUsername(String username) {
			this.username = username;
			return this;
		}
	}
	
	public UserPtiMatchEntity getUserPTiMatch() {
		return userPTiMatch;
	}

	public void setUserPTiMatch(UserPtiMatchEntity userPTiMatch) {
		this.userPTiMatch = userPTiMatch;
	}

	public void setEnableSubscribe(Boolean enableSubscribe) {
		this.enableSubscribe = enableSubscribe;
	}

	public Boolean getEnableSubscribe() {
		return enableSubscribe;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public ExtendUserType getExtendUserType() {
		return extendUserType;
	}

	public void setExtendUserType(ExtendUserType extendUserType) {
		this.extendUserType = extendUserType;
	}

	public List<QuestionPTiMatch> getQuestionsPTIMatch() {
		return questionsPTIMatch;
	}

	public void setQuestionsPTIMatch(List<QuestionPTiMatch> questionsPTIMatch) {
		this.questionsPTIMatch = questionsPTIMatch;
	}

	public static Builder builder() {
		return new Builder();
	}

	public Double getRated() {
		return rated;
	}

	public void setRated(Double rated) {
		this.rated = rated;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public Boolean getApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}

	public Boolean getFollowing() {
		return following;
	}

	public void setFollowing(Boolean following) {
		this.following = following;
	}

	public Boolean getActivated() {
		return activated;
	}

	public void setActivated(Boolean activated) {
		this.activated = activated;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public CustomProfileDTO getProfile() {
		return profile;
	}

	public void setProfile(CustomProfileDTO profile) {
		this.profile = profile;
	}
}
