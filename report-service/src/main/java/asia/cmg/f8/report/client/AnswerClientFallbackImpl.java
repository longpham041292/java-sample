package asia.cmg.f8.report.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import asia.cmg.f8.common.util.PagedUserGridResponse;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.report.entiy.usergrid.AnswerEntity;
import rx.Observable;

@Component
public class AnswerClientFallbackImpl implements AnswerClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(AnswerClient.class);
			
	@Override
	public UserGridResponse<AnswerEntity> storeAnswerBySystem(AnswerEntity answer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserGridResponse<AnswerEntity> updateAnswerBySystem(String answerUuid, AnswerEntity answer) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public UserGridResponse<AnswerEntity> getAnswersByUser(String token, String query, int limit) {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public PagedUserGridResponse<AnswerEntity> getAnswersByUser(String token, String query, int limit, String cursor) {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public Observable<UserGridResponse<AnswerEntity>> getAnswersByUserAsync(String token, String query, int limit) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public UserGridResponse<AnswerEntity> getAnswersBySystem(String query, int answerLimit) {
		LOGGER.error("[getAnswersBySystem] could not get answers on query [{}]", query);
		return new UserGridResponse<AnswerEntity>();
	}

	@Override
	public PagedUserGridResponse<AnswerEntity> getAnswersBySystem(String query, int answerLimit, String cursor) {
		LOGGER.error("[getAnswersBySystem] could not get answers on query [{}] and cursor [{}]", query, cursor);
		return new PagedUserGridResponse<AnswerEntity>();
	}
}
