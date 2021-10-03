package asia.cmg.f8.profile.domain.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import asia.cmg.f8.common.profile.CompleteProfileEvent;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.AnswerSubmittedEvent;
import asia.cmg.f8.profile.OptionRecord;
import asia.cmg.f8.profile.config.UserProfileProperties;
import asia.cmg.f8.profile.database.entity.BasicUserEntity;
import asia.cmg.f8.profile.database.entity.DistrictEntity;
import asia.cmg.f8.profile.database.entity.UserDistrictLocationEntity;
import asia.cmg.f8.profile.domain.client.AnswerClient;
import asia.cmg.f8.profile.domain.client.ModifyAnswerClient;
import asia.cmg.f8.profile.domain.client.QuestionClient;
import asia.cmg.f8.profile.domain.client.UserClient;
import asia.cmg.f8.profile.domain.entity.AnswerEntity;
import asia.cmg.f8.profile.domain.entity.QuestionEntity;
import asia.cmg.f8.profile.domain.entity.QuestionType;
import asia.cmg.f8.profile.domain.event.EventHandler;
import asia.cmg.f8.profile.domain.repository.BasicUserEntityRepository;
import asia.cmg.f8.profile.domain.repository.DistrictRepository;
import asia.cmg.f8.profile.domain.repository.QuestionRepository;
import asia.cmg.f8.profile.exception.QuestionAnswerValidateException;

/**
 * Created by tri.bui on 10/25/16.
 */
@Component
public class ProfileService {

    private static final Logger LOG = LoggerFactory.getLogger(ProfileService.class);
    private static final String DEFAULT_LANGUAGE = "en";
    private static final String GET_QUESTIONS_LANGUAGE = "select * where language='%s' and usedFor='%s' and hide=false order by sequence asc";
    private static final String GET_QUESTIONS_LANGUAGE_ADMIN = "select * where language='%s' and usedFor='%s' and (not questionType contains 'guide') order by sequence asc";
    private static final String GET_REQUIRED_QUESTIONS_LANGUAGE = "select * where required=true and hide=false and language='%s' and usedFor='%s'";
    private static final String GET_QUESTION = "select * where key='%s' and usedFor='%s' and hide=false";

	private static final String GET_ANSWER_BY_USER_QUESTION = "select * where owner='%s' and questionId='%s' and (not deleted = true)";
	private static final String USER_ANSWERED_QUERY = "select * where owner='%s' and (not deleted = true)";

	private static final String GET_QUESTIONS_BY_UUID = "select * where hide=false and ( %s ) order by sequence asc";

	private static final String SUCCESS = "success";
	
	private final String QUESTION_DISTRICT_KEY = "District";
  

    @Autowired
    private EventHandler eventHandler;

    @Autowired
    private QuestionClient questionClient;

    @Autowired
    private AnswerClient answerClient;

    @Autowired
    private ModifyAnswerClient modifyAnswerClient;

    @Autowired
    private UserProfileProperties profileProperties;

    @Autowired
    private UserClient userClient;
    
    @Autowired
    private QuestionRepository questionRepo;
    
    @Autowired
    private DistrictRepository districtRepo;
    
    @Autowired
    private UserDistrictLocationService userLocationService;
    
    @Autowired
    private BasicUserEntityRepository basicUserRepo;

    public void process(final SubmitAnswerCommand command) {
        // validate the question
        final List<asia.cmg.f8.profile.database.entity.QuestionEntity> questionResp = questionRepo.findByKeyAndUsedForAndHideIsFalse(command.getQuestionId(), command.getAccount().type());
        if (questionResp.isEmpty()) {
            throw new QuestionAnswerValidateException("Question invalid", "Question is not found");
        }
        
        final asia.cmg.f8.profile.database.entity.QuestionEntity question = questionResp.get(0);
        
        if (QuestionType.SINGLE.compareTo(question.questionType) == 0
                && command.getOptionKeys().size() > 1) {
            throw new QuestionAnswerValidateException("Question invalid",
                    "Multi option for single option question");
        }
        
        final List<String> options = question.getOptions().stream()
                					.map(option -> option.key).collect(Collectors.toList());

        // Excluding District Onboarding Question
        if(!command.getQuestionId().equalsIgnoreCase(QUESTION_DISTRICT_KEY)) {
        	command.getOptionKeys()
            .forEach(
                    option -> {
                        if (!options.contains(option)) {
                            throw new QuestionAnswerValidateException("Question invalid",
                                    "Question option is invalid");
                        }
                    });
        }

        // check whether user create or update the question
        final Account account = command.getAccount();
        final UserGridResponse<AnswerEntity> answers = answerClient
                .getAnswersByUser( String.format(
                        GET_ANSWER_BY_USER_QUESTION, account.uuid(), command.getQuestionId()),
                        profileProperties.getQuestionaire().getAnswerLimit());

        String answerId = "";
        if (!answers.getEntities().isEmpty()) {
            answerId = answers.getEntities().get(0).getUuid();
        }

        List<OptionRecord> optionRecords = new ArrayList<OptionRecord>();
        command.getOptionKeys().forEach(option -> {
        	OptionRecord optionRecord = OptionRecord.newBuilder().setOption(option).setChoose(Boolean.TRUE).build();
        	optionRecords.add(optionRecord);
        });
        
//      Handle for storing user district location for District question
        if(command.getQuestionId().equalsIgnoreCase(QUESTION_DISTRICT_KEY)) {
        	try {
            	List<UserDistrictLocationEntity> entities = new ArrayList<UserDistrictLocationEntity>();
              	optionRecords.forEach(option -> {
                  	List<DistrictEntity> districts = districtRepo.findByKeyAndLanguage(option.getOption().toString(), "en");
                  	if(!districts.isEmpty()) {
                  		try {
                  			DistrictEntity district = districts.get(0);
                      		UserDistrictLocationEntity userLocation = new UserDistrictLocationEntity();
                      		userLocation.setCityKey(district.getCityKey());
                      		userLocation.setDistrictKey(district.getKey());
                      		userLocation.setLatitude(district.getLatitude());
                      		userLocation.setLongtitude(district.getLongtitude());
                      		userLocation.setUserType(command.getAccount().type());
                      		userLocation.setUserUuid(command.getAccount().uuid());
                      		
                      		entities.add(userLocation);
        					} catch (Exception e) {
        						LOG.error("[handleStoreAnswer] error: {}", e.getMessage());
        					}
                  	}
                  });
              	userLocationService.save(entities, command.getAccount().uuid());
              	LOG.info("Stored user district locations successfully");
    		} catch (Exception e2) {
    			LOG.error("Handle for storing user district location failed: ", e2.getMessage());
    		}
        }

        // publish event to the stream
        final AnswerSubmittedEvent event = AnswerSubmittedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString()).setUserId(command.getAccount().uuid())
                .setUserType(command.getAccount().type()).setQuestionId(command.getQuestionId())
                .setOptionKeys(optionRecords).setAnswerId(answerId)
                .setSubmittedAt(System.currentTimeMillis()).build();

        eventHandler.publish(event);
    }

//    public Observable<Map<String, Boolean>> process(final CompleteProfileCommand command) {
//
//        final Account account = command.getAccount();
//        final String token = account.ugAccessToken();
//        final String questionQuery = String.format(GET_REQUIRED_QUESTIONS_LANGUAGE, DEFAULT_LANGUAGE, account.type());
//        final String answerQuery = String.format(USER_ANSWERED_QUERY, account.uuid());
//
//         return Observable
//                .zip(questionClient.getQuestionsAsync(token, questionQuery, profileProperties.getQuestionaire().getQuestionLimit()),
//                        answerClient.getAnswersByUserAsync(token, answerQuery, profileProperties.getQuestionaire().getAnswerLimit()),
//                        (questionResponse, answerResponse) -> {
//                            if (UserType.EU.name().equalsIgnoreCase(account.type())) {
//                            	return true;
//                            } else if (UserType.PT.name().equalsIgnoreCase(account.type())) {
//                            	return checkPTCompleteProfile(questionResponse.getEntities(), answerResponse.getEntities());
//                            }
//                            
//                            return false;
//                        })
//                .doOnError(error -> LOG.error("Error happen {}", error))
//                .map(success -> {
//                    if (!success) {
//                    	LOG.info("Failed to verify if user has completed profile. User {}, userType {}", account.uuid(), account.type());
//                        return Collections.singletonMap(SUCCESS, Boolean.FALSE);
//                    }
//                    final CompleteProfileEvent event = CompleteProfileEvent.newBuilder()
//                            .setEventId(UUID.randomUUID().toString())
//                            .setUserId(account.uuid())
//                            .setUserType(account.type())
//                            .setSubmittedAt(System.currentTimeMillis())
//                            .build();
//                    
//                    eventHandler.publish(event);
//                    LOG.info("Fired CompleteProfileEvent for {}", account.uuid());
//                    return Collections.singletonMap(SUCCESS, Boolean.TRUE);
//                });
//    }
    
    public Map<String, Boolean> process(final CompleteProfileCommand command) {

        final Account account = command.getAccount();
//        final String token = account.ugAccessToken();
//        final String answerQuery = String.format(USER_ANSWERED_QUERY, account.uuid());
//        boolean result = false;

        try {
//        	List<asia.cmg.f8.profile.database.entity.QuestionEntity> questions = questionRepo.findRequiredQuestionsByLanguageAndUserType(account.type(), DEFAULT_LANGUAGE);
//        	UserGridResponse<AnswerEntity> answers = answerClient.getAnswersByUser(answerQuery, profileProperties.getQuestionaire().getAnswerLimit());
//        	
//        	if (UserType.EU.name().equalsIgnoreCase(account.type())) {
//        		result = true;
//            } else if (UserType.PT.name().equalsIgnoreCase(account.type())) {
//            	result = checkPTCompleteProfile(questions, answers.getEntities());
//            }
//            
//        	if(result == true) {
//        		final CompleteProfileEvent event = CompleteProfileEvent.newBuilder()
//                      .setEventId(UUID.randomUUID().toString())
//                      .setUserId(account.uuid())
//                      .setUserType(account.type())
//                      .setSubmittedAt(System.currentTimeMillis())
//                      .build();
//        		
//        		eventHandler.publish(event);
//        		LOG.info("Fired CompleteProfileEvent for {}", account.uuid());
//              
//                userClient.updateProfileBySystem(Collections.singletonMap("onBoardCompleted", true),
//                        account.uuid());
//                LOG.info("Update onBoardCompleted is true successfully for user {}", account.uuid());
//        	}
        	
        	final CompleteProfileEvent event = CompleteProfileEvent.newBuilder()
                    .setEventId(UUID.randomUUID().toString())
                    .setUserId(account.uuid())
                    .setUserType(account.type())
                    .setSubmittedAt(System.currentTimeMillis())
                    .build();
      		eventHandler.publish(event);
      		LOG.info("Fired CompleteProfileEvent for {}", account.uuid());
            
      		userClient.updateProfileBySystem(Collections.singletonMap("onBoardCompleted", true), account.uuid());
      		LOG.info("Update onBoardCompleted is true successfully for user {}", account.uuid());
	    	
        	return Collections.singletonMap(SUCCESS, Boolean.TRUE);
    	} catch (Exception e) {
    		LOG.info("Process finish onboard questions happended error: {}", e.getMessage());
    		return Collections.singletonMap(SUCCESS, Boolean.FALSE);
    	}
    }
    
    private boolean checkPTCompleteProfile(final List<asia.cmg.f8.profile.database.entity.QuestionEntity> questions,
                                           final List<AnswerEntity> answers) {
        final List<String> answerKeys = answers.stream().map(AnswerEntity::getQuestionId).collect(Collectors.toList());
        final Optional<asia.cmg.f8.profile.database.entity.QuestionEntity> questOpt = questions.stream().filter(question -> !answerKeys.contains(question.key)).findFirst();
        return !questOpt.isPresent();
    }

    public List<asia.cmg.f8.profile.database.entity.QuestionEntity> getQuestions(final Account account, final QuestionQuery query) {
        
    	return questionRepo.findByLanguageAndUsedForAndHideIsFalse(query.getType(), query.getLanguage());
    }

    public List<asia.cmg.f8.profile.database.entity.QuestionEntity> getQuestionsByAdmin(final Account account, final QuestionQuery query) {
    	
    	return questionRepo.findByLanguageAndUsedFor(query.getType(), query.getLanguage());
    }

    public List<AnswerEntity> getAnswersByUser(final Account account) {
        return answerClient.getAnswersByUser(
                String.format(USER_ANSWERED_QUERY, account.uuid()),
                profileProperties.getQuestionaire().getAnswerLimit()).getEntities();
    }

    public void deleteAllUserAnswers(final Account account) {
        final Map<String, Boolean> body = Collections.singletonMap("deleted", Boolean.TRUE);
        modifyAnswerClient.deleteAnswersByUser(String.format(USER_ANSWERED_QUERY,
                account.uuid()),
                profileProperties.getQuestionaire().getAnswerLimit(), body);
    }

    public void updateUserProfileComplete(final CompleteProfileEvent event) {
    	//Check if PT is modified Profile -> Will not send message. Else send email message.
    	LOG.info("------------Begin updated onBoardCompleted profile for user {}" + event.getUserId());
//    	boolean isSendMessage = true;
    	//Retrieve the user to check whether first time on-boarding or update profile
//    	final UserGridResponse<UserEntity> userResp = userClient.getUser(event.getUserId().toString());
    	
//    	if(!userResp.getEntities().isEmpty()) {
//    		final UserEntity userEntity = userResp.getEntities().get(0);
//    		//This case PT or EU update their profile
//    		if(userEntity.getStatus().documentStatus().name().equalsIgnoreCase(DocumentStatusType.ONBOARD.name())) {
//    			isSendMessage = false;
//    		}
//    	}
    	
        if (UserType.PT.name().equalsIgnoreCase(event.getUserType().toString())/* && isSendMessage*/) {
            userClient.updateProfileBySystem(Collections.singletonMap("onBoardCompleted", true),
                    event.getUserId().toString());
        }
    }

    public List<QuestionEntity> getQuestionsBySystem(final Set<String> questionUuid) {
        final String query = questionUuid.stream()
                .map(questionId -> "uuid ='" + questionId + "'")
                .collect(Collectors.joining(" or "));

        return questionClient.getQuestionsBySystem(
                String.format(GET_QUESTIONS_BY_UUID, query),
                profileProperties.getQuestionaire().getQuestionLimit()).getEntities();
    }

	public List<AnswerEntity> getAnswersBySystem(final String userId) {
		return answerClient.getAnswersBySystem(String.format(USER_ANSWERED_QUERY, userId),
				profileProperties.getQuestionaire().getAnswerLimit()).getEntities();
	}

	/**
	 * Finding PT_AMBASSADOR and PT_LEADER users exclude list of uuids
	 * @param uuids
	 * @param limit
	 * @return List<String> of user's uuid 
	 */
	public List<String> findAmbassadorOrLeaderTrainers(final List<String> uuids, final int limit) {
		
		try {
			List<BasicUserEntity> trainers = basicUserRepo.searchAmbassadorOrLeaderTrainers(uuids, limit);
			return trainers.stream().map(trainer -> trainer.getUuid()).collect(Collectors.toList());
		} catch (Exception e) {
			LOG.error("[findAmbassadorOrLeaderTrainers] exception detail: ", e.getMessage());
			return Collections.emptyList();
		}
	}
	
	/**
	 * Finding PT_AMBASSADOR and PT_LEADER users
	 * @param limit
	 * @return List<String> of user's uuid 
	 */
	public List<String> findAmbassadorOrLeaderTrainers(final int limit) {
		
		try {
			List<BasicUserEntity> trainers = basicUserRepo.searchAmbassadorOrLeaderTrainers(limit);
			return trainers.stream().map(trainer -> trainer.getUuid()).collect(Collectors.toList());
		} catch (Exception e) {
			LOG.error("[findAmbassadorOrLeaderTrainers] exception detail: ", e.getMessage());
			return Collections.emptyList();
		}
	}
	
	public List<BasicUserEntity> findApprovedTrainersByUuids(List<String> uuids, int limit) {
		try {
			return basicUserRepo.getApprovedTrainersByUuidList(uuids, limit);
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
}
