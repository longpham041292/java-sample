package asia.cmg.f8.user.dto;

import asia.cmg.f8.common.spec.user.UserType;

/**
 * Re-present a required information to link user.
 * <p>
 * Created on 1/23/17.
 */
public class LinkUser {

    private final String uuid;
    private final UserType userType;

    public LinkUser(final String uuid, final UserType userType) {
        this.uuid = uuid;
        this.userType = userType;
    }

    public String getUuid() {
        return uuid;
    }

    public final boolean isPt() {
        return UserType.PT.equals(userType);
    }
}
