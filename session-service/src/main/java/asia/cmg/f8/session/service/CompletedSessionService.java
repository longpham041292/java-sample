package asia.cmg.f8.session.service;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.spec.session.SessionStatus;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.session.client.CompletedSessionClient;
import asia.cmg.f8.session.client.UserClient;
import asia.cmg.f8.session.dto.CompletedSessionResponse;
import asia.cmg.f8.session.dto.UserEntity;
import asia.cmg.f8.session.entity.CompletedSessionEntity;
import asia.cmg.f8.session.entity.SessionEntity;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created on 12/7/16.
 */
@Service
public class CompletedSessionService {

    private static final String QUERY_COMPLETED_SESSION_EU
            = "select * " +
              "where end_user_id = '%s' " + 
              		"and session_status = '%s' " +
              		"and session_date <= %s " +
              "order by session_date desc";
    private static final String QUERY_COMPLETED_SESSION_PT
            = "select * " +
              "where trainer_id = '%s' " +
              		"and session_status = '%s' " +
              		"and session_date <= %s " +
              "order by session_date desc";
    private static final String QUERY_COMPLETED_SESSION_BY_SESSION_ID
    		= "select * where session_id = %s";

    private static final String QUERY_USER_INFO = "select picture, name, username where %s";

    private final CompletedSessionClient completedSessionClient;

    private final UserClient userClient;
    
    @Autowired
    SessionService sessionService;

    private Logger logger = LoggerFactory.getLogger(CompletedSessionService.class);
    
    public CompletedSessionService(final CompletedSessionClient completedSessionClient,
                                   final UserClient userClient) {
        this.completedSessionClient = completedSessionClient;
        this.userClient = userClient;
    }

//    public List<CompletedSessionResponse> getCompletedSessionsEuNotRated(final Account account) {
//
//        final long startTime =  ZoneDateTimeUtils.convertToSecondUTC(LocalDateTime.now());
//
//        QUERY_COMPLETED_SESSION_EU = String.format(QUERY_COMPLETED_SESSION_EU,
//									                account.uuid(),
//									                SessionStatus.COMPLETED,
//									                startTime);
//        final List<CompletedSessionEntity> completedSessionEntityList =
//                completedSessionClient.getAllCompletedSessionsNotRated(QUERY_COMPLETED_SESSION_EU).getEntities();
//       
//        if (Objects.isNull(completedSessionEntityList) || completedSessionEntityList.isEmpty()) {
//            return Collections.emptyList();
//        }
//       
//        final String listTrainerId = completedSessionEntityList
//                .stream()
//                .map(entity -> "uuid=" + entity.getTrainerId())
//                .collect(Collectors.joining(" or "));
//        final Map<String, UserEntity> listUserInfo =
//                userClient.getUserByQuery(String.format(QUERY_USER_INFO, listTrainerId))
//                        .getEntities()
//                        .stream()
//                        .collect(Collectors.toMap(UserEntity::getUuid, userEntity -> userEntity));
//
//        return completedSessionEntityList.stream()
//                .map(entity -> {
//
//                    final String trainerId = entity.getTrainerId();
//
//                    final UserEntity trainer = listUserInfo.get(trainerId);
//                    final String picture;
//                    final String name;
//                    if (trainer == null) {
//                        picture = StringUtils.EMPTY;
//                        name = StringUtils.EMPTY;
//                    } else {
//                        picture = Optional.ofNullable(trainer.getPicture()).filter(StringUtils::isNotEmpty).orElse(StringUtils.EMPTY);
//                        name = Optional.ofNullable(trainer.getName()).filter(StringUtils::isNotEmpty).orElse(StringUtils.EMPTY);
//                    }
//
//                    return CompletedSessionResponse.builder()
//                            .partnerPicture(picture)
//                            .partnerFullName(name)
//                            .sessionDate(entity.getSessionDate())
//                            .sessionId(entity.getSessionId())
//                            .partnerId(trainerId)
//                            .build();
//                })
//                .collect(Collectors.toList());
//    }
//
//    public List<CompletedSessionResponse> getCompletedSessionsPtNotRated(final Account account) {
//
//        final long startTime =  ZoneDateTimeUtils.convertToSecondUTC(LocalDateTime.now());
//
//        final List<CompletedSessionEntity> completedSessionEntityList =
//                completedSessionClient
//                        .getAllCompletedSessionsNotRated(
//                                String.format(QUERY_COMPLETED_SESSION_PT,
//                                        account.uuid(),
//                                        SessionStatus.COMPLETED,
//                                        startTime))
//                        .getEntities();
//        
//      if (Objects.isNull(completedSessionEntityList) || completedSessionEntityList.isEmpty()) {
//            return Collections.emptyList();
//        }
//       
//        final String listEndUserId = completedSessionEntityList
//                .stream()
//                .map(entity -> "uuid=" + entity.getEndUserId())
//                .collect(Collectors.joining(" or "));
//
//        final Map<String, UserEntity> listUserInfo =
//                userClient.getUserByQuery(String.format(QUERY_USER_INFO, listEndUserId))
//                        .getEntities()
//                        .stream()
//                        .collect(Collectors.toMap(UserEntity::getUuid, userEntity -> userEntity));
//
//        return completedSessionEntityList.stream()
//                .map(entity -> {
//                    final String endUserId = entity.getEndUserId();
//                    final String picture = StringUtils.isNotEmpty(listUserInfo.get(endUserId).getPicture())
//                            ? listUserInfo.get(endUserId).getPicture()
//                            : StringUtils.EMPTY;
//                    final String name = StringUtils.isNotEmpty(listUserInfo.get(endUserId).getName())
//                            ? listUserInfo.get(endUserId).getName()
//                            : StringUtils.EMPTY;
//                    return CompletedSessionResponse.builder()
//                            .partnerPicture(picture)
//                            .partnerFullName(name)
//                            .sessionDate(entity.getSessionDate())
//                            .sessionId(entity.getSessionId())
//                            .partnerId(endUserId)
//                            .build();
//                })
//                .collect(Collectors.toList());
//    }
    
    public CompletedSessionResponse getLatestCompletedSessionPtNotRated(final Account account) {

    	try {
    		final long currentTimeInSecond =  ZoneDateTimeUtils.convertToSecondUTC(LocalDateTime.now());
	        final long beforeCurrentTimeInOneHour = currentTimeInSecond - 3600;
    		int limit = 1;

            final List<CompletedSessionEntity> completedSessionEntityList =
                    completedSessionClient.getCompletedSessionsByQueryAndLimit(
    				                                		String.format(QUERY_COMPLETED_SESSION_PT, account.uuid(), SessionStatus.COMPLETED, beforeCurrentTimeInOneHour), 
    				                                		limit).getEntities();
            if (Objects.isNull(completedSessionEntityList) || completedSessionEntityList.isEmpty()) {
            	logger.info("[getLatestCompletedSessionEuNotRated] Have no any not rated completed session of trainer {}", account.uuid());
                return null;
            }
            
            CompletedSessionEntity entity = completedSessionEntityList.get(0);
            
            if(entity.isTrainerRated() == false) {
            	String endUserId = entity.getEndUserId();
                UserEntity userInfo = userClient.getUserByQuery(String.format(QUERY_USER_INFO, "uuid = " + endUserId)).getEntities().get(0);
                SessionEntity sessionEntity = sessionService.findOne(entity.getSessionId());
                String picture = StringUtils.isNotEmpty(userInfo.getPicture()) ? userInfo.getPicture() : StringUtils.EMPTY;
                String name = StringUtils.isNotEmpty(userInfo.getName()) ? userInfo.getName() : StringUtils.EMPTY;
                String username = StringUtils.isNotEmpty(userInfo.getUsername()) ? userInfo.getUsername() : StringUtils.EMPTY;
                
                String locationName = sessionEntity.getCheckinClubName();
                if(StringUtils.isEmpty(locationName)) {
                	locationName = sessionEntity.getBookingClubName();
                }
                
                String locationAddress = sessionEntity.getCheckinClubAddress();
                if(StringUtils.isEmpty(locationAddress)) {
                	locationAddress = sessionEntity.getBookingClubAddress();
                }
                
                return CompletedSessionResponse.builder()
                		.partnerUsername(username)
                        .partnerPicture(picture)
                        .partnerFullName(name)
                        .sessionDate(entity.getSessionDate())
                        .sessionId(entity.getSessionId())
                        .partnerId(endUserId)
                        .build();
            } else {
            	logger.error("[getLatestCompletedSessionPtNotRated] The latest completed session already rated by PT of entity id [{}]", entity.getId());
				return null;
			}
            
		} catch (Exception e) {
			logger.error("[getLatestCompletedSessionPtNotRated][error: {}]", e.getMessage());
			return null;
		}
    }
    
    public CompletedSessionResponse getLatestCompletedSessionEuNotRated(final Account account) {
    	try {
	        final long currentTimeInSecond =  ZoneDateTimeUtils.convertToSecondUTC(LocalDateTime.now());
	        final long beforeCurrentTimeInOneHour = currentTimeInSecond - 3600;
	        final int limit = 1;
	        
	        final List<CompletedSessionEntity> completedSessionEntityList =
	                completedSessionClient
				                        .getCompletedSessionsByQueryAndLimit(String.format(QUERY_COMPLETED_SESSION_EU, account.uuid(), SessionStatus.COMPLETED, beforeCurrentTimeInOneHour),
				                        									limit).getEntities();
	       
	        if (Objects.isNull(completedSessionEntityList) || completedSessionEntityList.isEmpty()) {
	        	logger.info("[getLatestCompletedSessionEuNotRated] Have no any not rated completed session of user {}", account.uuid());
	            return null;
	        }
	        
	        CompletedSessionEntity entity = completedSessionEntityList.get(0);
	        
	        if(entity.isEndUserRated() == false) {
	        	String ptId = entity.getTrainerId();
	            UserEntity userInfo = userClient.getUserByQuery(String.format(QUERY_USER_INFO, "uuid = " + ptId)).getEntities().get(0);
	            SessionEntity sessionEntity = sessionService.findOne(entity.getSessionId());
	            
	            String picture = StringUtils.isNotEmpty(userInfo.getPicture()) ? userInfo.getPicture() : StringUtils.EMPTY;
	            String name = StringUtils.isNotEmpty(userInfo.getName()) ? userInfo.getName() : StringUtils.EMPTY;
	            String username = StringUtils.isNotEmpty(userInfo.getUsername()) ? userInfo.getUsername() : StringUtils.EMPTY;
	            
	            String locationName = sessionEntity.getCheckinClubName();
	            if(StringUtils.isEmpty(locationName)) {
	            	locationName = sessionEntity.getBookingClubName();
	            }
	            
	            String locationAddress = sessionEntity.getCheckinClubAddress();
	            if(StringUtils.isEmpty(locationAddress)) {
	            	locationAddress = sessionEntity.getBookingClubAddress();
	            }
	            
	            return CompletedSessionResponse.builder()
	            		.partnerUsername(username)
	                    .partnerPicture(picture)
	                    .partnerFullName(name)
	                    .sessionDate(entity.getSessionDate())
	                    .sessionId(entity.getSessionId())
	                    .partnerId(ptId)
	                    .locationName(locationName)
	                    .locationAddress(locationAddress)
	                    .build();
	        } else {
	        	logger.error("[getLatestCompletedSessionEuNotRated] The latest completed session already rated by EU of entity id [{}]", entity.getId());
				return null;
			}
            
    	} catch (Exception e) {
    		logger.error("[getLatestCompletedSessionEuNotRated][error: {}]", e.getMessage());
    		return null;
		}
    }
    
    public void skipRatingCompletedSession(final String sessionUuid, final Account account) throws Exception {
    	
    	try {
    		final int LIMIT = 1;
			UserGridResponse<CompletedSessionEntity> response = completedSessionClient.getCompletedSessionsByQueryAndLimit(
					String.format(QUERY_COMPLETED_SESSION_BY_SESSION_ID, sessionUuid), LIMIT);
			if(Objects.isNull(response) || response.getEntities().isEmpty()) {
				throw new Exception("Completed session uuid not existed");
			}
			CompletedSessionEntity entity = response.getEntities().get(0);
			if(account.isEu()) {
				entity = entity.withIsEndUserRated(Boolean.TRUE);
			} else if(account.isPt()) {
				entity = entity.withIsTrainerRated(Boolean.TRUE);
			}
			completedSessionClient.updateCompletedSession(entity.getId(), entity);
			
		} catch (Exception e) {
			logger.error("[skipRatingCompletedSession][error: {}]", e.getMessage());
			throw e;
		}
    }
}
 