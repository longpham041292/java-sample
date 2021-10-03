/**
 * 
 */
package asia.cmg.f8.profile.domain.client;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import asia.cmg.f8.common.dto.ApiRespListObject;
import asia.cmg.f8.profile.domain.entity.PagedUserResponse;
import asia.cmg.f8.profile.dto.SuggestedTrainerResponse;
import asia.cmg.f8.profile.dto.SuggestedTrainersDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import asia.cmg.f8.profile.domain.entity.FreeOrderRequest;

/**
 * @author khoa.bui
 *
 */
@FeignClient(value = "commerce", url = "${feign.commerceUrl}")
public interface CommerceClient {

	@RequestMapping(value = "/orders/freeForMigrationUser", method = RequestMethod.POST, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	public Integer createFreeOrderMigrationUsers(@RequestBody @Valid final List<FreeOrderRequest> orders);

	@RequestMapping(value = "/mobile/v1/wallets/credits/suggested-trainers", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	SuggestedTrainerResponse suggestTrainers(@RequestParam("latitude") double latitude,
						@RequestParam("longitude") double longitude,
						@RequestParam("page") int page,
						@RequestParam("per_page") int perPage,
						@RequestHeader("Authorization") String token);
}
