package asia.cmg.f8.notification.push;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;

import com.currencyfair.onesignal.model.notification.CreateNotificationResponse;

import asia.cmg.f8.common.profile.FollowingConnectionEvent;
import asia.cmg.f8.common.spec.social.FollowingAction;
import asia.cmg.f8.notification.config.NotificationProperties;
import asia.cmg.f8.notification.dto.BasicUserInfo;
import asia.cmg.f8.notification.dto.ChatMessageType;
import asia.cmg.f8.notification.dto.NotificationRequest;
import asia.cmg.f8.notification.dto.SocialUserInfo;
import asia.cmg.f8.notification.enumeration.ENotificationEventName;
import asia.cmg.f8.notification.util.CommonStringUtils;
import asia.cmg.f8.social.CommentPostEvent;
import asia.cmg.f8.social.LikeCommentEvent;
import asia.cmg.f8.social.LikePostEvent;
import asia.cmg.f8.social.SocialPostCreatedEvent;

/**
 * Created on 1/9/17.
 */
@Component
@EnableBinding(NotificationEventStream.class)
public class SocialNotifier extends NotificationSender {

	private static final String USER_UUID = "user_uuid";
	private static final String USER_TYPE = "user_type";
	private static final String COMMON_MESSAGE = "%s just shared a new post: %s";
	private static final String COMMON_MESSAGE_EMPTY_CONTENT = "%s just shared a new post.";
	private static final String POST_TYPE = "post";
	private static final String PROFILE_TYPE = "profile";

	private static final String MESSAGE_SOCIAL_FOLLOW_MSG = "message.social.follow";
	private static final String MESSAGE_SOCIAL_COMMENT_LIKE_MSG = "message.social.comment.like";
    private static final String MESSAGE_SOCIAL_COMMENT_MSG = "message.social.comment";
	private static final String MESSAGE_SOCIAL_POST_LIKE_MSG = "message.social.post.like";
	private static final String MESSAGE_SOCIAL_POST_TAGGED = "message.social.post.tagged";
	private static final String MESSAGE_SOCIAL_POST_PHOTO_TAGGED = "message.social.post.photo.tagged";
	private static final String MESSAGE_SOCIAL_COMMENT_TAGGED = "message.social.comment.tagged";
	
	private static final Logger LOG = LoggerFactory.getLogger(SocialNotifier.class);
	private static final SocialUserInfo NO_AVATAR = null;
	private static final List<SocialUserInfo> NO_TAGGED_ACCOUNTS = Collections.emptyList();
	
	@Autowired
	@Qualifier("likePostEventConverter")
	private MessageConverter likePostConverter;

	@Autowired
	@Qualifier("commentPostEventConverter")
	private MessageConverter commentPostConverter;

	@Autowired
	@Qualifier("likeCommentEventConverter")
	private MessageConverter likeCommentConverter;

	@Autowired
	@Qualifier("userFollowing")
	private MessageConverter userFollowingConverter;

	@Autowired
	@Qualifier("userCreatedPostEventConverter")
	private MessageConverter userCreatedPostConverter;

	@Autowired
	private NotificationProperties notificationProperties;

	@StreamListener(NotificationEventStream.USER_LIKE_POST_INPUT_CHANNEL)
	public void handleLikedPostEvent(final Message message) {
		final LikePostEvent event = (LikePostEvent) likePostConverter.fromMessage(message, LikePostEvent.class);

		final CharSequence postOwnerUuid = event.getPostOwnerUuid();
		final CharSequence likerUuid = event.getUserUuid();
		if (event.getCountValue().equals(1) && !likerUuid.equals(postOwnerUuid)) {

			find(String.valueOf(likerUuid)).ifPresent(userLikePost -> {

				final String postId = String.valueOf(event.getPostId());
				final PushMessage msg = buildMessage(postId, MESSAGE_SOCIAL_POST_LIKE_MSG, CommonStringUtils.formatNameInNotification(userLikePost.getName()), event.getPostType());
				List<SocialUserInfo> tagged_accounts = new ArrayList<SocialUserInfo>();
				tagged_accounts.add(new SocialUserInfo(userLikePost.getUuid(), userLikePost.getName()));
				SocialUserInfo avatar = new SocialUserInfo(userLikePost.getUuid(), userLikePost.getName(), userLikePost.getPicture());
				
				sendToUser(String.valueOf(postOwnerUuid), msg, tagged_accounts, avatar, ENotificationEventName.LIKE_POST.name());
				
				LOG.info("Push user {} likes {} of ownerId {} notification ", likerUuid, event.getPostType(),
						postOwnerUuid);

			});
		}
	}

	@StreamListener(NotificationEventStream.USER_COMMENT_POST_INPUT_CHANNEL)
	public void handleCommentPostEvent(final Message message) {
		
		final CommentPostEvent commentPostEvent = (CommentPostEvent) commentPostConverter.fromMessage(message,
				CommentPostEvent.class);
		final String postOwnerUuid = String.valueOf(commentPostEvent.getPostOwnerUuid());
		List<SocialUserInfo> tagged_accounts = new ArrayList<SocialUserInfo>();
		Optional<BasicUserInfo> commentedUser = find(String.valueOf(commentPostEvent.getUserUuid()));
		SocialUserInfo avatar = new SocialUserInfo();
		
		// Send notification for post owner
		if (commentPostEvent.getCountValue().equals(1)
				&& !commentPostEvent.getUserUuid().equals(commentPostEvent.getPostOwnerUuid())) {

				commentedUser.ifPresent(userComment -> {
					final String postId = String.valueOf(commentPostEvent.getPostId());
					final PushMessage msg = buildMessage(postId, MESSAGE_SOCIAL_COMMENT_MSG, CommonStringUtils.formatNameInNotification(userComment.getName()));
					LOG.info("Push notification user {} commented post {}", userComment.getName(), commentPostEvent.getPostId());
					tagged_accounts.add(new SocialUserInfo(userComment.getUuid(), userComment.getName()));
					avatar.setUuid(userComment.getUuid());
					avatar.setName(userComment.getName());
					avatar.setAvatar(userComment.getPicture());
					
					sendToUser(postOwnerUuid, msg, tagged_accounts, avatar, ENotificationEventName.COMMENT_POST.name());
				});
		}
		
		// Send notification for tagged users
		if (!CollectionUtils.isEmpty(commentPostEvent.getTaggedUuids())) {
			tagged_accounts.clear();
			final String postId = String.valueOf(commentPostEvent.getPostId());
			final PushMessage pushTaggedMessage = buildMessage(postId, MESSAGE_SOCIAL_COMMENT_TAGGED, CommonStringUtils.formatNameInNotification(commentPostEvent.getName().toString()));
			
			commentedUser.ifPresent(userComment -> {
				tagged_accounts.add(new SocialUserInfo(userComment.getUuid(), userComment.getName()));
				avatar.setUuid(userComment.getUuid());
				avatar.setName(userComment.getName());
				avatar.setAvatar(userComment.getPicture());
			});
			
			commentPostEvent.getTaggedUuids().forEach(item -> {
				sendToUser(item.toString(), pushTaggedMessage, tagged_accounts, avatar, ENotificationEventName.COMMENT_POST.name());	
			});
		}
	}

	@StreamListener(NotificationEventStream.USER_LIKE_COMMENT_INPUT_CHANNEL)
	public void handleLikeCommentEvent(final Message message) {
		final LikeCommentEvent likeCommentEvent = (LikeCommentEvent) likeCommentConverter.fromMessage(message,
				LikeCommentEvent.class);
		final CharSequence likeCommentEventUserUuid = likeCommentEvent.getUserUuid();
		final CharSequence postOwnerUuid = likeCommentEvent.getPostOwnerUuid();
		final CharSequence commentOwnerUuid = likeCommentEvent.getCommentOwnerUuid();
		if (likeCommentEvent.getCountValue().equals(1) && !likeCommentEventUserUuid.equals(commentOwnerUuid)) {

			find(String.valueOf(likeCommentEventUserUuid)).ifPresent(userLikeComment -> {

				find(String.valueOf(postOwnerUuid)).ifPresent(postUser -> {
					final String postId = String.valueOf(likeCommentEvent.getPostUuid());
					final PushMessage msg = buildMessage(postId, MESSAGE_SOCIAL_COMMENT_LIKE_MSG,
														CommonStringUtils.formatNameInNotification(userLikeComment.getName()), 
														CommonStringUtils.formatNameInNotification(postUser.getName()));

					List<SocialUserInfo> tagged_accounts = new ArrayList<SocialUserInfo>();
					tagged_accounts.add(new SocialUserInfo(userLikeComment.getUuid(), userLikeComment.getName()));
					tagged_accounts.add(new SocialUserInfo(postUser.getUuid(), postUser.getName()));
					SocialUserInfo avatar = new SocialUserInfo(userLikeComment.getUuid(), userLikeComment.getName(), userLikeComment.getPicture());
					
					sendToUser(String.valueOf(commentOwnerUuid), msg, tagged_accounts, avatar, ENotificationEventName.LIKE_COMMENT.name());

					LOG.info("push user {} like comment {} of post {}", commentOwnerUuid,
							likeCommentEvent.getCommentId(), likeCommentEvent.getPostUuid());
				});
			});
		}
	}

	@StreamListener(NotificationEventStream.USER_FOLLOWING_INPUT_CHANNEL)
	public void handleFollowingEvent(final Message message) {
		final FollowingConnectionEvent event = (FollowingConnectionEvent) userFollowingConverter.fromMessage(message,
				FollowingConnectionEvent.class);

		if (FollowingAction.FOLLOW.toString().equals(event.getAction().toString())
				&& !event.getFollowerId().equals(event.getUserId())) {

			final String userFollowedId = String.valueOf(event.getUserId());
			final String userFollowerId = String.valueOf(event.getFollowerId());

			find(userFollowedId).ifPresent(userFollowed -> {
				final Map<String, Object> data = new HashMap<>();
				data.put(USER_UUID, userFollowedId);
				data.put(USER_TYPE, userFollowed.getUserType());

				final PushMessage pushMessage = new PushMessage(PROFILE_TYPE);
				pushMessage.setCustomData(data);
				pushMessage.setLocalizedMessage(MESSAGE_SOCIAL_FOLLOW_MSG, CommonStringUtils.formatNameInNotification(userFollowed.getName()));

				List<SocialUserInfo> tagged_accounts = new ArrayList<SocialUserInfo>();
				tagged_accounts.add(new SocialUserInfo(userFollowed.getUuid(), userFollowed.getName()));
				SocialUserInfo avatar = new SocialUserInfo(userFollowed.getUuid(), userFollowed.getName(), userFollowed.getPicture());
				
				sendToUser(userFollowerId, pushMessage, tagged_accounts, avatar, ENotificationEventName.FOLLOW.name());
				
				LOG.info("Push following notification of user {}", userFollowerId);
			});
		}
	}

	private PushMessage buildMessage(final String postId, final String message, final Object... args) {
		final PushMessage pushMessage = new PushMessage(POST_TYPE);
		pushMessage.setLocalizedMessage(message, args);
		pushMessage.setCustomData(Collections.singletonMap("post_uuid", postId));
		return pushMessage;
	}

	@StreamListener(NotificationEventStream.USER_POST_STATUS_INPUT_CHANNEL)
	public void handleSocialPostNotifications(final Message message) {
		
		List<SocialUserInfo> tagged_accounts = new ArrayList<SocialUserInfo>();
		final SocialPostCreatedEvent socialPostEvent = (SocialPostCreatedEvent) userCreatedPostConverter
				.fromMessage(message, SocialPostCreatedEvent.class);
		LOG.info("handleSocialPostNotifications {}", socialPostEvent);
		final String[] f8PtUuids = notificationProperties.getF8UserUuids();
		CharSequence userCreatedPostUuid = socialPostEvent.getUserUuid();
		SocialUserInfo avatar = new SocialUserInfo();
		
		if (f8PtUuids != null && socialPostEvent.getUserUuid() != null && ArrayUtils.contains(f8PtUuids, socialPostEvent.getUserUuid().toString())) {
			LOG.info("Push f8 pts notification {}", message);
			String shortContent = StringUtils.isEmpty(String.valueOf(socialPostEvent.getShortContent())) == true ? "" : socialPostEvent.getShortContent().toString();
			
			String notificationContent = "";
			if(StringUtils.isEmpty(shortContent)) {
				notificationContent = String.format(COMMON_MESSAGE_EMPTY_CONTENT, CommonStringUtils.formatNameInNotification(socialPostEvent.getName().toString()));
			} else {
				notificationContent = String.format(COMMON_MESSAGE, CommonStringUtils.formatNameInNotification(socialPostEvent.getName().toString()), shortContent);
			}
					
			final PushContentMessage chatPushMessage = new PushContentMessage(notificationContent, POST_TYPE);
			chatPushMessage.setCustomData(Collections.singletonMap("post_uuid", String.valueOf(socialPostEvent.getPostId())));
			
			// send notification to OneSignal
			String language = "en";
			List<String> segments = Arrays.asList(notificationProperties.getOneSignal().getAllSegment());
			Map<String, String> data = new HashMap<String, String>();
			data.put(PushMessage.CUSTOM_DATA_TYPE_KEY, ENotificationEventName.CREATE_POST.getType());
			data.put(PushMessage.CUSTOM_DATA_UUID_KEY, String.valueOf(socialPostEvent.getPostId()));
			sendToSegments(segments, notificationContent, data, language);
		}

		if (!CollectionUtils.isEmpty(socialPostEvent.getTaggedUuids())) {
			final PushMessage pushTaggedMessage = new PushMessage(POST_TYPE);
			pushTaggedMessage.setCustomData(Collections.singletonMap("post_uuid", String.valueOf(socialPostEvent.getPostId())));
			String messageKey = ChatMessageType.TEXT.toString().compareToIgnoreCase(socialPostEvent.getPostType().toString()) == 0 ? MESSAGE_SOCIAL_POST_TAGGED
																					: MESSAGE_SOCIAL_POST_PHOTO_TAGGED;
			pushTaggedMessage.setLocalizedMessage(messageKey, CommonStringUtils.formatNameInNotification(socialPostEvent.getName().toString()));

			tagged_accounts.clear();
			socialPostEvent.getTaggedUuids().forEach(item -> {
				find(socialPostEvent.getUserUuid()).ifPresent(postedUser -> {
					tagged_accounts.add(new SocialUserInfo(postedUser.getUuid(), postedUser.getName()));
					avatar.setUuid(postedUser.getUuid());
					avatar.setName(postedUser.getName());
					avatar.setAvatar(postedUser.getPicture());
				});
				
				sendToUser(item.toString(), pushTaggedMessage, tagged_accounts, avatar, ENotificationEventName.CREATE_POST.name());
			});
		}
	}

	public void sendNotifications(final NotificationRequest request) {
		LOG.info("send notificationRequest {}", request);
		final PushContentMessage chatPushMessage = new PushContentMessage(request.getDescription(), POST_TYPE, request.getTitle());
		String groupId = StringUtils.EMPTY;
		switch (request.getGroupType()) {
		case ALL:
			groupId = notificationProperties.getDefaultGroupId();
			break;
		case EU:
			groupId = notificationProperties.getEuGroupId();
			break;
		case PT:
			groupId = notificationProperties.getPtGroupId();
			break;
		default:
			 throw new IllegalArgumentException("GroupType is invalid");
		}
		sendToGroup(groupId, chatPushMessage, NO_TAGGED_ACCOUNTS, NO_AVATAR);
	}

}
