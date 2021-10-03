package asia.cmg.f8.profile.api.profile;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.common.spec.user.DocumentStatusType;
import asia.cmg.f8.profile.domain.repository.UserPTiMatchRepository;
import asia.cmg.f8.profile.dto.SuggestedTrainersDTO;

@RestController
public class InternalProfileApi {

	@Autowired
	private UserPTiMatchRepository userPTiMatchRepo;

	/*
	 * Get list Pt iMatch but exclude from list ptUuid
	 * @Author Long
	 */
	@GetMapping(value = "/internal/v1/pti-match/users/{euUuid}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> findPtiMatch(@PathVariable("euUuid") String euUuid,
			@RequestParam("ptUuid") List<String> ptUuid, @RequestParam("size") int size) {
		List<SuggestedTrainersDTO> result = new ArrayList<SuggestedTrainersDTO>();
		try {
			Pageable pageable = new PageRequest(0, size);
			result = userPTiMatchRepo.findPtiMatch(euUuid, ptUuid, DocumentStatusType.APPROVED ,pageable);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<Object>(result, HttpStatus.OK);
	}
}
