package asia.cmg.f8.gateway.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import asia.cmg.f8.gateway.security.dto.AccountTokenDTO;

@FeignClient(name = "userApiClient", url = "${feign.userApiUrl}", fallback = UserApiClientFallbackImpl.class)
public interface UserApiClient {

	@RequestMapping(value = "/users/{uuid}/token", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	AccountTokenDTO getUserToken(@PathVariable(name = "uuid") final String userUuid);
	
	@RequestMapping(value = "/users/token", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	AccountTokenDTO getByAccessToken(@RequestParam(name = "leep_access_token") final String accessToken);
	
	@RequestMapping(value = "/users/token", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	AccountTokenDTO saveUserToken(@RequestBody AccountTokenDTO accountTokenDTO);
	
	@RequestMapping(value = "/users/token/refresh-token", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	AccountTokenDTO getByAccessTokenAndRefreshToken(@RequestParam(name = "leep_access_token") final String accessToken,
													@RequestParam(name = "leep_refresh_token") final String refreshToken);
	
}
