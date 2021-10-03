package asia.cmg.f8.commerce.client;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import asia.cmg.f8.commerce.dto.SuggestedTrainersDTO;

@FeignClient(value = "profiles", url = "${service.profileUrl}", fallback = ProfileClientFallback.class)
public interface ProfileClient {

	@RequestMapping(value = "/internal/v1/profile/user/{uuid}/suggested-trainers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	List<SuggestedTrainersDTO> getNearestTrainersByLocation(@PathVariable("uuid") String accountUuid,
			@RequestParam("latitude") Double latitude, @RequestParam("longitude") Double longitude,
			@RequestParam("page") int page, @RequestParam("size") int size);

	@RequestMapping(value = "/internal/v1/pti-match/users/{euUuid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	List<SuggestedTrainersDTO> findPtiMatch(@PathVariable("euUuid") String euUuid,
			@RequestParam("ptUuid") List<String> ptUuid, @RequestParam("size") int size);
}
