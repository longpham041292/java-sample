package asia.cmg.f8.gateway.security.dto;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Email;

import asia.cmg.f8.common.spec.user.GenderType;
import asia.cmg.f8.common.spec.user.ProfileType;
import asia.cmg.f8.common.spec.user.UserType;

public class UserSignup {

    @NotNull
    private String name;

    @NotNull
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

    public static class Facebook {

        private String id;
        private String name;

        public Facebook() {
			super();
		}
        
        public Facebook(String name, String id) {
			super();
			this.name = name;
			this.id = id;
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
    }
    
    public static class Apple {

        private String name;
        private String id;
        

        public Apple() {
			super();
		}
        
        public Apple(String name, String id) {
			super();
			this.name = name;
			this.id = id;
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

}
