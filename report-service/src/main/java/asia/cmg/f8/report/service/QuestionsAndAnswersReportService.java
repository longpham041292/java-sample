package asia.cmg.f8.report.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.common.util.PagedUserGridResponse;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.report.client.AnswerClient;
import asia.cmg.f8.report.client.UserClient;
import asia.cmg.f8.report.dto.CounterUsersAnsweredQuestion;
import asia.cmg.f8.report.entity.database.QuestionEntity;
import asia.cmg.f8.report.entiy.usergrid.AnswerEntity;
import asia.cmg.f8.report.entiy.usergrid.PagedUserResponse;
import asia.cmg.f8.report.entiy.usergrid.UserEntity;
import asia.cmg.f8.report.repository.QuestionRepository;

@Service
public class QuestionsAndAnswersReportService {

	@Autowired
	private AnswerClient answerClient;
	
	@Autowired
	private UserClient userClient;
	
	private static final String QUERY_ANSWERS_OF_QUESTION = "where questionId = '%s'";
	private static final String QUERY_ANSWERS_OF_OWNER = "where owner = '%s'";
	private static final String QUERY_PT_COMPLETED_OBQ = "where onBoardCompleted = true";
	private static final String QUERY_ACTIVED_EU = "where created > %s and userType = 'eu' and activated = true";
	
	public int countEUCompletedOBQ(int totalQuestions) {
		int result = 0;
		String cursor = null;
		int limit = 1000;
		long fromDate = 1546275600000L;	// 01-01-2019 00:00:00
		
		do {
			PagedUserGridResponse<UserEntity> pagedResponse = userClient.searchUsers(String.format(QUERY_ACTIVED_EU, fromDate), limit, cursor);
			cursor = pagedResponse.getCursor();
			if(pagedResponse.getEntities() != null) {
				List<UserEntity> users = pagedResponse.getEntities();
				for (UserEntity userEntity : users) {
					UserGridResponse<AnswerEntity> answersUG = answerClient.getAnswersBySystem(String.format(QUERY_ANSWERS_OF_OWNER, userEntity.getUuid()), limit);
					if(answersUG.getEntities() != null && answersUG.getEntities().size() >= totalQuestions) {
						result++;
					}
				}
			}
		} while (!StringUtils.isEmpty(cursor));
		
		return result;
	}
	
	public int countPTCompletedOBQ() {
		int result = 0;
		String cursor = null;
		int limit = 1000;
		
		do {
			PagedUserGridResponse<UserEntity> pagedResponse = userClient.searchUsers(QUERY_PT_COMPLETED_OBQ, limit, cursor);
			cursor = pagedResponse.getCursor();
			if(pagedResponse.getEntities() != null) {
				result += pagedResponse.getEntities().size();
			}
		} while (!StringUtils.isEmpty(cursor));
		
		return result;
	}
	
	public CounterUsersAnsweredQuestion countmNumOfUserAnsweredQuestion(final QuestionEntity questionEntity) {
		String cursor = null;
		CounterUsersAnsweredQuestion counter = new CounterUsersAnsweredQuestion();
		int LIMIT = 1000;
		Integer countAnsweredByEU = 0;
		Integer countAnsweredByPT = 0;
		
		do {
			PagedUserGridResponse<AnswerEntity> answersUG = answerClient.getAnswersBySystem(String.format(QUERY_ANSWERS_OF_QUESTION, questionEntity.key),
															LIMIT, cursor);
			cursor = answersUG.getCursor();
			if(answersUG.getEntities() != null) {
				List<AnswerEntity> answers = answersUG.getEntities();
				for (AnswerEntity answerEntity : answers) {
					if(UserType.PT == answerEntity.getUserType()) {
						countAnsweredByPT ++;
					} else {
						countAnsweredByEU ++;
					}
				}
			}
		} while (!StringUtils.isEmpty(cursor));
		
		counter.setCountAnsweredByEU(countAnsweredByEU);
		counter.setCountAnsweredByPT(countAnsweredByPT);
		counter.setQuestionKey(questionEntity.key);
		counter.setQuestionContent(questionEntity.description);
		
		return counter;
	}
}
