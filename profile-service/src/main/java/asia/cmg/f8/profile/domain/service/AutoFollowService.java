package asia.cmg.f8.profile.domain.service;
/**
 * Created on 26/10/17.
 */

import java.util.List;

import java.util.concurrent.TimeUnit;

import asia.cmg.f8.profile.domain.client.FollowUserClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.domain.client.UserClient;
import asia.cmg.f8.profile.domain.entity.UserEntity;

@Service
public class AutoFollowService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AutoFollowService.class);

	@Autowired
	private UserClient userClient;

	@Autowired
	private FollowUserClient followUserClient;
	
	private static final int DEFAULT_LIMIT = 2_000;

	public UserEntity updateKateAutoFollow(final String user_uuid, final String kate_uuid, final Account account) {
        return followUserClient.createFollowingConnection(user_uuid, kate_uuid);
	}

	public boolean updateAutofollowingByuserType(final String userType ,final String kate_uuid ,final  Account account) {

		try {

			LOGGER.info("kate uuid = {}", kate_uuid);
			final UserGridResponse<UserEntity> userResponse = userClient.searchUsersWithCursor("select *  where activated=true and userType = '" + userType + "' ", DEFAULT_LIMIT, null);

			if (null != userResponse && null != userResponse.getEntities()) {
				final List<UserEntity> entities = userResponse.getEntities();
					for (final UserEntity item : entities) {
						final String uuid = item.getUuid();
						final String name = item.getName();

						if(name != null && !(uuid.equals(kate_uuid))){
							LOGGER.info("Creating user uuid, {} , name, {}", uuid, name);
							followUserClient.createFollowingConnection(uuid, kate_uuid);
						}
					

						try {
							TimeUnit.MILLISECONDS.sleep(700L);
						} catch (final InterruptedException exception) {
							LOGGER.error(exception.getMessage(), exception);
						}
					}
				
			}

			return true;
		} catch (final Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			return false;
		}

	}



}