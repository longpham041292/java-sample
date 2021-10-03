package asia.cmg.f8.session.internal.service;

import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.session.client.UserClient;
import asia.cmg.f8.session.dto.Profile;
import asia.cmg.f8.session.dto.UserEntity;
import asia.cmg.f8.session.entity.BasicUserEntity;
import asia.cmg.f8.session.repository.BasicUserRepository;
import asia.cmg.f8.session.service.UserManagementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.Locale;
import java.util.Optional;

import static java.util.Collections.emptySet;
import static java.util.Objects.isNull;

/**
 * Created on 12/22/16.
 */
@Service
public class InternalUserManagementService implements UserManagementService {

    private final BasicUserRepository userRepository;
    private final UserClient userClient;

    public InternalUserManagementService(final BasicUserRepository userRepository, final UserClient userClient) {
        this.userRepository = userRepository;
        this.userClient = userClient;
    }

    @Transactional
    @Override
    public BasicUserEntity createIfNotExist(final String userUuid) {

        final Optional<BasicUserEntity> userEntity = userRepository.findOneByUuid(userUuid);
        if (userEntity.isPresent()) {
            return userEntity.get();
        }
        final UserEntity userGridEntity = findFromUserGrid(userUuid);

        final BasicUserEntity user = create(userGridEntity);

        return userRepository.save(user);
    }

    /**
     * Create new {@link BasicUserEntity} from given {@link UserEntity}.
     *
     * @param userEntity the user entity.
     * @return new created {@link BasicUserEntity}
     */
    private BasicUserEntity create(final UserEntity userEntity) {

        final BasicUserEntity basicUser = new BasicUserEntity();
        basicUser.setUuid(userEntity.getUuid());
        basicUser.setFullName(userEntity.getName());
        basicUser.setEmail(userEntity.getEmail());
        basicUser.setAvatar(userEntity.getPicture());
        basicUser.setUserName(userEntity.getUsername());
        basicUser.setActivated(userEntity.getActivated());
        basicUser.setUserType(getUserType(userEntity));

        final Profile profile = userEntity.getProfile();
        if (!isNull(profile)) {
            basicUser.setCity(profile.getCity());
            basicUser.setCountry(profile.getCountry());
            basicUser.setPhone(profile.getPhone());
        }
        return basicUser;
    }

    private String getUserType(final UserEntity entity) {
        final UserType userType = entity.getUserType();
        if (userType != null) {
            return userType.name().toLowerCase(Locale.US);
        }
        /**
         * This is supported not to happen but if it this happen then we stop processing of this message.
         */
        throw new ConstraintViolationException("User " + entity.getUuid() + " has not userType.", emptySet());
    }

    /**
     * Load user from user-grid with given user uuid.
     *
     * @param userUuid the user uuid
     * @return the {@link UserEntity}.
     */
    private UserEntity findFromUserGrid(final String userUuid) {

        final UserGridResponse<UserEntity> response = userClient.getUser(userUuid);
        if (isNull(response) || response.getEntities().isEmpty()) {
            throw new ConstraintViolationException("User " + userUuid + " is not found", emptySet());
        }
        return response.getEntities().get(0);
    }
}
