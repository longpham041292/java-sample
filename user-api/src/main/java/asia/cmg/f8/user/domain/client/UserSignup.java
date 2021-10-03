package asia.cmg.f8.user.domain.client;


import asia.cmg.f8.common.spec.user.GenderType;
import asia.cmg.f8.common.spec.user.ProfileType;
import asia.cmg.f8.common.spec.user.UserGrantType;
import asia.cmg.f8.common.spec.user.UserType;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class UserSignup {

    @NotNull
    private String name;

    
    @Email
    private String email;

    @NotNull
    @Pattern(regexp = "^[A-Za-z0-9]{4,30}$")
    private String username;
    
    private String usercode;
    private String clubcode;

    private String phone;
    private String password;
    private String picture;
    private String birthday;
    private String language;
    private UserType userType;
    private String country;
    private String city;
    private ProfileType privacy;
    private GenderType gender;
    private Facebook facebook;
    private Apple apple;
    private UserGrantType grantType;
    private String authProviderId;
    
    private Boolean updatedProfile;
    private Boolean emailValidate;

    public static class Facebook {

        private String accessToken;
        private String id;

        public Facebook() {
			super();
		}
        
        public Facebook(String accessToken) {
			super();
			this.accessToken = accessToken;
		}
        public Facebook(String accessToken, String id) {
			super();
			this.accessToken = accessToken;
			this.id = id;
		}

		public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(final String accessToken) {
            this.accessToken = accessToken;
        }

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }

    }
    public static class Apple {

        private String name;
        private String id;
        private String code;
        private String refreshToken;


		public Apple() {
			super();
		}
        
        public Apple(String code, String name, String id) {
			super();
			this.name = name;
			this.id = id;
			this.code = code;
		}


		public Apple(String name, String id, String code, String refreshToken) {
			super();
			this.name = name;
			this.id = id;
			this.code = code;
			this.refreshToken = refreshToken;
		}

		public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

        public String getRefreshToken() {
			return refreshToken;
		}

		public void setRefreshToken(String refreshToken) {
			this.refreshToken = refreshToken;
		}
		
		
    }


    public String getName() {
        return name;
    }


    public void setName(final String name) {
        this.name = name;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(final String email) {
        this.email = email;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(final String username) {
        this.username = username;
    }


    public String getPhone() {
        return phone;
    }


    public void setPhone(final String phone) {
        this.phone = phone;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(final String password) {
        this.password = password;
    }


    public String getPicture() {
        return picture;
    }


    public void setPicture(final String picture) {
        this.picture = picture;
    }


    public String getBirthday() {
        return birthday;
    }


    public void setBirthday(final String birthday) {
        this.birthday = birthday;
    }


    public String getLanguage() {
        return language;
    }


    public void setLanguage(final String language) {
        this.language = language;
    }


    public UserType getUserType() {
        return userType;
    }


    public void setUserType(final UserType userType) {
        this.userType = userType;
    }


    public String getCountry() {
        return country;
    }


    public void setCountry(final String country) {
        this.country = country;
    }


    public String getCity() {
        return city;
    }


    public void setCity(final String city) {
        this.city = city;
    }


    public ProfileType getPrivacy() {
        return privacy;
    }


    public void setPrivacy(final ProfileType privacy) {
        this.privacy = privacy;
    }


    public GenderType getGender() {
        return gender;
    }


    public void setGender(final GenderType gender) {
        this.gender = gender;
    }


    public Facebook getFacebook() {
        return facebook;
    }


    public void setFacebook(final Facebook facebook) {
        this.facebook = facebook;
    }


	public String getUsercode() {
		return usercode;
	}


	public void setUsercode(final String usercode) {
		this.usercode = usercode;
	}


	public String getClubcode() {
		return clubcode;
	}


	public void setClubcode(final String clubcode) {
		this.clubcode = clubcode;
	}


	public Apple getApple() {
		return apple;
	}


	public void setApple(Apple apple) {
		this.apple = apple;
	}


	public UserGrantType getGrantType() {
		return grantType;
	}


	public void setGrantType(UserGrantType grantType) {
		this.grantType = grantType;
	}


	public String getAuthProviderId() {
		return authProviderId;
	}


	public void setAuthProviderId(String authProviderId) {
		this.authProviderId = authProviderId;
	}


	public Boolean getUpdatedProfile() {
		return updatedProfile;
	}


	public void setUpdatedProfile(Boolean updatedProfile) {
		this.updatedProfile = updatedProfile;
	}


	public Boolean getEmailValidate() {
		return emailValidate;
	}


	public void setEmailValidate(Boolean emailValidate) {
		this.emailValidate = emailValidate;
	}
	
	
	

}
