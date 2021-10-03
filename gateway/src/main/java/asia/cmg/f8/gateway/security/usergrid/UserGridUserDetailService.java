package asia.cmg.f8.gateway.security.usergrid;

import asia.cmg.f8.gateway.client.UserApiClient;
import asia.cmg.f8.gateway.security.api.UserDetail;
import asia.cmg.f8.gateway.security.api.UserDetailService;
import asia.cmg.f8.gateway.security.dto.AccountTokenDTO;
import asia.cmg.f8.gateway.security.exception.InValidAuthenticationException;
import asia.cmg.f8.gateway.security.exception.UserGridException;
import asia.cmg.f8.gateway.security.token.JwtTokenFactoryImpl;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Optional;

import static asia.cmg.f8.gateway.SecurityUtil.toBearer;

/**
 * Created on 12/27/16.
 */
public class UserGridUserDetailService implements UserDetailService {

    public static final Logger LOGGER = LoggerFactory.getLogger(UserGridUserDetailService.class);

    private static final String UNABLE_AUTHENTICATE_TEXT="Unable to authenticate due to expired access token";
    private final UserGridApi userGridClient;
    
    @Autowired
    private UserApiClient userApiClient;
    
    public UserGridUserDetailService(final UserGridApi userGridClient) {
        this.userGridClient = userGridClient;
    }

//    @Override
//    public Optional<UserDetail> findByAccessToken(final String token) {
//
//        try {
//            final UserGridResponse<Map<String, Object>> response = userDetailApi.currentUser(toBearer(token));
//
//            if (response == null || response.getEntities() == null || response.getEntities().isEmpty()) {
//
//                LOGGER.warn("User is not found with token \"{}\"", token);
//                return Optional.empty();
//            }
//            return Optional.of(new UserGridUserDetail(response.getEntities().iterator().next()));
//        } catch (final InValidAuthenticationException | FeignException e) {
//            LOGGER.error("Error on load user detail...", e);
//            return Optional.empty();
//        } catch (final UserGridException e) {
//            if(e.getMessage().equals(UNABLE_AUTHENTICATE_TEXT)) {
//                LOGGER.error("Error on load user detail...", e);
//                return Optional.empty();
//            }
//            throw e;
//        }
//
//    }
    
    @Override
    public Optional<UserDetail> findByAccessToken(final String token) {
        try {
        	AccountTokenDTO accountTokenDTO = userApiClient.getByAccessToken(token);
        	if(accountTokenDTO != null && accountTokenDTO.getExpiredAt() > System.currentTimeMillis()) {
        		String userUuid = accountTokenDTO.getUuid();
                final UserGridResponse<Map<String, Object>> response = userGridClient.getUserByUuid(userUuid);
                if (response == null || response.getEntities() == null || response.getEntities().isEmpty()) {
                    LOGGER.info("User uuid {} not found with token {}", userUuid, token);
                    return Optional.empty();
                }
                return Optional.of(new UserGridUserDetail(response.getEntities().iterator().next()));
        	} else {
        		LOGGER.info("Token {} not found in database", token);
        		return Optional.empty();
			}
        } catch (final Exception e) {
        	LOGGER.info(e.getMessage());
            return Optional.empty();
        }
    }
}
