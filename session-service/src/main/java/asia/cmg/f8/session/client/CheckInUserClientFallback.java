package asia.cmg.f8.session.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.session.dto.CheckInUser;

@Component
public class CheckInUserClientFallback implements CheckInUserClient {

	private static final Logger LOG = LoggerFactory.getLogger(CheckInUserClientFallback.class);
	@Override
	public UserGridResponse<CheckInUser> getAllUserCheckInClub(String query) {
		// TODO Auto-generated method stub
		LOG.warn("CheckInUserClientFallback getAllUserCheckInClub query: {}", query);
		return null;
	}

}
