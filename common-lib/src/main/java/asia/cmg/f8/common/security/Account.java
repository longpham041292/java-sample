package asia.cmg.f8.common.security;

import static asia.cmg.f8.common.util.CommonConstant.EU_USER_TYPE;
import static asia.cmg.f8.common.util.CommonConstant.PT_USER_TYPE;
import static asia.cmg.f8.common.util.CommonConstant.CLUB_USER_TYPE;

/**
 * Represent information of current logged-in account.
 * Created on 11/1/16.
 */
public interface Account {

    /**
     * Account's uuid.
     *
     * @return uuid.
     */
    String uuid();

    /**
     * An UserGrid access token which is granted to current logged-in account.
     *
     * @return the access token.
     */
    String ugAccessToken();

    /**
     * @return account's type.
     */
    String type();

    /**
     * @return account's language
     */
    String language();

    /**
     * Whether the current account is PT (Personal Trainer) or not.
     *
     * @return true if the current account is PT
     */
    default boolean isPt() {
        return PT_USER_TYPE.equalsIgnoreCase(type());
    }

    /**
     * Whether the current account is EU (End User) or not.
     *
     * @return true if the current {@link Account} is
     */
    default boolean isEu() {
        return EU_USER_TYPE.equalsIgnoreCase(type());
    }
    
    /**
     * Whether the current account is CLUB (Club User) or not.
     *
     * @return true if the current {@link Account} is
     */
    default boolean isClub() {
    	return CLUB_USER_TYPE.equalsIgnoreCase(type());
    }

    /**
     * Whether the current account is in admin role.
     *
     * @return true if current account is in admin role, otherwise it returns false.
     */
    boolean isAdmin();
}
