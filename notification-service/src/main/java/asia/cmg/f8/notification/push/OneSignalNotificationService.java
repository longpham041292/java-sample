package asia.cmg.f8.notification.push;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.currencyfair.onesignal.OneSignal;
import com.currencyfair.onesignal.model.notification.CreateNotificationResponse;
import com.currencyfair.onesignal.model.notification.NotificationRequest;

public class OneSignalNotificationService {

	private String APP_ID;
	private String API_KEY;
	private static OneSignal oneSignalProvider = null;

	public OneSignalNotificationService(final String appId, final String apiKey) {
		this.APP_ID = appId;
		this.API_KEY = apiKey;
		if (oneSignalProvider == null) {
			oneSignalProvider = OneSignal.getInstance();
		}
	}

	public CreateNotificationResponse sendToSegments(final List<String> segments, final String heading, final String content,
													final Map<String, String> data, final String language) {
		
		NotificationRequest notificationRequest = this.buildNotifRequestSendToSegments(segments, heading, content, data, language);
		
		return oneSignalProvider.createNotification(API_KEY, notificationRequest);
	}
	
	public CreateNotificationResponse sendToDevices(final List<String> devices, final String heading, final String content,
													final Map<String, String> data, final String language) {

		NotificationRequest notificationRequest = this.buildNotifRequestSendToDevices(devices, heading, content, data, language);
		
		return oneSignalProvider.createNotification(API_KEY, notificationRequest);
	}
	
	private NotificationRequest buildNotifRequestSendToSegments(final List<String> segments, final String heading, final String content,
											final Map<String, String> data, final String language) {
		NotificationRequest notifRequest = new NotificationRequest();

		notifRequest.setAppId(APP_ID);
		notifRequest.setIncludedSegments(segments);
		notifRequest.setContents(Collections.singletonMap(language, content));
		if (data != null) {
			notifRequest.setData(data);
		}
		if (StringUtils.isNotEmpty(heading)) {
			notifRequest.setHeadings(Collections.singletonMap(language, heading));
		}

		return notifRequest;
	}

	private NotificationRequest buildNotifRequestSendToDevices(final List<String> devices, final String heading, final String content,
															final Map<String, String> data, final String language) {
		
		NotificationRequest notifRequest = new NotificationRequest();

		notifRequest.setAppId(APP_ID);
		notifRequest.setIncludeExternalUserIds(devices);
		notifRequest.setContents(Collections.singletonMap(language, content));
		if (data != null) {
			notifRequest.setData(data);
		}
		if (StringUtils.isNotEmpty(heading)) {
			notifRequest.setHeadings(Collections.singletonMap(language, heading));
		}

		return notifRequest;
	}

}
