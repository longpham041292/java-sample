package asia.cmg.f8.gateway.security.facebook;

import asia.cmg.f8.gateway.security.auth.AbstractUserDetail;

import java.util.Map;

/**
 * Created on 11/3/16.
 */
public class FacebookUserDetail extends AbstractUserDetail {

    private static final long serialVersionUID = 4517002802014296139L;

    public FacebookUserDetail(final Map userInfo) {
        super(userInfo);
    }
}
