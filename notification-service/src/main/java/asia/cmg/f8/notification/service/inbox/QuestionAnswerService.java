package asia.cmg.f8.notification.service.inbox;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.notification.client.QuestionAnswerClient;
import asia.cmg.f8.notification.config.NotificationProperties;
import asia.cmg.f8.notification.dto.QuestionResponse;
import asia.cmg.f8.notification.entity.AnswerEntity;
import asia.cmg.f8.notification.entity.QuestionEntity;
import asia.cmg.f8.notification.entity.QuestionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created on 1/4/17.
 */
//TODO: It should be replace by calling profile service
@Service
public class QuestionAnswerService {
    private static final Logger LOG = LoggerFactory.getLogger(QuestionAnswerService.class);

    private static final String GET_ADMIN_SHOW_QUESTIONS =
            "select * where language = '%1$s' and  usedFor = 'eu' " +
                    "and (not questionType ='" + QuestionType.GUIDE + "') " +
                    "and hide=false order by sequence asc";
    private static final String GET_QUESTIONS_BY_UUID =
            "select * where language = '%1$s' and  usedFor = 'eu' and (%2$s) order by sequence asc";
    private static final String USER_ANSWERED_QUERY =
            "select * where owner='%s' and (not deleted = true)";

    private final QuestionAnswerClient questionAnswerClient;
    private final NotificationProperties notificationProperties;

    public QuestionAnswerService(final QuestionAnswerClient questionAnswerClient,
                                 final NotificationProperties notificationProperties) {
        this.questionAnswerClient = questionAnswerClient;
        this.notificationProperties = notificationProperties;
    }

    /**
     * This will be used for load question that based on admin show/hide QA.
     *
     * @return shown question
     */
    public List<QuestionEntity> getAdminShowQuestions(final Account account, final String language) {
        final String query = String.format(GET_ADMIN_SHOW_QUESTIONS, language);
        return this.getQuestionByQuery(query);
    }

    public List<QuestionEntity> getQuestionsByKey(final Set<String> questionUuid) {
        final String listQuestionKey = questionUuid.stream()
                .map(questionId -> "key ='" + questionId + "'")
                .collect(Collectors.joining(" or "));
        final String query = String.format(GET_QUESTIONS_BY_UUID,
                notificationProperties.getDefaultLang(), listQuestionKey);
        return this.getQuestionByQuery(query);
    }

    public List<AnswerEntity> getAnswersByUserId(final String userId) {
        final String query = String.format(USER_ANSWERED_QUERY, userId);
        final UserGridResponse<AnswerEntity> answerResp =
                questionAnswerClient.getAnswersByQuery(query);

        if (Objects.isNull(answerResp) || answerResp.isEmpty()) {
            LOG.error("Could not found any answer in query {}", query);
            return Collections.emptyList();
        }
        return answerResp.getEntities();
    }

    public List<QuestionResponse> getQuestionResponseOfUser(final String userId) {
        LOG.info("Retrieving question answer of user {}", userId);

        //Get answers of user
        final List<AnswerEntity> answers = getAnswersByUserId(userId);
        if (answers.isEmpty()) {
            LOG.error("Could not found any answer of user {}", userId);
            return Collections.emptyList();
        }
        final Map<String, AnswerEntity> answerResp = new HashMap<>(answers.size());
        final Set<String> questionKeyList = new HashSet<>(answers.size());

        answers.stream().forEach(answerEntity -> {
            answerResp.put(answerEntity.getQuestionId(), answerEntity);
            questionKeyList.add(answerEntity.getQuestionId());
        });

        //Get all questions that user answered already 
        final List<QuestionEntity> questions = getQuestionsByKey(questionKeyList);

        return questions.stream().map(question -> {
            final QuestionResponse result = new QuestionResponse(question);
            //TODO: filter chosen options
            result.setAnswered(true);
            result.getOptions().stream().forEach(option -> {
                final AnswerEntity answer = answerResp.get(question.getKey());
                if (answer.getOptionKeys().contains(option.getKey())) {
                    option.setChoose(true);
                }
            });
            return result;
        }).collect(Collectors.toList());
    }

    private List<QuestionEntity> getQuestionByQuery(final String query) {
        final UserGridResponse<QuestionEntity> questionResp =
                questionAnswerClient.getQuestionsByQuery(query);
        if (Objects.isNull(questionResp) || questionResp.getEntities().isEmpty()) {
            LOG.error("Could not found any questions in query {}", query);
            return Collections.emptyList();
        }
        return questionResp.getEntities();

    }

}
