package asia.cmg.f8.gateway.security.test;

import asia.cmg.f8.common.spec.user.ExtendUserType;
import asia.cmg.f8.gateway.security.api.UserDetail;

import javax.annotation.Nullable;

/**
 * Created on 11/3/16.
 */
public class TestUserDetail implements UserDetail {

    private final TestOauthToken annotation;

    public TestUserDetail(final TestOauthToken annotation) {
        this.annotation = annotation;
    }

    @Override
    public boolean isOnBoarded() {
        return false;
    }

    @Override
    public String getUserType() {
        return null;
    }

    @Override
    public String getUuid() {
        return annotation.uuid();
    }

    @Override
    public boolean isActivated() {
        return annotation.activated();
    }

    @Override
    public String getUserName() {
        return annotation.username();
    }

    @Override
    public String getPicture() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getLanguage() {
        return null;
    }

    @Nullable
    @Override
    public String getDocumentApprovalStatus() {
        return null;
    }

    @Nullable
    @Override
    public String getFacebookId() {
        return null;
    }
    
    @Nullable
    @Override
    public String getAppleId() {
        return null;
    }

	@Override
	@Nullable
	public String getPermissions() {
		return null;
	}
	
	@Nullable
    @Override
    public ExtendUserType getExtendUserType() {
    	return null;
    }
	
	@Override
    public Integer getLastQuestionSequence() {
    	return null;
    }

	@Override
	public boolean isOnBoardCompleted() {
		return false;
	}

	@Override
	public Boolean updatedProfile() {
		// TODO Auto-generated method stub
		return false;
	}
}
