package asia.cmg.f8.profile.api.question;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.profile.domain.service.CompleteProfileCommand;
import asia.cmg.f8.profile.domain.service.ProfileService;
import asia.cmg.f8.profile.domain.service.SubmitAnswerCommand;
import asia.cmg.f8.profile.domain.service.UserProfileService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.OperationNotSupportedException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author tung.nguyenthanh
 */
@RestController
public class AnswerApi {

    private static final String PROFILE_MANGEMENT = "Profile Management";
    private static final String SUCCESS = "success";

    private final ProfileService profileService;
    
    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    public AnswerApi(final ProfileService profileService) {
        this.profileService = profileService;
    }

    @ApiOperation(value = "Finish Onboard Screen", tags = PROFILE_MANGEMENT)
    @RequestMapping(value = "/finish", method = RequestMethod.POST)
    public Map<String, Boolean> finishAnswer(final Account account) {
        return profileService.process(new CompleteProfileCommand(account));
    }


    @ApiOperation(value = "Submit answer for a question", tags = "Profile Management")
    @RequestMapping(value = "/answers", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Map<String, Boolean> submitAnswer(@RequestBody final SubmitAnswerRequest request, final Account account) {
        final SubmitAnswerCommand command = new SubmitAnswerCommand(account,
                request.getQuestionId(), request.getOptionKeys());
        profileService.process(command);

        return Collections.singletonMap(SUCCESS, Boolean.TRUE);
    }
    
    @ApiOperation(value = "Submit answer for a question", tags = "Profile Management")
    @RequestMapping(value = "/mobile/v1/answers/last_question_sequence/{lastSequence}", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Map<String, Boolean> submitAnswers(@RequestBody final List<SubmitAnswerRequest> request,
											@PathVariable(name = "lastSequence") final int lastSequence,
											final Account account) {
    	
    	request.forEach(answer -> {
    		final SubmitAnswerCommand command = new SubmitAnswerCommand(account, answer.getQuestionId(), answer.getOptionKeys());
            profileService.process(command);
    	});
    	
    	userProfileService.updateLastQuestionSequence(account.uuid(), lastSequence);
        
        return Collections.singletonMap(SUCCESS, Boolean.TRUE);
    }

    @ApiOperation(value = "Delete all answers", tags = PROFILE_MANGEMENT)
    @RequestMapping(value = "/answers", method = RequestMethod.DELETE, produces = APPLICATION_JSON_VALUE)
    public Map<String, Boolean> deleteAllAnswers(final Account account) throws OperationNotSupportedException {
        if (UserType.EU.name().equalsIgnoreCase(account.type()) || UserType.PT.name().equalsIgnoreCase(account.type())) {
            profileService.deleteAllUserAnswers(account);
            return Collections.singletonMap(SUCCESS, Boolean.TRUE);
        }
        throw new OperationNotSupportedException("This operation is not found");
    }
}
