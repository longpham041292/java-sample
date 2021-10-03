package asia.cmg.f8.gateway.security.apple;

import java.util.Map;

import asia.cmg.f8.gateway.security.auth.AbstractUserDetail;
import asia.cmg.f8.gateway.security.dto.UserInfo;
import asia.cmg.f8.gateway.security.dto.UserSignup;

public class AppleUserDetail extends AbstractUserDetail {
    private static final long serialVersionUID = 4517002802014296139L;

    public AppleUserDetail(final Map userInfo) {
        super(userInfo);
    }
}
