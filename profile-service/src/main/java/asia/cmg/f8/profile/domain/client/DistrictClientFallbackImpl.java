package asia.cmg.f8.profile.domain.client;

import org.springframework.stereotype.Component;

import asia.cmg.f8.common.exception.UserGridException;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.domain.entity.DistrictCollection;

@Component
public class DistrictClientFallbackImpl implements DistrictClient {

	@Override
	public UserGridResponse<DistrictCollection> getDistrictsByQuery(String query, int limit) {
		throw new UserGridException("ERROR_ON_QUERY_DISTRICT", "Usergrid went wrong while query districts data by query: " + query);
	}

	@Override
	public UserGridResponse<DistrictCollection> getDistrictsByQuery( String query) {
		throw new UserGridException("ERROR_ON_QUERY_DISTRICT", "Usergrid went wrong while query districts data by query: " + query);
	}
}
