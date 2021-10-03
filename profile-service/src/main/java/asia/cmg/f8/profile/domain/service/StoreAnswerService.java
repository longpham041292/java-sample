package asia.cmg.f8.profile.domain.service;

import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.profile.domain.client.AnswerClient;
import asia.cmg.f8.profile.domain.client.UserClient;
import asia.cmg.f8.profile.domain.entity.AnswerEntity;
import asia.cmg.f8.profile.domain.entity.UserEntity;
import asia.cmg.f8.profile.domain.repository.AnswerRepository;
import asia.cmg.f8.profile.domain.repository.UserElasticsearchRepository;
import asia.cmg.f8.profile.AnswerSubmittedEvent;
import asia.cmg.f8.profile.OptionRecord;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Component
public class StoreAnswerService {
    private static final Logger LOG = LoggerFactory.getLogger(StoreAnswerService.class);

    private final AnswerClient answerClient;
    private final UserClient userClient;
    private final UserElasticsearchRepository userElasticsearchRepository;
    
    @Autowired
    private AnswerRepository answerRepo;

    @Inject
    public StoreAnswerService(
            final AnswerClient answerClient,
            final UserClient userClient,
            final UserElasticsearchRepository userElasticsearchRepository) {
        this.answerClient = answerClient;
        this.userClient = userClient;
        this.userElasticsearchRepository = userElasticsearchRepository;
    }

    public boolean storeAnswerUserGrid(final AnswerEntity message) {
        LOG.info("Store to usergrid {}", message.toString());
        try {
        	if (StringUtils.isEmpty(message.getUuid())) {
                answerClient.storeAnswerBySystem(message);
            } else {
                answerClient.updateAnswerBySystem(message.getUuid(), message);
            }
        	return true;
		} catch (Exception e) {
			return false;
		}
    }
    
    public boolean storeAnswerDatabase(final AnswerSubmittedEvent message) {
        try {
        	if(message != null) {
        		LOG.info("Store answer to database {}", message.toString());
        		
        		List<OptionRecord> optionKeys = message.getOptionKeys();
        		String questionId = message.getQuestionId().toString();
        		String userUuid = message.getUserId().toString();
        		String userType = message.getUserType().toString();
        		
        		List<asia.cmg.f8.profile.database.entity.AnswerEntity> answerEntities = answerRepo.findByOwnerUuidAndQuestionKey(userUuid, questionId);
        		
        		if(!answerEntities.isEmpty()) {
        			answerRepo.delete(answerEntities);
        			answerEntities.clear();
        		}
        		
        		for (OptionRecord optionRecord : optionKeys) {
					String optionKey = optionRecord.getOption().toString();
					asia.cmg.f8.profile.database.entity.AnswerEntity answerEntity = new asia.cmg.f8.profile.database.entity.AnswerEntity();
					answerEntity.setOptionKey(optionKey);
					answerEntity.setOwnerUuid(userUuid);
					answerEntity.setQuestionKey(questionId);
					answerEntity.setUserType(userType);
					
					answerEntities.add(answerEntity);
				}
        		
        		// Saving to database
        		answerRepo.save(answerEntities);
        		answerRepo.flush();
        	}
        	return true;
		} catch (Exception e) {
			LOG.info("Store answer to database fail: {}", e.getMessage());
			return false;
		}
    }

    public void storeAnswerElastic(final AnswerSubmittedEvent answer) {
        if (Objects.isNull(answer)) {
            throw new ConstraintViolationException(
                    "Received null object from kafka.",
                    Collections.emptySet());
        }

        LOG.info("Processing to store answer to Elasticsearch for user ID: " + answer.getUserId());

        final Optional<String> UserId = Optional.of(answer.getUserId().toString());
        if (!UserId.isPresent()) {
            throw new ConstraintViolationException("User ID is not contained in this event request: " +
                    answer.getEventId(), Collections.emptySet());
        }
        if (!StringUtils.equals(answer.getUserType().toString(), UserType.PT.toString().toLowerCase())) {
            LOG.info("Don't need to store normal user to Elasticsearch: " + answer.getUserId());
            return;
        }
      
        Optional<UserEntity> trainer = ofNullable(userElasticsearchRepository.findOne(answer.getUserId().toString()));
        if (trainer.isPresent()) {

            final UserEntity user = trainer.get();
            //Handling delete option case and adding new option to skills.
            final List<OptionRecord> optionRecords = answer.getOptionKeys();

            List<String> oldSkills = Collections.emptyList();
            if (user.getSkills() != null) {
                oldSkills = Arrays.asList(user.getSkills().split(" "));
            }

            final List<String> newSkills = oldSkills.stream()
                    .filter(skill -> !isOptionKeyRemoved(skill, optionRecords))
                    .collect(Collectors.toList());

            final List<String> newOption = optionRecords.stream()
                    .filter(OptionRecord::getChoose)
                    .map(optionRecord -> optionRecord.getOption().toString())
                    .collect(Collectors.toList());

            newSkills.addAll(newOption);
            final String finalSkills = newSkills.stream().distinct().collect(Collectors.joining(" "));

            final UserEntity userEntity = user.withSkills(finalSkills);

            userElasticsearchRepository.save(userEntity);

            LOG.info("Finished to update the skills: " +
                    finalSkills +
                    " to Elasticsearch for uuid: " + UserId.get());
        } else {
            trainer = userClient.getUser(UserId.get()).getEntities()
                    .stream()
                    .findFirst();

            if (!trainer.isPresent()) {
                throw new ConstraintViolationException(
                        "User not exist in the system for the answer object from Kafka.",
                        Collections.emptySet());
            }

            final String skills = answer.getOptionKeys()
                    .stream()
                    .filter(OptionRecord::getChoose)
                    .map(option -> option.getOption().toString().toLowerCase())
                    .distinct()
                    .collect(Collectors.joining(" "));

            final UserEntity userEntity = UserEntity.copyOf(trainer.get()).withSkills(skills);

            userElasticsearchRepository.save(userEntity);
            LOG.info("Finished to add new trainer uuid: " +
                    UserId + " with skills: " +
                    skills + " to Elasticsearch");
        }

    }

    private boolean isOptionKeyRemoved(final String key, final List<OptionRecord> optionRecords) {
        for (final OptionRecord optionRecord : optionRecords) {
            if (optionRecord.getOption().toString().equalsIgnoreCase(key) && !optionRecord.getChoose()) {
                return true;
            }
        }
        return false;
    }

}
