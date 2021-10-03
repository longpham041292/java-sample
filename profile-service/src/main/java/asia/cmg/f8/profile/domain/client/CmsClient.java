package asia.cmg.f8.profile.domain.client;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "CMS", url = "${feign.cmsUrl}")
public interface CmsClient {

	static final String AUTHORIZATION_HEADER = "Authorization";
	static final String LANGUAGE_HEADER = "Accept-Language";

	@RequestMapping(value = "/v2/studios/nearby", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	Map<String, Object> nearbyClubs(@RequestParam(name = "longitude") double longitude,
			@RequestParam(name = "latitude") double latitude,
			@RequestParam(name = "page") int page,
			@RequestParam(name = "per_page") int perPage,
			@RequestHeader(name = AUTHORIZATION_HEADER) String bearerToken,
			@RequestHeader(name = LANGUAGE_HEADER) String langage);
}
