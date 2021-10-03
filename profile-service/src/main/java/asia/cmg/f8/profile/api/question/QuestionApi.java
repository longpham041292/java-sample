package asia.cmg.f8.profile.api.question;

import asia.cmg.f8.common.dto.QuestionDTO;
import asia.cmg.f8.common.dto.QuestionOptionDTO;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.profile.domain.entity.AnswerEntity;
import asia.cmg.f8.profile.domain.entity.Option;
import asia.cmg.f8.profile.domain.repository.QuestionRepository;
import asia.cmg.f8.profile.domain.service.ProfileService;
import asia.cmg.f8.profile.domain.service.QuestionQuery;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import com.google.gson.Gson;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created on 10/17/16.
 */
@RestController
public class QuestionApi {

	private static final Logger LOG = LoggerFactory.getLogger(QuestionApi.class);
	private Gson gson = new Gson();
	private static final String SUCCESS = "success";
    private static final String QUEST_BY_KEY_QUERY = "select * where key = '%s'";
    private static final String QUESTION_DISTRICT_KEY = "District";
    private final ProfileService profileService;
    
    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    public QuestionApi(final ProfileService profileService) {
        this.profileService = profileService;
    }

    @RequestMapping(value = "/internal/v1/questions/user_type/{user_type}/language/{language}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public List<QuestionDTO> getQuestionsByUserTypeAndLanguage(@PathVariable("user_type") final String userType,
																	@PathVariable("language") final String language) {
    	try {
    		List<asia.cmg.f8.profile.database.entity.QuestionEntity> questionEntities = questionRepository.findByUserTypeAndLanguage(userType, language);
        	List<QuestionDTO> questionDTOs = questionEntities.stream().map(questionEntity -> {
        		
        		List<QuestionOptionDTO> optionsDTO = questionEntity.getOptions().stream().map(optionEntity -> {
        			return QuestionOptionDTO.builder()
    										.setFiltered(optionEntity.filtered)
    										.setIconUrl(optionEntity.iconUrl)
    										.setId(optionEntity.id)
    										.setKey(optionEntity.key)
    										.setSequence(optionEntity.sequence)
    										.setText(optionEntity.text)
    										.setType(optionEntity.type)
    										.setWeight(optionEntity.weight)
        									.build();
        		}).collect(Collectors.toList());
        		
        		return QuestionDTO.builder().setDescription(questionEntity.description)
        									.setFiltered(questionEntity.filtered)
        									.setHide(questionEntity.hide)
        									.setId(questionEntity.id)
        									.setKey(questionEntity.key)
        									.setLanguage(questionEntity.language)
        									.setLimitedUserSelection(questionEntity.limitedUserSelection)
        									.setOptions(optionsDTO)
        									.setQuestionType(questionEntity.questionType.name())
        									.setRequired(questionEntity.required)
        									.setSequence(questionEntity.sequence)
        									.setTags(questionEntity.tags)
        									.setTitle(questionEntity.title)
        									.setUsedFor(questionEntity.usedFor)
        									.setWeight(questionEntity.weight)
        									.build();
        	}).collect(Collectors.toList());
        	
        	return questionDTOs;
		} catch (Exception e) {
			return Collections.emptyList();
		}
    }
    
    @ApiOperation(value = "Get list of on-boarding profile questions", tags = "Profile Management")
    @RequestMapping(value = "/questions/{language}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public GetQuestionsResponse<QuestionResponse> getQuestions(@PathVariable("language") final String language,
															@RequestParam(name = "lastSequence", required = false) final Integer lastSequence,
															final Account account) {
    	
    	List<asia.cmg.f8.profile.database.entity.QuestionEntity> questions = new ArrayList<asia.cmg.f8.profile.database.entity.QuestionEntity>();
    	
    	if(lastSequence == null) {
    		questions = questionRepository.findByUserTypeAndLanguage(account.type(), language);
    	} else {
			questions = questionRepository.findByUserTypeAndLanguageAndSequence(account.type(), language, lastSequence);
		}
    	
        final List<AnswerEntity> answers = profileService.getAnswersByUser(account);
        final Map<String, AnsweredResponse> answerResp = new HashMap<>();
        answers.stream().forEach(answer -> answerResp.put(answer.getQuestionId(), new AnsweredResponse(answer)));

        return new GetQuestionsResponse<>(
        		questions.stream().map(question -> {
											            final QuestionResponse result = new QuestionResponse(question);
											            final AnsweredResponse answer = answerResp.get(question.key);
											            
											            if (answerResp.containsKey(question.key)) {
											            	if(QUESTION_DISTRICT_KEY.equalsIgnoreCase(question.key)) {
											            		List<OptionResponse> optionResponseList = new ArrayList<OptionResponse>();
												                answer.getOptionKeys().forEach(anwserKey -> {
												                	Option option = new Option();
												                	option.setKey(anwserKey);
												                	OptionResponse optionResponse = new OptionResponse(option);
												                	optionResponse.setChoose(Boolean.TRUE);		
												                	optionResponseList.add(optionResponse);
											                	});
												                result.setOptions(optionResponseList);
											            	} else {
												                result.setAnswered(true);
												                result.getOptions().stream().forEach(option -> {
												                    if (answer.getOptionKeys().contains(option.getKey())) {
												                        option.setChoose(true);
												                    }
												                });
											            	}
											            }
											            return result;
						            				})
        						.collect(Collectors.toList()));
    }

    @ApiOperation(value = "Admin manage on-boarding questions", tags = "Admin Management")
    @RequestMapping(value = "/admin/questions/{language}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @RequiredAdminRole
    public GetQuestionsResponse<QuestionManageResponse> getQuestionsByAdmin(
            @PathVariable("language") final String language, final Account account) {
    	
        final List<asia.cmg.f8.profile.database.entity.QuestionEntity> questions = profileService.getQuestionsByAdmin(account,
                new QuestionQuery(UserType.PT.name().toLowerCase(), language));

        return new GetQuestionsResponse<>(questions.stream()
                .map(QuestionManageResponse::new).collect(Collectors.toList()));
    }

    @RequestMapping(value = "/admin/questions/status", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @RequiredAdminRole
    public Map<String, Boolean> updateQuestionStatus(@RequestBody final Map<String, Object> body, final Account account) {
    	try {
    		final String questionKey = (String) body.get("key");
            final boolean status = (boolean) body.get("hide");

            questionRepository.updateQuestionStatusByKey(status, questionKey);
            
            return Collections.singletonMap(SUCCESS, Boolean.TRUE);
		} catch (Exception e) {
			LOG.error("[updateQuestionStatus][Error: {}]", e.getMessage());
			return Collections.singletonMap(SUCCESS, Boolean.FALSE);
		}
    }
    
    @ApiOperation(value = "Admin update questions weight", tags = "Admin Management")
    @RequestMapping(value = "/admin/questions/weight", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    @RequiredAdminRole
	public Map<String, Boolean> updateQuestionsWeight(@RequestBody final Map<String, Object> body, final Account account) {
		try {
			final String questionKey = (String) body.get("key");
			final Integer weight = (int) body.get("weight");

			LOG.info("--- >>> -- 1  {}", questionKey);
			LOG.info("--- >>> -- 2  {}", weight);

			questionRepository.updateQuestionWeightByKey(weight, questionKey);
		} catch (Exception e) {
			LOG.error("[updateQuestionsWeight][Error: {}]", e.getMessage());
			return Collections.singletonMap(SUCCESS, Boolean.FALSE);
		}

		return Collections.singletonMap(SUCCESS, Boolean.TRUE);
	}
 
    /**
     * Closed this function because of stopping using Question collection on Usergrid
     * By: Thach Vo
     * Date: 25-12-2019
     * @param body
     * @param account
     * @return
     */
//    @RequiredAdminRole
//    @RequestMapping(value = "/admin/questions", method = RequestMethod.PUT, produces = APPLICATION_JSON_VALUE)
//    public Map<String, Boolean> updateQuestion(@RequestBody @Validated final QuestionEntity body, final Account account) {
//    	    	
//    	LOG.info("Logging input data for updateQuestion: " + gson.toJson(body));
//    	
//    	questionClient.updateQuesttion(body.getUuid(), body);
//    	
//    	return Collections.singletonMap(SUCCESS, Boolean.TRUE);
//    }
}
