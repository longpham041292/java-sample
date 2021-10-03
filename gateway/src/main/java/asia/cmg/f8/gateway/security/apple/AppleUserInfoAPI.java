package asia.cmg.f8.gateway.security.apple;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import asia.cmg.f8.gateway.security.dto.AppleVerifyResponse;
import feign.Headers;


@FeignClient(name = "appleSignIn", url = "https://appleid.apple.com")
public interface AppleUserInfoAPI {
	@RequestMapping(value = "/auth/token", method = RequestMethod.POST , produces = APPLICATION_JSON_VALUE)
	@Headers("Content-Type: application/x-www-form-urlencoded")
	AppleVerifyResponse authAppleID(@RequestParam("grant_type") final String grantType,
								@RequestParam("client_id") final String clientId,
								@RequestParam("client_secret") final String clientSecret,
								@RequestParam(name = "code", required = false, defaultValue = "") final String code,
								@RequestParam(name = "refresh_token", required = false, defaultValue = "") final String refreshToken);	
	
}