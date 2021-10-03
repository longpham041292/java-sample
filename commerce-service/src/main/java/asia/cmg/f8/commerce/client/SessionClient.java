package asia.cmg.f8.commerce.client;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import asia.cmg.f8.commerce.dto.RecentPartnerDTO;

@FeignClient(value = "sessions", url = "${service.sessionUrl}", fallback = SessionClientFallback.class)
public interface SessionClient {

	@RequestMapping(value = "/sessions/users/{user_uuid}/contracting/trainers/{trainer_uuid}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	Map<String, Boolean> checkValidContractWithTrainer(@PathVariable("user_uuid") final String userUuid,
			@PathVariable("trainer_uuid") final String trainerUuid);

	@RequestMapping(value = "/internal/orders/{orderUuid}/cancel-invalid-sessions/?expired_date={expiredDate}", method = RequestMethod.PUT, consumes = "application/json;charset=UTF-8")
	void cancelInvalidSessions(@PathVariable("orderUuid") final String orderUuid,
			@PathVariable("expiredDate") final String expiredDate);
	
	@RequestMapping(value = "/internal/v1/credit/booking/sessions/user/{euUuid}/recent-trainers", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	List<RecentPartnerDTO> getRecentBookedTrainers(@PathVariable("euUuid") String euUuid, 
													@RequestParam("page") int page, 
													@RequestParam("size") int size);
}
