/**
 * 
 */
package asia.cmg.f8.user.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import asia.cmg.f8.user.entity.UserEntity;
import asia.cmg.f8.user.client.UserProfileClient;
import asia.cmg.f8.user.config.UserProperties;
import asia.cmg.f8.user.entity.PagedUserResponse;

/**
 * @author khoa.bui
 *
 */
@Service
public class UpdateDefaultProfileUserImageService implements ApplicationRunner {
	private static final Logger LOG = LoggerFactory.getLogger(UpdateDefaultProfileUserImageService.class);
	private static final int DEFAULT_LIMIT = 1_000;
	private static final String QUERY = "select uuid where picture contains '*www.gravatar.com*'";

	@Autowired
	private UserProperties userProperties;

	@Autowired
	private UserProfileClient userProfileClient;
	private boolean isFinished = false;
    private int trying = 1;

	public void processUpdatingDefaultImage() {
		try {
			String cursor = null;
			PagedUserResponse<UserEntity> response = userProfileClient.getOldDefaultImageUser(QUERY,
					DEFAULT_LIMIT);
			do {

				if (null != response) {
					if (null != response.getEntities()) {
						final List<UserEntity> entities = response.getEntities();
						for (final UserEntity item : entities) {
							final UserEntity newUser = UserEntity.builder()
									.picture(userProperties.getDefaultProfilePicture()).build();
							// this will wait until the result come then process
							// the next item.
							userProfileClient.updateOldAvartaImage(item.getUuid(), newUser);
							LOG.info("Update default image user {}", item.getUuid());

							// don't stress the user-grid so sleep a little
							// bit??
							try {
								TimeUnit.MILLISECONDS.sleep(200L);
							} catch (final InterruptedException exception) {
								// just leave it here
							}
						}
					}

					cursor = response.getCursor();
					LOG.info("Fetching users with old default image pageSize {} cursor {}", DEFAULT_LIMIT, cursor);
					if (null != cursor) {
						response = userProfileClient.getOldDefaultImageUserWithCursor(QUERY,
								DEFAULT_LIMIT, cursor);
					}

				}
			} while (null != cursor);
			//Finished the update job. shouldn't work anymore
			isFinished = true;
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	@Override
	public void run(final ApplicationArguments args) {
		try {
			TimeUnit.MILLISECONDS.sleep(2000L);
		} catch (final InterruptedException exception) {
			// just leave it here
		}
		do {
//		if (userProperties.isAllowUpdatePictureServiceRunning()) {
			LOG.info("--------------Calling update default avarta image .");
			//waiting until service bind successfully
			processUpdatingDefaultImage();
			trying += 1;
		//	If exception occurs during running, trying 5 times only
		} while(!isFinished && trying <= 5); 
//		}
		
	}
}
