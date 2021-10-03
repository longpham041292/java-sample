package asia.cmg.f8.notification.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.notification.entity.AnswerEntity;
import asia.cmg.f8.notification.entity.QuestionEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created on 1/12/17.
 */
@Component
public class QuestionAnswerClientFallbackImpl implements QuestionAnswerClient {
    @Override
    public UserGridResponse<AnswerEntity> getAnswersByQuery(
            @PathVariable(QUERY) final String query) {
        return null;
    }

    @Override
    public UserGridResponse<QuestionEntity> getQuestionsByQuery(
            @PathVariable(QUERY) final String query) {
        return null;
    }
}
