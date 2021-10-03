package asia.cmg.f8.user.service;

import asia.cmg.f8.user.entity.UserEntity;

import java.util.Optional;

/**
 * Created on 2/21/17.
 */
public interface UserManagementService {

    /**
     * A convenience method to verify where the email is activated.
     *
     * @param email the email
     * @return true if email is activated otherwise false.
     */
    default boolean isEmailActivated(final String email) {
        return findByQuery("select uuid,emailvalidated where email='" + email + "'").map(UserManagementService::isEmailValidated).orElse(false);
    }

    static boolean isEmailValidated(final UserEntity user) {
        final Boolean activated = user.getEmailvalidated();
        if (activated == null) {
            return true;
        }
        return activated;
    }

    default boolean isUserActivated(final String email) {
        return findByQuery("select uuid,activated where email='" + email + "'").map(UserManagementService::isActivated).orElse(false);
    }

    /**
     * Verify if user with given facebook id is activated.
     *
     * @param facebookId the facebook id
     * @return true if user's activated otherwise false.
     */
    default boolean isFacebookActivated(final String facebookId) {
        return findByQuery("select uuid,activated where facebook.id='" + facebookId + "'").map(UserManagementService::isActivated).orElse(false);
    }

    static boolean isActivated(final UserEntity user) {
        final Boolean activated = user.getActivated();
        if (activated == null) {
            return false;
        }
        return activated;
    }

    /**
     * Find user by username
     *
     * @param username the user name
     * @return an optional of {@link UserEntity}
     */
    default Optional<UserEntity> findByUserName(final String username) {
        return findByQuery("where username='" + username + "'");
    }

    /**
     * A convenience method to verify if an user with given username is existed.
     *
     * @param username the username
     * @return true if username is existed.
     */
    default boolean isUserNameExisted(final String username) {
        return findByQuery("select uuid where username='" + username + "'").isPresent();
    }

    /**
     * A convenience method to check if an email is existed.
     *
     * @param email the email
     * @return true if email is already existed.
     */
    default boolean isEmailExisted(final String email) {
        return findByQuery("select uuid where email='" + email + "'").isPresent();
    }
    
    /**
     * A convenience method to check if an user code is existed.
     *
     * @param usercode the usercode
     * @return true if usercode is already existed.
     */
    default boolean isUserCodeExisted(final String usercode) {
        return findByQuery("select uuid where usercode='" + usercode + "'").isPresent();
    }

    /**
     * Find user's email by given facebook id.
     *
     * @param facebookId the facebook id.
     * @return an optional of user's email
     */
    default Optional<String> findEmailByFacebookId(final String facebookId) {
        return findByQuery("select uuid, email where facebook.id='" + facebookId + "'").map(user -> Optional.ofNullable(user.getEmail())).orElse(Optional.empty());
    }

    /**
     * Find user by usercode
     *
     * @param username the user code
     * @return an optional of {@link UserEntity}
     */
    default Optional<UserEntity> findByUserCode(final String usercode) {
        return findByQuery("where usercode='" + usercode + "'");
    }
    
    /**
     * Find user by given query
     *
     * @param query the query
     * @return an optional of {@link UserEntity}
     */
    Optional<UserEntity> findByQuery(String query);
}
