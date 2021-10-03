/**
 * 
 */
package asia.cmg.f8.profile.domain.event;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * @author khoa.bui
 *
 */
public interface SubEventStream extends EventStream {
	String USER_UN_FOLLOWING_OUTPUT = "userUnFollowingOut";
	  
    @Output(USER_UN_FOLLOWING_OUTPUT)
    MessageChannel userUnFollowingInfo();
}
