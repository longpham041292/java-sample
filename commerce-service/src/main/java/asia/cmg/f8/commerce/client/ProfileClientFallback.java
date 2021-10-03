package asia.cmg.f8.commerce.client;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import asia.cmg.f8.commerce.dto.SuggestedTrainersDTO;

@Component
public class ProfileClientFallback implements ProfileClient {

	private static final Logger LOG = LoggerFactory.getLogger(ProfileClientFallback.class);

	@Override
	public List<SuggestedTrainersDTO> getNearestTrainersByLocation(String accountUuid, Double latitude,
																Double longitude, int page, int size) {
		LOG.error("Error when calling getNearestTrainersByLocation: accountUuid:{} latitude:{} longitude:{}", accountUuid, latitude, longitude);
		return Collections.emptyList();
	}

	@Override
	public List<SuggestedTrainersDTO> findPtiMatch(String euUuid, List<String> ptUuid, int size) {
		LOG.error("Error when calling findPtiMatch: accountUuid:{} ", euUuid);
		return Collections.emptyList();
	}
}
