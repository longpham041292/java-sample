package asia.cmg.f8.commerce.client;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import asia.cmg.f8.commerce.dto.RecentPartnerDTO;

@Component
public class SessionClientFallback implements SessionClient {
	
	private static final Logger LOG = LoggerFactory.getLogger(SessionClientFallback.class);
	
    @Override
    public Map<String, Boolean> checkValidContractWithTrainer(final String userUuid,
            final String trainerUuid) {
        return null;
    }

	@Override
	public void cancelInvalidSessions(String orderUuid, String expiredDate) {
		LOG.error("cancelInvalidSessions has error {0} {1}", orderUuid, expiredDate);
	}

	@Override
	public List<RecentPartnerDTO> getRecentBookedTrainers(String euUuid, int page, int size) {
		return Collections.emptyList();
	}

}
