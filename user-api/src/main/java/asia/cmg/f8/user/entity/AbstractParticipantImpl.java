package asia.cmg.f8.user.entity;

import asia.cmg.f8.common.spec.conversation.Participant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.immutables.value.Value;

import javax.annotation.Nullable;

/**
 * Created by tuong.le on 12/6/16.
 */
@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(typeImmutable = "*", visibility = Value.Style.ImplementationVisibility.PUBLIC, passAnnotations = {
		JsonIgnoreProperties.class, JsonInclude.class })
@SuppressWarnings({ "CheckReturnValue", "PMD" })
public abstract class AbstractParticipantImpl implements Participant {
	@Override
	@Nullable
	public abstract String getUuid();

	@Override
	@Nullable
	public abstract String getName();

	@Override
	@Nullable
	public abstract String getPicture();

	@Nullable
	public abstract String getLastMsg();

	@Nullable
	public abstract String getLastMsgOwner();
	
	@Nullable
	public abstract String getLastMsgReceiver();
	
	@Nullable
	public abstract String getLastMsgType();

	@Nullable
	public abstract Long getModified();

	@Nullable
	public abstract Long getLastMsgTime();

	@Nullable
	public abstract String getLastMsgId();
}
