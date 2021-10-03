package asia.cmg.f8.notification.push;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;

import asia.cmg.f8.notification.dto.BasicUserInfo;

/**
 * Created by nhieu on 8/9/17.
 */
public class PushContentMessage extends PushMessage {

	final private String message;
	final private String title;

	public PushContentMessage(final String message, final String type) {
		super(type);
		this.message = message;
		this.title = StringUtils.EMPTY;
	}
	
	public PushContentMessage(final String message, final String type, final String title) {
		super(type);
		this.message = message;
		this.title = title;
	}

	@Override
	public String getDefaultMessage() {
		return message;
	}

	@Override
	public String getDefaultTitle() {
		return title;
	}

	public void setSenderId(final String senderUuid, final Optional<BasicUserInfo> userInfo) {
		if (getCustomData() == null) {
			final Map<String, Object> customData = new HashMap<String, Object>();
			customData.put("senderId", senderUuid);

			if (userInfo.isPresent()) {
				customData.put("name", userInfo.get().getName());
				customData.put("picture", userInfo.get().getPicture());
			}

			setCustomData(customData);
		} else {
			getCustomData().put("senderId", senderUuid);
			if (userInfo.isPresent()) {
				getCustomData().put("name", userInfo.get().getName());
				getCustomData().put("picture", userInfo.get().getPicture());
			}
		}

	}

}
