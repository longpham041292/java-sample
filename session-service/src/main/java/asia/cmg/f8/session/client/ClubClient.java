package asia.cmg.f8.session.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.session.dto.ClubDto;
import asia.cmg.f8.session.entity.ClubEntity;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@FeignClient(value = "clubClient", url = "${feign.url}")
public interface ClubClient {
	
	String SECRET_QUERY = "client_id=${userGrid.userGridClientId}&client_secret=${userGrid.userGridClientSecret}";
	
	@RequestMapping(value = "/clubs/{uuid}?" + SECRET_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	UserGridResponse<ClubEntity> getByUuid(@PathVariable(name = "uuid") final String uuid);
}
