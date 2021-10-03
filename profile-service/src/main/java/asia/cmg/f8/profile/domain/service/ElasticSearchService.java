package asia.cmg.f8.profile.domain.service;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.profile.config.UserProfileProperties;
import asia.cmg.f8.profile.database.entity.QuestionOptionEntity;
import asia.cmg.f8.profile.domain.entity.AnswerEntity;
import asia.cmg.f8.profile.domain.entity.Option;
import asia.cmg.f8.profile.domain.entity.QuestionEntity;
import asia.cmg.f8.profile.domain.entity.UserEntity;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.random.RandomScoreFunctionBuilder;
import org.elasticsearch.index.query.functionscore.weight.WeightBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ElasticSearchService {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchService.class);

    private static final String SKILLS_FIELD = "skills";
    private static final String ACTIVATED = "activated";
    private static final String APPROVED = "approved";
    private static final String CREATED_DATE = "created";

    private final ElasticsearchTemplate elasticsearchTemplate;

    private final UserProfileProperties profileProperties;

    private final ProfileService profileService;

    @Inject
    public ElasticSearchService(
            final ElasticsearchTemplate elasticsearchTemplate,
            final UserProfileProperties profileProperties,
            final ProfileService profileService) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.profileProperties = profileProperties;
        this.profileService = profileService;
    }

    public Page<UserEntity> matchedTrainer(
            final Account account,
            final Long timestamp,
            final Pageable pageable) {

        LOG.debug("Start to build query for Elasticsearch");

        final List<String> traineeKeyAnswers = getSearchedSkills(account);

        final Map<String, QuestionOptionEntity> optionWeight = getWeightTraineeAnswers(traineeKeyAnswers, account);

        final String searchSkills = traineeKeyAnswers
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.joining(" "));

        final FunctionScoreQueryBuilder functionScoreQueryBuilder;

        //Default filter
        final BoolQueryBuilder boolFilterQuery = new BoolQueryBuilder();
        boolFilterQuery.must(new TermQueryBuilder(ACTIVATED, true));
        boolFilterQuery.must(new TermQueryBuilder(APPROVED, true));

        if (StringUtils.isEmpty(searchSkills)) {
            LOG.info("Random trainers for this user: " + account.uuid());
            final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.filter(boolFilterQuery);

            functionScoreQueryBuilder = new FunctionScoreQueryBuilder(boolQueryBuilder);
            functionScoreQueryBuilder.add(new RandomScoreFunctionBuilder());
        } else {

            // Build Elastic Query
            final RangeQueryBuilder rangeQueryBuilder = QueryBuilders
                    .rangeQuery(CREATED_DATE)
                    .to(timestamp);

            final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.must(rangeQueryBuilder);

            // Add filter fields for eliminated questions.
            final List<String> filter = optionWeight.entrySet()
                    .stream()
                    .filter(option -> option.getValue().filtered)
                    .map(Map.Entry::getKey)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            if (!filter.isEmpty()) {
                final TermsQueryBuilder filterBuilder = new TermsQueryBuilder(SKILLS_FIELD, filter);
                boolFilterQuery.must(filterBuilder);
            }

            boolQueryBuilder.filter(boolFilterQuery);

            functionScoreQueryBuilder = new FunctionScoreQueryBuilder(boolQueryBuilder)
                    .scoreMode("sum")
                    .boostMode("replace");

            // Add fields are not eliminated question for ranking.
            optionWeight.entrySet()
                    .stream()
                    .filter(option -> !option.getValue().filtered)
                    .forEach(option ->
                            functionScoreQueryBuilder.add(new TermQueryBuilder(
                                            SKILLS_FIELD,
                                            StringUtils.lowerCase(option.getValue().key)),
                                    new WeightBuilder().setWeight(option.getValue().weight)
                            )
                    );
        }

        final SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(functionScoreQueryBuilder)
                .withIndices(profileProperties.getElasticIndice())
                .withTypes(profileProperties.getElasticType())
                .withPageable(pageable)
                .build();

        return elasticsearchTemplate.queryForPage(searchQuery, UserEntity.class);
    }

    private List<String> getSearchedSkills(final Account account) {

        final List<AnswerEntity> answers = profileService.getAnswersByUser(account);

        final List<String> skills = new ArrayList<>();

        answers.forEach(answer -> skills.addAll(answer.getOptionKeys()));

        return skills.stream().distinct().collect(Collectors.toList());
    }

    // Get answers of user and filter options needed to put weight.
    private Map<String, QuestionOptionEntity> getWeightTraineeAnswers(
            final List<String> traineeKeyAnswers,
            final Account account) {

        final Map<String, QuestionOptionEntity> optionWeight = new HashMap<>();

        final List<asia.cmg.f8.profile.database.entity.QuestionEntity> questions = profileService.getQuestions(
                account,
                new QuestionQuery(UserType.PT.toString(), "en")
        );

        if (questions.isEmpty()) {
            throw new ConstraintViolationException(
                    "Question list is not initialized.",
                    Collections.emptySet());
        }

        questions.forEach(question -> {
            final boolean isFiltered = question.filtered;
            final Integer weight = question.weight;
            question.getOptions().forEach(option -> {
                if (traineeKeyAnswers.contains(option.key)) {
                    option.filtered = Boolean.valueOf(isFiltered);
                    option.weight = option.weight != null ? option.weight : weight;
                    optionWeight.put(option.key, option);
                }
            });
        });

        return optionWeight;
    }
}
