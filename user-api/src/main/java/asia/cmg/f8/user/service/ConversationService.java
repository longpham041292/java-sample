package asia.cmg.f8.user.service;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.user.client.ConversationClient;
import asia.cmg.f8.user.client.UserClient;
import asia.cmg.f8.user.entity.PagedUserResponse;
import asia.cmg.f8.user.entity.UserEntity;
import asia.cmg.f8.user.notification.client.NotificationClientUserApi;
import asia.cmg.f8.user.notification.dto.NotificationResponseUserApi;
//import asia.cmg.f8.user.notification.entity.LastLoadNotificationTimeUserApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


/**
 * Created by on 11/8/16.
 */
@Component
public class ConversationService {
    private static final Logger LOG = LoggerFactory.getLogger(ConversationService.class);
    private final static String QUERY_EXIST_USER = "select uuid where uuid='%1$s' and (not uuid = '%2$s')";

    @Autowired
    private ConversationClient conversationClient;

    @Autowired
    private UserClient userClient;
    
    @Autowired
    private  NotificationClientUserApi notificationClient;
    
    public static final String TYPE = "type";
    protected static final String SESSION_TYPE = "chat";
    public static final String ANDROID_NOTIFICATION_PROP = "notification";
    public static final String IOS_NOTIFICATION_PROP = "aps";
    public static final String DATA = "data";
    public static final String MESSAGE= "message";

    public PagedUserResponse<UserEntity> getMyConversations(final Account account,
                                                            final String cursor,
                                                            final int limit) {
        return conversationClient.getConversationConnection(account.uuid() ,cursor, limit);
    }

    public Optional<UserEntity> createConversations(final Account account, final String partnerId) {
        final UserGridResponse<UserEntity> createdConversationResp = conversationClient
                .createConversationConnectionWith(account.uuid(), partnerId);
        if (Objects.isNull(createdConversationResp)
                || createdConversationResp.getEntities().isEmpty()) {
            LOG.error("user-grid went wrong while creating conversation connection with userID: {}", partnerId);
            return Optional.empty();
        }
        return createdConversationResp.getEntities().stream().findFirst();
    }

    public Optional<UserEntity> deleteConversation(final Account account, final String partnerId) {
        final UserGridResponse<UserEntity> deletedConversationResp = conversationClient
                .deleteConversationConnectionWith(account.uuid() ,partnerId);
        if (Objects.isNull(deletedConversationResp) || deletedConversationResp.getEntities().isEmpty()) {
            LOG.error("user-grid went wrong while deleting conversation connection with userID: {}", partnerId);
            return Optional.empty();
        }
        return deletedConversationResp.getEntities().stream().findFirst();
    }

    public boolean isNotValidUser(final String meUuid, final String partnerId) {
        return userClient.getUserByUUID(String.format(QUERY_EXIST_USER, partnerId, meUuid))
                .getEntities()
                .isEmpty();
    }
    
    /**
     * Load latest notifications of given user and device type.
     *
     * @param uuid  the user uuid
     * @param token the access token
     * @return list of sent notifications. It never return null.
     */
    public String loadLatestSentNotifications(final String uuid, final String receiverUuid, final String token) {
    	
   final String query = composeQuery(receiverUuid/*, lastLoadTime*/);
        LOG.info("Load notification by query {}", query);
      final UserGridResponse<NotificationResponseUserApi> notifications = notificationClient.getLatest(query, 1);
        if (notifications == null || notifications.getEntities() == null) {
            return null;
        }

       try{
        if( notifications.getEntities() != null && notifications.getEntities().get(0).getPayloads() != null  && notifications.getEntities().get(0).getPayloads().size() > 0){
        	LOG.info("Load notification by query {}", notifications.getEntities().get(0).getPayloads().size());
              return  getDataFromPayload(notifications.getEntities().get(0).getPayloads());
          }else {
           return null;
          }
        
    	  }catch (Exception e){
    		  LOG.error(" Last Msg ", e);	
    	   return null;
    	  }
    }
    
 
    
    @SuppressWarnings("PMD")
    private String composeQuery(final String uuid/*, final LastLoadNotificationTimeUserApi lastLoadTime*/) {

        final StringBuilder query = new StringBuilder("select * where receiver='" + uuid + "'");
        query.append(" order by created desc");
        return query.toString();
    }
    
   
    
    @SuppressWarnings("unchecked")
	private String getDataFromPayload(final Map<String, Object> payload) {
        
     Object message = null;
         
       final   Map<String, Object> apsvalue=(Map<String, Object>)((Map<String, Object>) payload.get(IOS_NOTIFICATION_PROP)).get(IOS_NOTIFICATION_PROP);
       final   Map<String, Object> datavalue=(Map<String, Object>)((Map<String, Object>) apsvalue).get(DATA);
      
       if(datavalue.get(TYPE) != null && datavalue.get(TYPE).equals(SESSION_TYPE)){
        message =datavalue.get(MESSAGE);  
       }
      return message != null ? message.toString().substring(message.toString().indexOf(":")+1):null;
      
    }
    
    
    
}
