package asia.cmg.f8.notification.client;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import asia.cmg.f8.notification.dto.BasicUserInfo;
import asia.cmg.f8.notification.entity.PagedResponse;
import asia.cmg.f8.notification.entity.UserGridResponse;

/**
 * Created on 1/7/17.
 */
@FeignClient(name = "basicUsers", url = "${feign.url}", fallback = BasicUserInfoClientFallback.class)
public interface BasicUserInfoClient {

	String SECRET_QUERY = "client_id=${usergrid.clientId}&client_secret=${usergrid.clientSecret}";
	String LIMIT = "limit";
	String QUERY = "query";

	@RequestMapping(method = GET, path = "/users/{uuid}?ql=select uuid,name,username,userType,picture where activated=true&" + SECRET_QUERY, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	UserGridResponse<BasicUserInfo> findBasicUserInfo(@PathVariable("uuid") final String userUuid);

	@RequestMapping(method = GET, path = "/users?" + SECRET_QUERY
			+ "&ql={query}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	UserGridResponse<BasicUserInfo> findBasicUserInfoByQuery(@PathVariable("query") final String query);

	@RequestMapping(value = "/users?" + SECRET_QUERY
			+ "&limit={limit}&cursor={cursor}&ql={query}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	PagedResponse<BasicUserInfo> searchUsersWithCursor(@PathVariable(QUERY) final String query,
			@PathVariable(LIMIT) final int limit, @PathVariable("cursor") final String cursor);

}
