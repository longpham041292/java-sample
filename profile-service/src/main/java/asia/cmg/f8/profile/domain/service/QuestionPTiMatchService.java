package asia.cmg.f8.profile.domain.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.domain.client.AnswerClient;
import asia.cmg.f8.profile.domain.entity.AnswerEntity;
import asia.cmg.f8.profile.domain.repository.QuestionRepository;
import asia.cmg.f8.profile.dto.QuestionPTiMatch;

@Service
public class QuestionPTiMatchService {

	private int LIMIT_1000 = 1000;
	private String USER_ANSWERED_QUERY = "select * where owner='%s' and (not deleted = true)";

	private final String AGE_QUESTION_KEY = "Age";
    private final String GENDER_QUESTION_KEY = "Gender";
    
    private final String AGE_DOESNOTMATTER_ANSWER_KEY = "AgeA3";
    private final String GENDER_DOESNOTMATTER_ANSWER_KEY = "GenderA3";
	
    @Autowired
    private AnswerClient answerClient;

    @Autowired
    private QuestionRepository questionRepo;

    public List<QuestionPTiMatch> getQuestionsPTIMatch(String accountToken, String userUuid, Map<String, asia.cmg.f8.profile.database.entity.QuestionEntity> questionsMap) {

        List<QuestionPTiMatch> questionPTIs = new ArrayList<>();
        List<AnswerEntity> answers = this.getAnswersByUserUuid(accountToken, userUuid);

        answers.forEach(answer -> {
        	QuestionPTiMatch questionPTI = new QuestionPTiMatch();
        	questionPTI.setQuestionId(answer.getQuestionId());
        	asia.cmg.f8.profile.database.entity.QuestionEntity question = questionsMap.get(answer.getQuestionId());
        	if(question != null) {
        		questionPTI.setWeight(question.weight);
        	}
            questionPTI.setOptions(answer.getOptionKeys());
            questionPTIs.add(questionPTI);
        });

        return questionPTIs;
    }
    
    public List<AnswerEntity> getAnswersByUserUuid(String accountToken, String userUuid) {
    	try {
    		UserGridResponse<AnswerEntity> response = answerClient.getAnswersByUser(String.format(USER_ANSWERED_QUERY, userUuid), LIMIT_1000);
    		if(!Objects.isNull(response)) {
    			return response.getEntities();
    		}
		} catch (Exception e) {
//			Logging
		}
    	
    	return Collections.emptyList();
    }

    /**
     * Get question from usergrid
     * @param queryType
     * @return
     */
//    public List<QuestionEntity> getQuestions(String accountToken, QuestionQuery queryType) {
//    	try {
//    		UserGridResponse<QuestionEntity> response = questionClient.getQuestions(String.format(GET_QUESTIONS_LANGUAGE, queryType.getLanguage(), queryType.getType()), LIMIT_1000);
//    		if(!Objects.isNull(response)) {
//    			return response.getEntities();
//    		}
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//    	
//    	return Collections.emptyList();
//    }
    
    /**
     * Get question from database
     * @param queryType
     * @return
     */
    public List<asia.cmg.f8.profile.database.entity.QuestionEntity> getQuestions(QuestionQuery queryType) {
    	try {
    		List<asia.cmg.f8.profile.database.entity.QuestionEntity> questions = questionRepo.findByUserTypeAndLanguage(queryType.getType(), queryType.getLanguage());
    		return questions;
		} catch (Exception e) {
			return Collections.emptyList();
		}
    }
    
    public boolean isMatchingOneOfAnswers(final AnswerEntity userAnswer, final AnswerEntity trainerAnswer) {
    	Set<String> userOptionKeys = userAnswer.getOptionKeys();
    	Set<String> trainerOptionKeys = trainerAnswer.getOptionKeys();
    	
    	if(userAnswer.getQuestionId().compareToIgnoreCase(trainerAnswer.getQuestionId()) != 0) {
    		return false;
    	}
    	
    	for (String userOptionKey : userOptionKeys) {
			for (String trainerOptionKey : trainerOptionKeys) {
				if(userAnswer.getQuestionId().compareToIgnoreCase(AGE_QUESTION_KEY) == 0 &&
						userOptionKey.compareToIgnoreCase(AGE_DOESNOTMATTER_ANSWER_KEY) == 0) {
					return true;
				}
				
				if(userAnswer.getQuestionId().compareToIgnoreCase(GENDER_QUESTION_KEY) == 0 &&
						userOptionKey.compareToIgnoreCase(GENDER_DOESNOTMATTER_ANSWER_KEY) == 0) {
					return true;
				}
				
				if(userOptionKey.compareToIgnoreCase(trainerOptionKey) == 0) {
					return true;
				}
			}
		}
    	
    	return false;
    }
}
