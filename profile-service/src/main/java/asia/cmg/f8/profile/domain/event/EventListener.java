package asia.cmg.f8.profile.domain.event;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;
import asia.cmg.f8.commerce.OrderCompletedEvent;
import asia.cmg.f8.common.profile.ChangeUserInfoEvent;
import asia.cmg.f8.common.profile.CompleteProfileEvent;
import asia.cmg.f8.common.profile.FollowingConnectionEvent;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.profile.AnswerSubmittedEvent;
import asia.cmg.f8.profile.OptionRecord;
import asia.cmg.f8.profile.domain.entity.AnswerEntity;
import asia.cmg.f8.profile.domain.repository.DistrictRepository;
import asia.cmg.f8.profile.domain.service.ContractService;
import asia.cmg.f8.profile.domain.service.CounterService;
import asia.cmg.f8.profile.domain.service.ProfileService;
import asia.cmg.f8.profile.domain.service.StoreAnswerService;
import asia.cmg.f8.profile.domain.service.UserDistrictLocationService;

/**
 * Created on 11/11/16.
 */
@Component
public class EventListener {

    @Autowired
    @Qualifier("completeProfileEventConverter")
    private MessageConverter completeProfileEventConverter;

    @Autowired
    @Qualifier("changeFollowingEventConverter")
    private MessageConverter changeFollowingEventConverter;

    @Autowired
    @Qualifier("orderCompletedEventConverter")
    private MessageConverter orderCompletedEventConverter;
    
    private final String QUESTION_DISTRICT_KEY = "District";
    private static final Logger LOG = LoggerFactory.getLogger(EventListener.class);

    private final StoreAnswerService storeAnswerService;
    private final ProfileService profileService;
    private final ContractService contractService;
    private final CounterService counterService;

    public EventListener(
            final StoreAnswerService storeAnswerService,
            final ProfileService profileService,
            final ContractService contractService,
            final CounterService counterService,
            final DistrictRepository districtRepo,
            final UserDistrictLocationService userLocationService) {
        this.storeAnswerService = storeAnswerService;
        this.profileService = profileService;
        this.contractService = contractService;
        this.counterService = counterService;
    }

    @StreamListener(EventStream.ANSWER_INPUTES_EVENT_CHANNEL)
    public void handleIndexAnswer(final AnswerSubmittedEvent message) {
        storeAnswerService.storeAnswerElastic(message);
    }
    
    @StreamListener(EventStream.ANSWER_INPUTDB_EVENT_CHANNEL)
    public void handleStoreAnswerToDatabase(final AnswerSubmittedEvent message) {
    	storeAnswerService.storeAnswerDatabase(message);
    }

    @StreamListener(EventStream.ANSWER_INPUTUG_EVENT_CHANNEL)
    public void handleStoreAnswer(final AnswerSubmittedEvent message) {
        final AnswerEntity answerEntity = new AnswerEntity();
        Set<String> options = message.getOptionKeys().stream()
						        .filter(OptionRecord::getChoose)
						        .map(option -> option.getOption().toString())
						        .collect(Collectors.toSet());
        String questionKey = message.getQuestionId().toString();
        
        answerEntity.setQuestionId(questionKey);
        answerEntity.setOwner(message.getUserId().toString());
        answerEntity.setUserType(UserType.valueOf(message.getUserType().toString().toUpperCase()));
        answerEntity.setEventId(message.getEventId().toString());
        answerEntity.setUuid(message.getAnswerId().toString());
        answerEntity.setOptionKeys(options);
        
        storeAnswerService.storeAnswerUserGrid(answerEntity);
    }

    @StreamListener(EventStream.COMPLETE_PROFILE_IN_CHANNEL)
    public void handleCompleteProfile(final Message message) {
        profileService
                .updateUserProfileComplete((CompleteProfileEvent) completeProfileEventConverter
                        .fromMessage(message, CompleteProfileEvent.class));

    }

    @StreamListener(EventStream.FOLLOWING_IN_CHANNEL)
    public void handleFollowingConnection(final Message message) {
        final FollowingConnectionEvent followingConnectionEvent = (FollowingConnectionEvent) changeFollowingEventConverter.fromMessage(message, FollowingConnectionEvent.class);
        counterService.updateFollowersNumber(followingConnectionEvent);
    }

    @StreamListener(EventStream.ORDER_COMPLETED_INPUT_EVENT_CHANNEL)
    public void handleCreateContractConnection(final Message message) {
        final OrderCompletedEvent contractConnectionEvent =
                (OrderCompletedEvent) orderCompletedEventConverter.fromMessage(message, ChangeUserInfoEvent.class);

        contractService.createContractConnection(contractConnectionEvent);
    }
}
