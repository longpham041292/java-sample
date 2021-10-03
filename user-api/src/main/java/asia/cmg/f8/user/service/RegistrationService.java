package asia.cmg.f8.user.service;

import java.util.Map;

import asia.cmg.f8.user.entity.UserEntity;

/**
 * Created on 2/17/17.
 */
public interface RegistrationService {

    /**
     * Register an {@link UserEntity} and return registered {@link UserEntity} if it's success.
     *
     * @param userEntity the user entity
     * @return registered user entity
     */
	Map<String, Object> register(UserEntity userEntity);
	Map<String, Object> handleExistAuthProviderID(UserEntity userEntity);
}
