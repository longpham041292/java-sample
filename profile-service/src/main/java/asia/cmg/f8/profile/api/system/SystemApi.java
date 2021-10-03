package asia.cmg.f8.profile.api.system;

import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.profile.api.question.AnsweredResponse;
import asia.cmg.f8.profile.api.question.QuestionResponse;
import asia.cmg.f8.profile.domain.entity.AnswerEntity;
import asia.cmg.f8.profile.domain.entity.QuestionEntity;
import asia.cmg.f8.profile.domain.service.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created on 1/10/17.
 */
@RestController
public class SystemApi {
    private static final Logger LOG = LoggerFactory.getLogger(SystemApi.class);
    private final ProfileService profileService;

    public SystemApi(final ProfileService profileService) {
        this.profileService = profileService;
    }

    @RequestMapping(value = "/system/questions", method = RequestMethod.GET,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getQuestions(
            @RequestParam("user_id") final String userId) {
        LOG.info("Retrieving question answer of user {}", userId);
        //Get answers of user
        final List<AnswerEntity> answers = profileService.getAnswersBySystem(userId);
        if (answers.isEmpty()) {
            LOG.info("Could not found any answer of user {}", userId);
            return new ResponseEntity<>(ErrorCode.REQUEST_INVALID, HttpStatus.BAD_REQUEST);
        }

        final Set<String> questionUuid = answers.stream()
                .map(AnswerEntity::getQuestionId)
                .collect(Collectors.toSet());

        //Get all questions are shown by admin that user answered already 
        final List<QuestionEntity> questions = profileService
                .getQuestionsBySystem(questionUuid);

        final Map<String, AnsweredResponse> answerResp = answers
                .stream().collect(Collectors.toMap(
                        AnswerEntity::getQuestionId,
                        AnsweredResponse::new));

        return new ResponseEntity<>(questions.stream().map(question -> {

            final String questionKey = question.getKey();
            final QuestionResponse result = new QuestionResponse(question);

            result.setAnswered(true);
            result.getOptions().stream().forEach(option -> {
                final AnsweredResponse answer = answerResp.get(questionKey);
                if (answer.getOptionKeys().contains(option.getKey())) {
                    option.setChoose(true);
                }
            });
            return result;
        }).collect(Collectors.toList()), HttpStatus.OK);
    }
}