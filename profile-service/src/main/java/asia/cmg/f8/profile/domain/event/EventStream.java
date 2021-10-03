package asia.cmg.f8.profile.domain.event;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * Created by tri.bui on 10/21/16.
 */
public interface EventStream {

    String ANSWER_OUTPUT_EVENT_CHANNEL = "answerOutput";
    String ANSWER_INPUTUG_EVENT_CHANNEL = "answerInputUG";
    String ANSWER_INPUTES_EVENT_CHANNEL = "answerInputES";
    String ANSWER_INPUTDB_EVENT_CHANNEL = "answerInputDB";

    String COMPLETE_PROFILE_CHANNEL = "completeProfileOut";
    String COMPLETE_PROFILE_IN_CHANNEL = "completeProfileIn";

    String USER_INFO_IN_CHANNEL = "changeUser";
    String USER_INFO_OUT_CHANNEL = "changeUserOut";

    String FOLLOWING_OUT_CHANNEL = "followingOut";
    String FOLLOWING_IN_CHANNEL = "followingIn";

    String ORDER_COMPLETED_INPUT_EVENT_CHANNEL = "orderCompletedInput";

    @Output(ANSWER_OUTPUT_EVENT_CHANNEL)
    MessageChannel answerEvents();

    @Input(ANSWER_INPUTUG_EVENT_CHANNEL)
    SubscribableChannel storeAnswerEvent();
    
    @Input(ANSWER_INPUTDB_EVENT_CHANNEL)
    SubscribableChannel storeAnswerToDBEvent();

    @Input(ANSWER_INPUTES_EVENT_CHANNEL)
    SubscribableChannel indexAnswerEvent();

    // Complete Profile Event
    @Output(COMPLETE_PROFILE_CHANNEL)
    MessageChannel completeProfile();

    @Input(COMPLETE_PROFILE_IN_CHANNEL)
    SubscribableChannel handleCompleteProfile();

    @Output(USER_INFO_OUT_CHANNEL)
    MessageChannel changeUserInfo();

    @Input(USER_INFO_IN_CHANNEL)
    SubscribableChannel handleChangeUserInfo();

    //Following connection
    @Output(FOLLOWING_OUT_CHANNEL)
    MessageChannel changeFollowingEvent();

    @Input(FOLLOWING_IN_CHANNEL)
    SubscribableChannel handleChangeFollowingConnection();

    @Input(ORDER_COMPLETED_INPUT_EVENT_CHANNEL)
    SubscribableChannel handleCreateContractConnection();

}
