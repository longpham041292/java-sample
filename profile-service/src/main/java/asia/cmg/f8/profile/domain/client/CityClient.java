package asia.cmg.f8.profile.domain.client;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.domain.entity.CityCollection;

/**
 * 
 * @author thach vo
 * @since September-20-2019
 */
@FeignClient(value = "cities", url = "${usergrid.baseUrl}")
public interface CityClient {

	String SECRECT_QUERY = "client_id=${userprofile.userGridClientId}&client_secret=${userprofile.userGridClientSecret}";
    String QUERY = "query";
    String LIMIT = "limit";
    
    @RequestMapping(value = "/cities?limit={limit}&ql={query}&" + SECRECT_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<CityCollection> getCitiesByQuery(@PathVariable(QUERY) final String query, @PathVariable(LIMIT) final int limit);
    
    @RequestMapping(value = "/cities?ql={query}&"+ SECRECT_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<CityCollection> getCitiesByQuery(@PathVariable(QUERY) final String query);
}
