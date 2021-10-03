package asia.cmg.f8.profile.domain.client;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.domain.entity.DistrictCollection;

/**
 * 
 * @author thach vo
 * @since September-18-2019
 */
@FeignClient(value = "districts", url = "${usergrid.baseUrl}")
public interface DistrictClient {
	String SECRECT_QUERY = "client_id=${userprofile.userGridClientId}&client_secret=${userprofile.userGridClientSecret}";
    String QUERY = "query";
    String LIMIT = "limit";
    
    @RequestMapping(value = "/districts?limit={limit}&ql={query}&" + SECRECT_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<DistrictCollection> getDistrictsByQuery( @PathVariable(QUERY) final String query, @PathVariable(LIMIT) final int limit);
    
    @RequestMapping(value = "/districts?ql={query}&"+ SECRECT_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<DistrictCollection> getDistrictsByQuery(@PathVariable(QUERY) final String query);
}
