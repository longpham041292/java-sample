package asia.cmg.f8.session.service;

import asia.cmg.f8.session.entity.BasicUserEntity;

/**
 * Created on 12/21/16.
 */
public interface UserManagementService {

    /**
     * Create new user from given uuid.
     *
     * @param userUuid the uuid
     * @return new created {@link BasicUserEntity} or an existing user with given user uuid.
     */
    BasicUserEntity createIfNotExist(final String userUuid);
}
