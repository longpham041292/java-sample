package asia.cmg.f8.profile.domain.client;

import org.springframework.stereotype.Component;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.domain.entity.QuestionGroupEntity;

@Component
public class QuestionGroupClientFallbackImpl implements QuestionGroupClient {

//	@Override
//	public UserGridResponse<QuestionGroupEntity> getByQueryWithLimit(String token, String query, int limit) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public UserGridResponse<QuestionGroupEntity> getByQuery(String query) {
		// TODO Auto-generated method stub
		return null;
	}
}
