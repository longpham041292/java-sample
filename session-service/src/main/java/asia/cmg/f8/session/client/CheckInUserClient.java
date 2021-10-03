package asia.cmg.f8.session.client;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.session.dto.CheckInUser;


/**
 * 
 * @author Nguyen Pham
 * Created 17/06/2019
 */

@FeignClient(value = "checkinusers", url = "${feign.url}", fallback = CheckInUserClientFallback.class)
public interface CheckInUserClient {
    String SECRET_QUERY =
            "client_id=${userGrid.userGridClientId}&client_secret=${userGrid.userGridClientSecret}";
    String GET_CHECK_IN_USER = "/check_in_users?" + SECRET_QUERY + "&limit=${userGrid.maxCheckedInUser}&ql={query}";

    @RequestMapping(value = GET_CHECK_IN_USER, method = GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<CheckInUser> getAllUserCheckInClub(@PathVariable("query") final String query);
}
