package asia.cmg.f8.profile.domain.event;

import asia.cmg.f8.common.profile.ChangeUserInfoEvent;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.profile.domain.client.UserClient;
import asia.cmg.f8.profile.domain.entity.UserEntity;
import asia.cmg.f8.profile.domain.repository.UserElasticsearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.Optional;

/**
 * A handle responsible for sync information of <strong>TRAINER</strong> user to search engine (Elastic).
 * <p>
 * Created on 1/5/17.
 */
@Component
public class SyncUserInfoWithElasticHandler {

    public static final Logger LOGGER = LoggerFactory.getLogger(SyncUserInfoWithElasticHandler.class);

    @Autowired
    @Qualifier("changeUserInfoEventConverter")
    private MessageConverter changUserInfoEventConverter;

    private final UserClient userClient;
    private final UserElasticsearchRepository userElasticsearchRepository;

    public SyncUserInfoWithElasticHandler(final UserClient userClient, final UserElasticsearchRepository userElasticsearchRepository) {
        this.userClient = userClient;
        this.userElasticsearchRepository = userElasticsearchRepository;
    }

    @StreamListener(EventStream.USER_INFO_IN_CHANNEL)
    public void onEvent(final Message message) {

        final ChangeUserInfoEvent event = (ChangeUserInfoEvent) changUserInfoEventConverter.fromMessage(message, ChangeUserInfoEvent.class);

        if (event == null) {
            throw new ConstraintViolationException("Event data is null", Collections.emptySet());
        }

        if (!event.getUserType().toString().equalsIgnoreCase(UserType.PT.toString())) {
            LOGGER.info("Don't need to sync normal user to ES.");
            return;
        }

        final Optional<UserEntity> userGridEntity = userClient.getUser(event.getUserId().toString()).getEntities().stream().findFirst();
        if (userGridEntity.isPresent()) {
            UserEntity userEntity = userGridEntity.get();
            final Optional<UserEntity> userESEntity = Optional.ofNullable(userElasticsearchRepository.findOne(event.getUserId().toString()));
            if (userESEntity.isPresent()) {
                userEntity = UserEntity.builder().from(userEntity).withSkills(userESEntity.get().getSkills()).build();
            }
            userElasticsearchRepository.save(userEntity);
            LOGGER.info(String.format("Finished to synchronize user %s to Elasticsearch", event.getUserId()));
        }
    }
}
