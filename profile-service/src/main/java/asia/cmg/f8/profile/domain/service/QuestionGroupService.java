package asia.cmg.f8.profile.domain.service;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.api.question.QuestionGroupApi;
import asia.cmg.f8.profile.domain.client.QuestionGroupClient;
import asia.cmg.f8.profile.domain.entity.QuestionGroupEntity;

@Component
public class QuestionGroupService {

	private static final Logger LOG = LoggerFactory.getLogger(QuestionGroupApi.class);
	
	@Autowired
	private QuestionGroupClient questionGroupClient;
	
	private String QUERY_GETALL = "select *";
	
	public List<QuestionGroupEntity> getAll() {
		try {
			UserGridResponse<QuestionGroupEntity> result = questionGroupClient.getByQuery(QUERY_GETALL);
			return result.getEntities();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return Collections.emptyList();
		}
	}
}
