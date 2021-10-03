package asia.cmg.f8.gateway.security.usergrid;

import asia.cmg.f8.gateway.security.auth.AbstractUserDetail;

import java.util.Map;

/**
 * Created on 11/3/16.
 */
public class UserGridUserDetail extends AbstractUserDetail {

    public UserGridUserDetail(final Map userInfo) {
        super(userInfo);
    }
}
