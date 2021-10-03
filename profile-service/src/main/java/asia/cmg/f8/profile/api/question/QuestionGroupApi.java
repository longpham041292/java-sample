package asia.cmg.f8.profile.api.question;

import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.profile.domain.service.QuestionGroupService;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class QuestionGroupApi {

	private static final Logger LOG = LoggerFactory.getLogger(QuestionGroupApi.class);
	private Gson gson = new Gson();
	
	@Autowired
	private QuestionGroupService questionGroupService;
	
	@GetMapping(value = "/mobile/v1/question_groups", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity getAll() {
		try {
			return new ResponseEntity(questionGroupService.getAll(), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return new ResponseEntity(Collections.emptyList(), HttpStatus.OK);
		}
	}
}
