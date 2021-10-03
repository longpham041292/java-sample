package asia.cmg.f8.gateway.security.auth;


import asia.cmg.f8.common.spec.user.ExtendUserType;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.gateway.security.api.UserDetail;
import asia.cmg.f8.gateway.security.dto.UserSignup;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

/**
 * Created on 11/3/16.
 */
@SuppressWarnings("squid:S2384")
public abstract class AbstractUserDetail implements UserDetail {

    private static final long serialVersionUID = 5832222099944969471L;

    @JsonIgnore
    @SuppressWarnings("squid:S1948")
    private final Map<String, Object> userInfo;

    protected AbstractUserDetail(final Map<String, Object> userInfo) {
        this.userInfo = userInfo;
    }

    public Map<String, Object> getUserInfo() {
        return userInfo;
    }

    @Override
    public boolean isActivated() {
        final Boolean activated = (Boolean) userInfo.get("activated");
        if (activated == null) {
            return Boolean.FALSE;
        }
        return activated;
    }

    @Override
    public boolean isOnBoarded() {
        final Boolean completed = (Boolean) userInfo.get("onBoardCompleted");
        if (completed == null) {
//          if(getDocumentApprovalStatus() != null && getDocumentApprovalStatus().equalsIgnoreCase("ONBOARD")){
//        	  return Boolean.TRUE;  
//          }
        	return Boolean.FALSE;
       }
        return completed;
    }
    
	@Override
	public boolean isOnBoardCompleted() {
		final Boolean completed = (Boolean) userInfo.get("onBoardCompleted");
		if (completed == null) {
			return Boolean.FALSE;
		}
		return completed;
	}

    @Override
    public String getUserType() {
        return (String) userInfo.get("userType");
    }

	@Override
	public String getPermissions() {
		return (String) userInfo.get("permissions");
	}

    @Override
    public String getUuid() {
        return (String) userInfo.get("uuid");
    }

    @Override
    public String getUserName() {
        return (String) getUserInfo().get("username");
    }

    @Override
    public String getPicture() {
        return (String) getUserInfo().get("picture");
    }

    @Override
    public String getName() {
        return (String) getUserInfo().get("name");
    }

    @Override
    public String getLanguage() {
        return (String) getUserInfo().get("language");
    }

    @Nullable
    @Override
    public String getDocumentApprovalStatus() {
        final Object status = getUserInfo().get("status");
        if (status instanceof Map) {
            final Map statusMap = (Map) status;
            return (String) statusMap.get("document_status");
        }
        return null;
    }

    @Nullable
    @Override
    public String getFacebookId() {
        final Map<String, Object> faceInfo = (Map<String, Object>) getUserInfo().get("facebook");
        if (faceInfo != null) {
            return (String) faceInfo.get("id");
        }
        return null;
    }
    
    @Nullable
    @Override
    public String getAppleId() {
    	return (String) getUserInfo().get("appleId");
    }
    
    @Nullable
    @Override
    public ExtendUserType getExtendUserType() {
    	final String extUserType = (String)getUserInfo().get("extend_user_type");
    	if(UserType.PT.name().equalsIgnoreCase(getUserType())) {
    		try {
    			if(Objects.isNull(extUserType)) {
        			return ExtendUserType.PT_NORMAL;
        		}
				ExtendUserType extendUserTypeEnum = ExtendUserType.valueOf(extUserType.toUpperCase());
				return extendUserTypeEnum;
			} catch (Exception e) {
				return ExtendUserType.PT_NORMAL;
			}
    	}
    	
    	return null;
    }
    
    @Override
    public Integer getLastQuestionSequence() {
    	Integer lastQuestionSequence = (Integer)getUserInfo().get("last_question_sequence");
    	
    	return lastQuestionSequence == null ? 0 : lastQuestionSequence;
    }
    
	@Override
	public Boolean updatedProfile() {
		final Boolean updatedProfile = (Boolean) getUserInfo().get("updated_profile");
		if (updatedProfile == null) {
			return Boolean.TRUE;
		}
		return updatedProfile;
	}
}
