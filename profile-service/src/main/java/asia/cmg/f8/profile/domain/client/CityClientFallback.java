package asia.cmg.f8.profile.domain.client;

import org.springframework.stereotype.Component;
import asia.cmg.f8.common.exception.UserGridException;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.domain.entity.CityCollection;

@Component
public class CityClientFallback implements CityClient {

	@Override
	public UserGridResponse<CityCollection> getCitiesByQuery(String query, int limit) {
		throw new UserGridException("ERROR_ON_QUERY_CITY", "Usergrid went wrong while query cities by query: " + query);
	}

	@Override
	public UserGridResponse<CityCollection> getCitiesByQuery(String query) {
		throw new UserGridException("ERROR_ON_QUERY_CITY", "Usergrid went wrong while query cities by query: " + query);
	}
}
