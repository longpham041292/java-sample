package asia.cmg.f8.session.client.cms;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import asia.cmg.f8.session.dto.cms.CMSClassCategoryBookingDTO;

@FeignClient(value = "CMS", url = "${cms.url}")
public interface CmsClient {

	static final String AUTHORIZATION_HEADER = "Authorization";
//	
//	@RequestMapping(value = "/v2/users/signin/email", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//	ResponseEntity<Object> cmsSignin(@RequestBody CMSAuthenticationDTO body);
//
//	@RequestMapping(method = RequestMethod.GET, value = "/etickets/{id}")
//	@Headers("Content-Type: application/json")
//	ResponseEntity<ETicketDto> getEticket(@PathVariable(name = "id", required = true) int id, @RequestHeader(name = AUTHORIZATION_HEADER, required = true) String bearerToken);
//	
//	@RequestMapping(method = RequestMethod.GET, value = "/studios/{studio-uuid}/schedule/sessions/class-id")
//	ResponseEntity<CMSClassBookingDTO> getClassBookingDetail(@PathVariable(name = "studio-uuid") String studioUuid,
//															@PathVariable(name = "class-id") Long classId,
//															@RequestHeader(name = AUTHORIZATION_HEADER) String bearerToken);
//	
//	@RequestMapping(value = "/v2/amenities", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//	ResponseEntity<Object> getAmenities();

	@RequestMapping(value = "/v2/categories/{category_id}/sessions/belong", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	CMSClassCategoryBookingDTO checkCategory(@PathVariable(name = "category_id") String category_id,
			@RequestParam(name = "session_ids") List<String> session_ids,
			@RequestHeader(name = AUTHORIZATION_HEADER) String bearerToken);

	@RequestMapping(value = "/v2/studios/{id}/schedule/sessions/{session_id}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	CMSClassCategoryBookingDTO getClassDetail(@PathVariable(name = "id") String id,
			@PathVariable(name = "session_id") Long session_id,
			@RequestHeader(name = AUTHORIZATION_HEADER) String bearerToken);

	@RequestMapping(value = "/v2/studios/{studio_uuid}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	CMSClassCategoryBookingDTO getClubDetail(@PathVariable(name = "studio_uuid") String studio_uuid,
			@RequestHeader(name = AUTHORIZATION_HEADER) String bearerToken);
}
