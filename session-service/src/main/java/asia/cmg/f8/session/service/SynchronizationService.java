package asia.cmg.f8.session.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import asia.cmg.f8.common.profile.ChangeUserInfoEvent;
import asia.cmg.f8.common.spec.user.DocumentStatusType;
import asia.cmg.f8.common.spec.user.GenderType;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.common.user.UserActivationEvent;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.session.NewOrderCommerceEvent;
import asia.cmg.f8.session.client.UserClient;
import asia.cmg.f8.session.config.SessionProperties;
import asia.cmg.f8.session.dto.Profile;
import asia.cmg.f8.session.dto.UserEntity;
import asia.cmg.f8.session.entity.BasicUserEntity;
import asia.cmg.f8.session.entity.MemberUploadEntity;
import asia.cmg.f8.session.entity.SessionAction;
import asia.cmg.f8.session.entity.SessionEntity;
import asia.cmg.f8.session.entity.SessionHistoryEntity;
import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.entity.TrainerUploadEntity;
import asia.cmg.f8.session.entity.UserSkillsEntity;
import asia.cmg.f8.session.repository.BasicUserRepository;
import asia.cmg.f8.session.repository.MemberUploadRepository;
import asia.cmg.f8.session.repository.SessionRepository;
import asia.cmg.f8.session.repository.TrainerUploadRepository;
import asia.cmg.f8.session.repository.UserSkillsRepository;
import asia.cmg.f8.session.utils.TimeSlotUtil;

/**
 * Created on 11/30/16.
 */
@Service
@SuppressWarnings("PMD.ExcessiveImports")
public class SynchronizationService {
    private static final Logger LOG = LoggerFactory.getLogger(SynchronizationService.class);

    private final BasicUserRepository basicUserRepository;
    private final UserClient userClient;
    private final SessionManagementService sessionManagementService;
    private final SessionRepository sessionRepository;
    private final EventManagementService eventManagementService;
    private final SessionHistoryManagementService sessionHistoryManagementService;
    private final AvailabilityService availabilityService;
    private final SessionProperties sessionProperties;
    private static final int ACTIVATED = 1;
    private final MemberUploadRepository memberUploadRepository;
    private final TrainerUploadRepository trainerUploadRepository;
    private final MigrationUserService migrationUserService;
    
    @Autowired
    UserSkillsRepository userSkillsRepo;

    @Inject
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public SynchronizationService(final BasicUserRepository basicUserRepository,
            final UserClient userClient, final SessionManagementService sessionManagementService,
            final SessionRepository sessionRepository,
            final MemberUploadRepository memberUploadRepository,
            final TrainerUploadRepository trainerUploadRepository,
            final EventManagementService eventManagementService,
            final SessionHistoryManagementService sessionHistoryManagementService,
            final AvailabilityService availabilityService, final SessionProperties sessionProperties,
            final MigrationUserService migrationUserService) {
        this.basicUserRepository = basicUserRepository;
        this.userClient = userClient;
        this.sessionManagementService = sessionManagementService;
        this.sessionRepository = sessionRepository;
        this.memberUploadRepository = memberUploadRepository;
        this.trainerUploadRepository = trainerUploadRepository;
        this.eventManagementService = eventManagementService;
        this.sessionHistoryManagementService = sessionHistoryManagementService;
        this.availabilityService = availabilityService;
        this.sessionProperties = sessionProperties;
        this.migrationUserService = migrationUserService;
    }

    public void handleNewOrderCommerceEvent(final NewOrderCommerceEvent event) {
        LOG.info(
                "Processing syncing data for order materialized view after getting new order from Commerce service: {}",
                event.getOrderId().toString());

        createUser(event.getUserUuid().toString());
        createUser(event.getPtUuid().toString());
    }

	public void handleNewUserActivated(final UserActivationEvent event) {
		LOG.info("Consume user activate event {}", event.getUuid().toString());

		final String userId = String.valueOf(event.getUuid());
		final Optional<BasicUserEntity> user = basicUserRepository.findOneByUuid(userId);
		final BasicUserEntity basicUser = new BasicUserEntity();
		if (!user.isPresent()) {
			LOG.info("Create user: {}", userId);

			
			basicUser.setUuid(userId);
			basicUser.setFullName(String.valueOf(event.getName()));
			basicUser.setEmail(String.valueOf(event.getEmail()));
			basicUser.setAvatar(String.valueOf(event.getAvarta()));
			basicUser.setUserName(String.valueOf(event.getUserName()));
			basicUser.setActivated((ACTIVATED == event.getActivated())? true : false);
			basicUser.setUserType(String.valueOf(event.getUserType()).toLowerCase(Locale.US));
			basicUser.setJoinDate(LocalDateTime.ofInstant(
	                Instant.ofEpochMilli(event.getJoinedDate()), ZoneId.systemDefault()));
			basicUser.setUsercode((null == event.getUserCode())? null : String.valueOf(event.getUserCode()));
			basicUser.setEmailValidated((ACTIVATED == event.getEmailValidated())? true : false);
			basicUser.setClubcode((null == event.getClubCode())? null : String.valueOf(event.getClubCode()));
			DocumentStatusType docStatus = DocumentStatusType.ONBOARD;
			final String docStatusEvent = String.valueOf(event.getDocumentStatus());
			if(!StringUtils.isEmpty(docStatusEvent)) {
				if(DocumentStatusType.APPROVED.name().equalsIgnoreCase(docStatusEvent)) {
					docStatus = DocumentStatusType.APPROVED;
				} else if(DocumentStatusType.PENDING.name().equalsIgnoreCase(docStatusEvent)) {
					docStatus = DocumentStatusType.PENDING;
				}
			}
			basicUser.setDocStatus(docStatus);
			basicUser.setLevel((null == event.getLevel())? null : String.valueOf(event.getLevel()));
			basicUser.setPhone(event.getPhone() == null ? null : String.valueOf(event.getPhone()));
			basicUser.setGender(event.getGender());
			basicUser.setVerifyPhone(false);
			
			basicUserRepository.save(basicUser);
			LOG.info("-----------------------Create user with user code: {} and comparison {} ", event.getUserCode(), (null != event.getUserCode()));
			
			if(null != event.getUserCode()) {
				final String userCode = String.valueOf(event.getUserCode());
				final String userType = String.valueOf(event.getUserType()).toLowerCase();
				synchronizedContractForUser(userId, userCode, userType);
			}
		} else {
			LOG.info("Activated user: {}", userId);
			user.get().setActivated(true);
			user.get().setEmailValidated(event.getEmailValidated() == ACTIVATED);
			basicUserRepository.save(user.get());
		}
		
	}

    /**
     * Update user activate status, and document status
     *
     * @param event
     */
    public void updateUserInfo(final ChangeUserInfoEvent event) {
        final String userUuid = event.getUserId().toString();
        LOG.info("Updating change user info for user {}", userUuid);

        final String docStatus = (event.getDocumentStatus() == null) ? "" : event
                .getDocumentStatus().toString();

        final Optional<BasicUserEntity> userOpt = basicUserRepository.findOneByUuid(userUuid);
        if (!userOpt.isPresent()) {
            LOG.info("Could not found user info {} in session repository", userUuid);
            return;
        }
        //Import contract for user
        final String oldUserCode = userOpt.get().getUsercode() == null ? "" : userOpt.get().getUsercode().toString();
        final String newUserCode = event.getUsercode() == null ? "" : event.getUsercode().toString();
        LOG.info("Adding contract oldUserCode {} - newUserCode {}", oldUserCode, newUserCode);
        if(!StringUtils.isEmpty(newUserCode)		
        		&& !newUserCode.equalsIgnoreCase(oldUserCode)) {
			final String userType = String.valueOf(event.getUserType()).toLowerCase();
			synchronizedContractForUser(userUuid, newUserCode, userType);
			updateOldUserCodeStatus(oldUserCode);
        }
        updateAvailability(userOpt.get(), docStatus);

        final UserGridResponse<UserEntity> userGridUserEntity = userClient.getUser(userUuid);
        if (!Objects.isNull(userGridUserEntity) && !userGridUserEntity.getEntities().isEmpty()) {
            final UserEntity userEntity = userGridUserEntity.getEntities().get(0);
            final BasicUserEntity user = updateUserInfo(event, docStatus, userEntity, userOpt.get());
            
            // Delete current skills of current user in user_skills table
            userSkillsRepo.deleteByUserId(user.getId());
            LOG.info("Deleted current skills of user {}", userUuid);
            
            // Update user's new info
            final BasicUserEntity savedUser = basicUserRepository.save(user);

            // Cancel all pending and confirm sessions of deactivated user.
            if (!Objects.isNull(savedUser) && !savedUser.isActivated()) {
                cancelSessionsDeactivatedUser(userUuid, UserType.valueOf(savedUser.getUserType()));
            }

            LOG.info("Updated user info {}", userUuid);
            return;
        }
        LOG.info("Could not Update user info {}", userUuid);

    }

    private void updateAvailability(final BasicUserEntity basicUserEntity, final String docStatus) {
        // this checking makes sure that we'll only set PT's availability for
        // the first time APPROVED
        if (UserType.PT.name().equalsIgnoreCase(basicUserEntity.getUserType())
                && DocumentStatusType.APPROVED.name().equals(docStatus)
                && !DocumentStatusType.APPROVED.name()
                        .equals(basicUserEntity.getDocStatus().name())) {

            final int month = Integer.valueOf(sessionProperties.getNewPTMonthsAhead());
            final LocalDateTime targetTime = LocalDate.now().plusMonths(month).withDayOfMonth(1)
                    .minusDays(1).atTime(LocalTime.MAX);

            LocalDateTime currentTime = LocalDateTime.now().withSecond(0).withNano(0);
            currentTime = TimeSlotUtil.roundToSlotTime(currentTime);

            availabilityService.setupAvailablityForTrainer(basicUserEntity.getUuid(), currentTime,
                    targetTime);
        }
    }

    private BasicUserEntity createUser(final String userId) {
        final Optional<BasicUserEntity> user = basicUserRepository.findOneByUuid(userId);
        final UserGridResponse<UserEntity> responseUG = userClient.getUser(userId);
        if (!user.isPresent()) {
            LOG.info("Create user: {}", userId);
            
            if (!Objects.isNull(responseUG) && !responseUG.getEntities().isEmpty()) {
                final UserEntity userEntity = responseUG.getEntities().get(0);
                final BasicUserEntity basicUser = new BasicUserEntity();
                return storeUserIntoDB(userEntity, basicUser);
            } else {
                throw new ConstraintViolationException(String.format(
                        "User %s is not exist in the system", userId), Collections.emptySet());
            }
        }
        else {
        	
        	if (!Objects.isNull(responseUG) && !responseUG.getEntities().isEmpty()) {
                final UserEntity userEntity = responseUG.getEntities().get(0);
                final BasicUserEntity basicUser = user.get();
                return storeUserIntoDB(userEntity, basicUser);
            } else {
                throw new ConstraintViolationException(String.format(
                        "User %s is not exist in the system", userId), Collections.emptySet());
            }
        }

    }

    private BasicUserEntity storeUserIntoDB(final UserEntity usrGridEntity, final BasicUserEntity mysqlEntity) {
    	mysqlEntity.setUuid(usrGridEntity.getUuid());
    	mysqlEntity.setFullName(usrGridEntity.getName());
    	mysqlEntity.setEmail(usrGridEntity.getEmail());
    	mysqlEntity.setAvatar(usrGridEntity.getPicture());
    	mysqlEntity.setUserName(usrGridEntity.getUsername());
    	mysqlEntity.setActivated(usrGridEntity.getActivated());
    	mysqlEntity.setUserType(usrGridEntity.getUserType().name().toLowerCase(Locale.US));
    	mysqlEntity.setJoinDate(LocalDateTime.ofInstant(
                Instant.ofEpochMilli(usrGridEntity.getCreated()), ZoneId.systemDefault()));
        if (usrGridEntity.getStatus() != null) {
        	mysqlEntity.setDocStatus(usrGridEntity.getStatus().documentStatus());
        }

        final Profile profile = usrGridEntity.getProfile();
        if (!Objects.isNull(profile)) {
        	mysqlEntity.setCity(profile.getCity());
        	mysqlEntity.setCountry(profile.getCountry());
            mysqlEntity.setPhone(profile.getPhone());
        }

        return basicUserRepository.save(mysqlEntity);
    }
    
    private BasicUserEntity updateUserInfo(final ChangeUserInfoEvent event, final String docStatus,
            final UserEntity userEntity, final BasicUserEntity user) {
        user.setFullName(userEntity.getName());
        user.setEmail(userEntity.getEmail());
        user.setAvatar(userEntity.getPicture());
        user.setActivated(userEntity.getActivated());
        if (!Objects.isNull(userEntity.getStatus())) {
            user.setDocStatus(userEntity.getStatus().documentStatus());
        }
        user.setFullName(userEntity.getName());
        user.setUserName(userEntity.getUsername());
        user.setUserType(userEntity.getUserType().toString());
        user.setUuid(userEntity.getUuid());
        if (!Objects.isNull(userEntity.getProfile())) {
            user.setCity(userEntity.getProfile().getCity());
            user.setCountry(userEntity.getProfile().getCountry());
            user.setPhone(userEntity.getProfile().getPhone());
            user.setGender(userEntity.getProfile().getGender().ordinal());
            user.setNumberOfRate(userEntity.getProfile().getNumberOfRate() == null ? 0 : userEntity.getProfile().getNumberOfRate());
            user.setTotalStar(userEntity.getProfile().getTotalRate() == null ? 0 : userEntity.getProfile().getTotalRate());
            user.setAverageStar(userEntity.getProfile().getRated() == null ? 0 : userEntity.getProfile().getRated());
        }
        if (StringUtils.isNotEmpty(docStatus)
                && DocumentStatusType.APPROVED.name().equals(docStatus)) {
            user.setDocApprovedDate(LocalDateTime.now());
            user.setLevel(event.getLevel().toString());
        }
        user.setEmailValidated(userEntity.getEmailvalidated());
        user.setUsercode(userEntity.getUsercode());
        user.setClubcode(userEntity.getClubcode());
        user.setEnableSubscribe(userEntity.getEnableSubscribe() == null ? false : userEntity.getEnableSubscribe());
        user.setExtendUserType(userEntity.getExtUserType() == null ? null : userEntity.getExtUserType().name());
        
        // Update user's skills
        this.updateUserSkills(user, userEntity);
        
        return user;
    }
    
    private void updateUserSkills(BasicUserEntity dbUser, UserEntity ugUser) {
    	Set<String> skillSet = ugUser.getProfile().getSkills();
			
    	if(skillSet != null) {
    		List<UserSkillsEntity> userSkillsEntities = new ArrayList<UserSkillsEntity>();
    		skillSet.forEach(skill -> {
    			UserSkillsEntity userSkillsEntity = new UserSkillsEntity();
    			userSkillsEntity.setSkillKey(skill);
    			userSkillsEntities.add(userSkillsEntity);
    		});
    			
    		dbUser.setSkills(userSkillsEntities);
    	}
    }

    private List<SessionHistoryEntity> cancelSessionsDeactivatedUser(final String userUuid,
            final UserType userType) {
        LOG.info("Cancel all booking session after deactivated user {} as {}", userUuid,
                userType.name());
        final List<SessionEntity> bookingSessions = sessionManagementService.getBookingSession(
                userUuid, userType);
        if (!bookingSessions.isEmpty()) {
            final List<SessionHistoryEntity> historyEntities = new ArrayList<>();
            final List<String> deletedEvents = new ArrayList<>();

            for (final SessionEntity session : bookingSessions) {
                final SessionHistoryEntity sessionHistoryEntity = new SessionHistoryEntity();
                sessionHistoryEntity.setSessionUuid(session.getUuid());
                sessionHistoryEntity.setUserUuid(session.getUserUuid());
                sessionHistoryEntity.setNewPackageUuid(session.getPackageUuid());
                sessionHistoryEntity.setOldPackageUuid(session.getPackageUuid());
                sessionHistoryEntity.setOldPtUuid(session.getPtUuid());
                sessionHistoryEntity.setNewPtUuid(session.getPtUuid());
                sessionHistoryEntity.setOldStatus(session.getStatus());
                sessionHistoryEntity.setNewStatus(SessionStatus.OPEN);
                sessionHistoryEntity.setAction(SessionAction.DEACTIVATED);

                session.setLastStatus(session.getStatus());
                session.setLastStatusModifiedDate(session.getStatusModifiedDate());

                session.setStatus(SessionStatus.OPEN);
                session.setStatusModifiedDate(LocalDateTime.now());

                deletedEvents.add(session.getPtEventId());
                deletedEvents.add(session.getUserEventId());

                historyEntities.add(sessionHistoryEntity);
            }

            sessionRepository.save(bookingSessions);

            if (!deletedEvents.isEmpty()) {
                eventManagementService.deleteEvents(deletedEvents);
            }

            final List<SessionHistoryEntity> savedSessions = sessionHistoryManagementService
                    .save(historyEntities);

            if (!savedSessions.isEmpty()) {
                LOG.info("Finished to update {} after deactivated user {}", savedSessions.size(),
                        userUuid);
                return savedSessions;
            }
        }

        return Collections.emptyList();
    }
    
    public void synchronizedContractForUser(final String userUuid, final String userCode, final String userType) {
		LOG.info("-----------------------Compare userType: {} and comparison{} ", userType ,(userType.equalsIgnoreCase(UserType.EU.toString())));
		//Set flag true for user or pt had imported
		if(userType.equalsIgnoreCase(UserType.EU.toString())) {
			final List<MemberUploadEntity> updatedMemberUsageList =  memberUploadRepository.getMemberByMemberBarCode(userCode);
			LOG.info("-----------------------updatedMemberUsageList size {}  ", updatedMemberUsageList.size());
			if(!CollectionUtils.isEmpty(updatedMemberUsageList)) {
				final MemberUploadEntity entity = updatedMemberUsageList.get(0);
				entity.setUserCodechecked(true);
				memberUploadRepository.save(entity);
			}
		}
		else if(userType.equalsIgnoreCase(UserType.PT.toString())) {
			final List<TrainerUploadEntity> updateTrainerUsageList = trainerUploadRepository.getTrainerByTrainerCode(userCode);
			if(!CollectionUtils.isEmpty(updateTrainerUsageList)) {
				final TrainerUploadEntity entity = updateTrainerUsageList.get(0);
				entity.setUserCodechecked(true);
				trainerUploadRepository.save(entity);
			}
		}
		//Create free order for users or pts
		migrationUserService.createFreeOrderRequest(userUuid, userCode, userType);
    }
    
    
    public void updateOldUserCodeStatus(final String oldUserCode){
    	final List<MemberUploadEntity> updatedMemberUsageList =  memberUploadRepository.getMemberByMemberBarCode(oldUserCode);
		
		if(!CollectionUtils.isEmpty(updatedMemberUsageList)) {
			LOG.info("--------------***-------update old member code  ", oldUserCode);
			final MemberUploadEntity entity = updatedMemberUsageList.get(0);
			entity.setUserCodechecked(false);
			memberUploadRepository.save(entity);
		}
		
		final List<TrainerUploadEntity> updateTrainerUsageList = trainerUploadRepository.getTrainerByTrainerCode(oldUserCode);
		if(!CollectionUtils.isEmpty(updateTrainerUsageList)) {
			LOG.info("--------------***-------update traner  member code  ", oldUserCode);
			final TrainerUploadEntity entity = updateTrainerUsageList.get(0);
			entity.setUserCodechecked(false);
			trainerUploadRepository.save(entity);
		}
    }
    
    
	public void updateSessionUserTableNewUserCode(final String newUserCode, final String oldUserCode) {

		final List<BasicUserEntity> userEntities = basicUserRepository.getUserByUserCode(oldUserCode);

		for (final BasicUserEntity userEntitie : userEntities) {
			LOG.info("--- *** update session user *** --- new user code {} , old user code {}",newUserCode,oldUserCode);
			userEntitie.setUsercode(newUserCode);
			basicUserRepository.save(userEntitie);
		}

	}
    

}
