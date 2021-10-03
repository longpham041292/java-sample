//package com.currencyfair.onesignal;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import com.currencyfair.onesignal.model.notification.CreateNotificationResponse;
//import com.currencyfair.onesignal.model.notification.NotificationRequest;
//import com.currencyfair.onesignal.model.player.ViewDeviceResponse;
//
//import asia.cmg.f8.notification.push.OneSignalNotificationService;
//
//public class TestOneSignal {
//	
//	private static final String APP_ID = "c3660167-6b33-4982-bf5d-c0f022503771";
//	private static final String API_KEY = "ZmMwOGZmMWItNjY4YS00YmQ2LTgwNDgtNjA0ZTBjMmFhMGZh";
//	private static OneSignal oneSignal = OneSignal.getInstance();
//	private static OneSignalNotificationService osnNotificationService = new OneSignalNotificationService(APP_ID, API_KEY);
//	
//	public static void main(String[] args) {
//		
////		Map<String, String> contents = new HashMap<String, String>();
////		List<String> players = new ArrayList<String>();
////		NotificationRequest notifRequest = new NotificationRequest();
////		
////		
////		contents.put("en", "This is the content");
////		players.add("383c1a16-f58c-4927-b5d3-0b64a44c8518");	// Steven
////		players.add("c4f9cf93-38af-475b-8412-3bb729e50fc2");	// Tan
////		players.add("413CD4CE-75DC-40D9-A8B1-5ED3D9C7117F");	// Thach's iphone
//		
////		notifRequest.setAppId(APP_ID);
////		notifRequest.setContents(contents);
////		notifRequest.setIncludeExternalUserIds(players);
////		notifRequest.setIncludedSegments(Arrays.asList("All"));
////		notifRequest.setHeadings(Collections.singletonMap("en", "This is the heading"));
////		notifRequest.setData(Collections.singletonMap("data", "This is my data"));
//		
//		// Test create notification
////		CreateNotificationResponse notifResp = createNotification(notifRequest);
////		System.out.println("Notification response: " + notifResp.toString());
//		
//		// Test view device info
////		ViewDeviceResponse deviceInfo = viewDevices("7f345baf-dc28-46c4-bd50-e6d457280514");
////		System.out.println("Device info: " + deviceInfo.toString());
//		
//		
//		CreateNotificationResponse notifResp = sentToSegment("All");
//		System.out.println("Notification response: " + notifResp.toString());
//	}
//	
//	private static CreateNotificationResponse sentToSegment(final String segmentName) {
//		List<String> segments = Arrays.asList(segmentName);
//		String heading = "This is the heading";
//		Map<String, String> data = Collections.singletonMap("data", "This is my data");
//		String content = "This is the content with data attachment";
//		String language = "en";
//		
//		return osnNotificationService.sendToSegments(segments, heading, content, data, language);
//	}
//	
//	private static ViewDeviceResponse viewDevices(final String deviceId) {
//		return oneSignal.viewDevice(API_KEY, APP_ID, deviceId);
//	}
//	
//	private static CreateNotificationResponse createNotification(NotificationRequest request) {
//		return oneSignal.createNotification(API_KEY, request);
//	}
//}
