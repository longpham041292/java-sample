package asia.cmg.f8.profile.domain.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.domain.client.AnswerClient;
import asia.cmg.f8.profile.domain.entity.AnswerEntity;

@Service
public class AnswerService {

	private int LIMIT_1000 = 1000;
	private String USER_ANSWERED_QUERY = "select * where owner='%s' and (not deleted = true)";
	
	@Autowired
    private AnswerClient answerClient;
	
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
}
